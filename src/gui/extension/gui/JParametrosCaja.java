package extension.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.Point;

import extension.model.Box;
import extension.model.Panel;
import extension.model.Program;
import extension.model.FunctionBox;

/*
 * Aplicación que permite configurar los parametros de las funciones
 */

public class JParametrosCaja extends JPanel{
	
	// instancias para modificar los valores de las funciones en el programa...	
    private Program programa;
    private Box box;
    private Point puntoARet = new Point();
    private Point puntoBRet = new Point();
	// valores para normalización de valores...
	static int minX =    0;
	static int maxX =  100;
	static int minY = -100;
	static int maxY =  100;
	// seteo variables de tamaños de las cosas... 
	static int anchoVentana = 500;	// el mínimo es 300, y tiene que ser múltiplo de 100, si no no anda...
	static int altoVentana = 500;	// el mínimo es 300, y tiene que ser múltiplo de 100, si no no anda...
	static int margenVentana = 50;	// 100
	static int margenGrilla1 = 53;	// 103
	static int margenGrilla2 = 47;	// 97
	static int centroGrillaVertical = (((altoVentana-(2*margenVentana))/2)+margenVentana);
	static int grosorLinea = 4;
	static int grosorPunto = 8;
	static int grosorPunto2 = 1;
	Color colorLinea = Color.black;
	Color colorPunto = Color.red;
	Color colorTexto = Color.black;
	// seteo posiciones iniciales de los 4 puntos...
	Ellipse2D.Double punto1 = new Ellipse2D.Double(margenVentana, (((altoVentana-(2*margenVentana))/2)+margenVentana), grosorPunto, grosorPunto); // este se mueve pero es invisible...
	Ellipse2D.Double punto4 = new Ellipse2D.Double(anchoVentana-margenVentana, punto1.y, grosorPunto, grosorPunto); // este se mueve pero es invisible...
	Ellipse2D.Double punto2 = new Ellipse2D.Double(punto1.x+((punto4.x-punto1.x)/3), punto1.y, grosorPunto, grosorPunto); // el 1ro de los que se mueven horizontalmente
    Ellipse2D.Double punto3 = new Ellipse2D.Double(punto2.x+((punto4.x-punto1.x)/3), punto1.y, grosorPunto, grosorPunto); // el 2do de los que se mueven horizontalmente
	// seteo posicion inicial de la lineaMovible1 y lineaMovible3 	
    Rectangle2D.Double lineaMovible1 = new Rectangle2D.Double(punto1.x+2, (punto1.y+(grosorLinea/2)), (punto2.x-punto1.x), grosorLinea); // esta es la lineaMovible1
    Rectangle2D.Double lineaMovible3 = new Rectangle2D.Double(punto3.x, (punto3.y+(grosorLinea/2)), (punto4.x-punto3.x)+2, grosorLinea); // esta es la lineaMovible3
    // seteo las líneas "invisibles" que me sirven para unir los puntos...
	Line2D.Double linea1 = new Line2D.Double();
    Line2D.Double linea2 = new Line2D.Double();
    Line2D.Double linea3 = new Line2D.Double();
    // para que me una los puntos la 1ra vez...
    boolean primeraVez = true;
    // para las fuentes...
    Font oldFont = getFont();
    Font newFont = new Font("Monospaced", Font.BOLD, oldFont.getSize());
    // creo la ventana
    JFrame frameVentana = new JFrame("Modificación de Parámetros de Función");

    
	public JParametrosCaja( Point puntoA, Point puntoB, Program programa, Box box ){
		//	obtengo las referencias a la función y al programa...		
		this.programa = programa;
    	this.box = box;	
		// normalizo valores y seteo los puntos según los recibidos
		punto2 = desnormalizarPunto(puntoA);
		punto3 = desnormalizarPunto(puntoB);
		punto1.setFrame(margenVentana, punto2.y, punto1.width, punto1.height);
		punto4.setFrame(anchoVentana-margenVentana, punto3.y, punto4.width, punto4.height);
		// seteo el Listener de Mouse
        this.addMouseListener(this.mouseInputAdapter);			// le seteo el Listener de Mouse
        this.addMouseMotionListener(this.mouseInputAdapter);	// le seteo el Listener de Mouse
        // variables auxiliares para posicion de ventana
        Dimension tamanioPantalla, tamanioVentana;							
        // seteo el DefaultCloseOperation de la ventana (el botoncito X de la ventana)
		// los valores posibles son: EXIT_ON_CLOSE, DO_NOTHING_ON_CLOSE y DISPOSE_ON_CLOSE.
		// el EXIT_ON_CLOSE hace que se cierre toda la aplicación...
		// el DO_NOTHING_ON_CLOSE no hace nada...
		// el DISPOSE_ON_CLOSE hace que se como un "dispose()", se desaparece la instancia...        
        frameVentana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // seteo el tamaño ventana...
        frameVentana.setSize(anchoVentana,altoVentana+100);
        // creo los botones aceptar y cancelar
    	JButton aceptar = new JButton("Aceptar");
    	JButton cancelar = new JButton("Cancelar");
    	// le agrego la acción al botón cancelar
		aceptar.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				actionBotonCerrarAceptar();
			}
		});    	
    	// le agrego la acción al botón cancelar
		cancelar.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				actionBotonCerrarCancelar();
			}
		});    	
    	//CREAMOS UN PANEL PARA COLOCAR LOS BOTONES
    	JPanel botones = new JPanel();
    	botones.add("North", aceptar);
    	botones.add("South", cancelar);
    	// ARMO LOS DOS PANELES (APLICACION + BOTONES) !!!
        JPanel panelPrincipal = new JPanel();
        // panel principal
        panelPrincipal.setLayout( new GridBagLayout() );
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1; // 100%
        // panel superior (aplicacion)
        c.weighty = 1.0; // 100%
        c.gridx = 0;
        c.gridy = 0; // posiscion (0,0) de la tabla
        panelPrincipal.add( this, c );
        // panel inferior (botones) 
        c.weighty = 0.0; // 0%
        c.gridx = 0;
        c.gridy = 1; // posiscion (0,1) de la tabla
        panelPrincipal.add( botones, c );
        // AGREGO EL PANEL PARTIDO A LA VENTANA 
        frameVentana.add( panelPrincipal );
        // calculo para ver donde poner la ventana en pantalla...
        tamanioPantalla = Toolkit.getDefaultToolkit().getScreenSize();
		tamanioVentana = frameVentana.getSize();
		// coloco ventana
		frameVentana.setLocation(((tamanioPantalla.width - tamanioVentana.width)/2), (tamanioPantalla.height - tamanioVentana.height)/2);
		// hago visible la ventana
		frameVentana.setVisible(true);
	}    

	
	public void run(){
        Graphics g = this.getGraphics();
        g.dispose();
	}

	
	public void actionBotonCerrarCancelar(){
		frameVentana.dispose();
	}

	public void actionBotonCerrarAceptar(){
		// seteo los valores de los puntos de la funcion llamando a "setBox"...
		this.puntoARet.x = (int)normalizarPunto(punto2).x;
		this.puntoARet.y = (int)normalizarPunto(punto2).y;
		this.puntoBRet.x = (int)normalizarPunto(punto3).x;
		this.puntoBRet.y = (int)normalizarPunto(punto3).y;
		this.programa.setBox( this.box, puntoARet, puntoBRet );
		frameVentana.dispose();
	}

	
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        // para conectar los puntos la primera vez...
        if(primeraVez){
        	conectarPuntos();
        	primeraVez = false;
        }
        // DIBUJO LA GRILLA
        g2.setPaint(Color.gray);
        for (int i=0; i <= altoVentana-2*margenGrilla2; i+=10){
        	g2.drawLine(margenGrilla1, margenGrilla1+i, anchoVentana-margenGrilla2, margenGrilla1+i);
            g2.drawLine(margenGrilla1+i, margenGrilla1, margenGrilla1+i, altoVentana-margenGrilla2);
        }
        for (int i=310; i <= anchoVentana-2*margenGrilla2; i+=10){
            g2.drawLine(margenGrilla1+i, margenGrilla1, margenGrilla1+i, altoVentana-margenGrilla2);
        }
        // DIBUJO EL EJE DE COORDERANAS
    	g2.drawLine(margenGrilla1, centroGrillaVertical+1, anchoVentana-margenGrilla2, centroGrillaVertical+1);
    	g2.drawLine(margenGrilla1, centroGrillaVertical+2, anchoVentana-margenGrilla2, centroGrillaVertical+2);
    	g2.drawLine(margenGrilla1, centroGrillaVertical+4, anchoVentana-margenGrilla2, centroGrillaVertical+4);
    	g2.drawLine(margenGrilla1, centroGrillaVertical+5, anchoVentana-margenGrilla2, centroGrillaVertical+5);
    	g2.drawLine(margenGrilla1-1, margenGrilla1, margenGrilla1-1, altoVentana-margenGrilla2);
        g2.drawLine(margenGrilla1+1, margenGrilla1, margenGrilla1+1, altoVentana-margenGrilla2);
        g2.drawLine(margenGrilla1+2, margenGrilla1, margenGrilla1+2, altoVentana-margenGrilla2);
        // DIBUJO LOS PUNTOS Y LAS LINEAS
        g2.setPaint(colorLinea);			// seteo color linea
        g2.draw(lineaMovible1);				// dibujo lineaMovible1
        g2.fill(lineaMovible1);				// relleno lineaMovible1    
        g2.draw(lineaMovible3);				// dibujo lineaMovible3
        g2.fill(lineaMovible3);				// relleno lineaMovible3    
        g2.setPaint(colorPunto);			// seteo color Punto2
        g2.draw(punto2);					// dibujo Punto2
        g2.fill(punto2);					// relleno Punto2
        g2.draw(punto3);					// dibujo Punto3
        g2.fill(punto3);					// relleno Punto3        
        dibujarLinea(g2, (int)linea2.x1, (int)linea2.y1, (int)linea2.x2, (int)linea2.y2, grosorLinea, colorLinea);	// seteo color Linea2 y dibujo Linea2
        // DIBUJO LOS TEXTOS LOS EJES...
        g2.setFont(oldFont);
        g2.setPaint(colorTexto);			// seteo color de los textos
        g2.drawString(maxY+"%", margenVentana-10, margenVentana-5);         
        g2.drawString(minY+"%", margenVentana-10, altoVentana-margenVentana+20);         
        g2.drawString(minX+"%", margenVentana-18, centroGrillaVertical+7);         
        g2.drawString(maxX+"%", anchoVentana-margenVentana+10, centroGrillaVertical+7);         
        // DIBUJO LOS TEXTOS DE LOS VALORES DE LOS PUNTOS...        
        g2.setFont(newFont);
        g2.drawString("Punto A = (" + (int)normalizarPunto(punto2).x + "," + (int)normalizarPunto(punto2).y + ")", (anchoVentana-2*margenVentana)/2, altoVentana-20);         
        g2.drawString("Punto B = (" + (int)normalizarPunto(punto3).x + "," + (int)normalizarPunto(punto3).y + ")", (anchoVentana-2*margenVentana)/2, altoVentana- 5);         
    }

    
    private Ellipse2D.Double normalizarPunto( Ellipse2D.Double punto ){
    	// obtengo valores relativos actuales del punto
    	int xActual = (int)(punto.x-margenVentana);
    	int yActual = (int)((((altoVentana-(2*margenVentana))/2)+margenVentana)-punto.y);
    	// calculo los topes relativos reales...
    	int xMaxDesnormalizado = anchoVentana-2*margenVentana;
    	int yMaxDesnormalizado = (altoVentana-2*margenVentana)/2;
    	// calculo nuevo valores normalizados
    	int xNormalizado = maxX*xActual/xMaxDesnormalizado;
    	int yNormalizado = maxY*yActual/yMaxDesnormalizado;
    	// saturo por si las dudas...
    	if( xNormalizado > maxX ) xNormalizado = maxX;
    	if( xNormalizado < minX ) xNormalizado = minX;    		
    	if( yNormalizado > maxY ) yNormalizado = maxY;
    	if( yNormalizado < minY ) yNormalizado = minY;    		
    	// seteo el punto a devolver
    	Ellipse2D.Double puntoNormalizado = new Ellipse2D.Double(xNormalizado, yNormalizado, grosorPunto, grosorPunto);
    	return puntoNormalizado;
    }

    
    private Ellipse2D.Double desnormalizarPunto( Point punto ){
    	// obtengo valores absolutos actuales del punto
    	int xActual = (int)(punto.x);
    	int yActual = (int)(punto.y);
    	// calculo nuevo valores desnormalizados
    	int xDesnormalizado = margenVentana+((anchoVentana-2*margenVentana)*xActual/maxX); 
    	int yDesnormalizado = (altoVentana/2)-(((altoVentana-2*margenVentana)/2)*yActual/maxY);
    	// saturo por si las dudas...
    	if( xDesnormalizado > (anchoVentana-margenVentana) ) 	xDesnormalizado = anchoVentana-margenVentana;
    	if( xDesnormalizado < margenVentana ) 					xDesnormalizado = margenVentana;    		
    	if( yDesnormalizado > altoVentana-margenVentana ) 		yDesnormalizado = altoVentana-margenVentana;
    	if( yDesnormalizado < margenVentana ) 					yDesnormalizado = margenVentana;    		
    	// seteo el punto a devolver
    	Ellipse2D.Double puntoDesormalizado = new Ellipse2D.Double(xDesnormalizado, yDesnormalizado, grosorPunto, grosorPunto);
    	return puntoDesormalizado;
    }
	
    
    private void conectarPuntos(){
        double flatness = 0.01;
        PathIterator path_it = punto1.getPathIterator(null, flatness);
        double[] coordenadas = new double[2];
        double x1 = 0, y1 = 0, x2 = 0, y2 = 0;
        double min = Double.MAX_VALUE;
        while(!path_it.isDone()) {
            int type = path_it.currentSegment(coordenadas);
            switch(type) {
                case PathIterator.SEG_MOVETO:
                case PathIterator.SEG_LINETO:
                    Point2D.Double p = obtenerPuntoMasCercano(punto2, coordenadas[0], coordenadas[1]);
                    double dist = p.distance(coordenadas[0], coordenadas[1]);
                    if(dist < min) {
                        min = dist;
                        x1 = coordenadas[0];
                        y1 = coordenadas[1];
                        x2 = p.x;
                        y2 = p.y;
                    }
                    break;
                case PathIterator.SEG_CLOSE:
                    break;
                default:
                    System.out.println("punto1 type: " + type);
            }
            path_it.next();
        }
        linea1.setLine(x1, y1, x2, y2);

        flatness = 0.01;
        path_it = punto2.getPathIterator(null, flatness);
        x1 = 0; y1 = 0; x2 = 0; y2 = 0;
        min = Double.MAX_VALUE;
        while(!path_it.isDone()) {
            int type = path_it.currentSegment(coordenadas);
            switch(type) {
                case PathIterator.SEG_MOVETO:
                case PathIterator.SEG_LINETO:
                    Point2D.Double p = obtenerPuntoMasCercano(punto3, coordenadas[0], coordenadas[1]);
                    double dist = p.distance(coordenadas[0], coordenadas[1]);
                    if(dist < min) {
                        min = dist;
                        x1 = coordenadas[0];
                        y1 = coordenadas[1];
                        x2 = p.x;
                        y2 = p.y;
                    }
                    break;
                case PathIterator.SEG_CLOSE:
                    break;
                default:
                    System.out.println("punto2 type: " + type);
            }
            path_it.next();
        }
        linea2.setLine(x1, y1, x2, y2);

        flatness = 0.01;
        path_it = punto3.getPathIterator(null, flatness);
        x1 = 0; y1 = 0; x2 = 0; y2 = 0;
        min = Double.MAX_VALUE;
        while(!path_it.isDone()) {
            int type = path_it.currentSegment(coordenadas);
            switch(type) {
                case PathIterator.SEG_MOVETO:
                case PathIterator.SEG_LINETO:
                    Point2D.Double p = obtenerPuntoMasCercano(punto4, coordenadas[0], coordenadas[1]);
                    double dist = p.distance(coordenadas[0], coordenadas[1]);
                    if(dist < min) {
                        min = dist;
                        x1 = coordenadas[0];
                        y1 = coordenadas[1];
                        x2 = p.x;
                        y2 = p.y;
                    }
                    break;
                case PathIterator.SEG_CLOSE:
                    break;
                default:
                    System.out.println("punto3 type: " + type);
            }
            path_it.next();
        }
        linea3.setLine(x1, y1, x2, y2);

        // seteo coordenadas de la lineaMovible1 y lineaMovible3
        lineaMovible1.setFrame(punto1.x+2, (punto1.y+(grosorLinea/2)), (punto2.x-punto1.x), grosorLinea); // esta es la lineaMovible1
        lineaMovible3.setFrame(punto3.x, (punto3.y+(grosorLinea/2)), (punto4.x-punto3.x)+2, grosorLinea); // esta es la lineaMovible3
    }
 
    
    private Point2D.Double obtenerPuntoMasCercano(Ellipse2D.Double punto, double x, double y) {
        double flatness = 0.01;
        PathIterator path_it = punto.getPathIterator(null, flatness);
        double[] coordenadas = new double[2];
        Point2D.Double p = new Point2D.Double();
        double min = Double.MAX_VALUE;
        while(!path_it.isDone()) {
            int type = path_it.currentSegment(coordenadas);
            switch(type) {
                case PathIterator.SEG_MOVETO:
                case PathIterator.SEG_LINETO:
                    double dist = Point2D.distance(x, y, coordenadas[0], coordenadas[1]);
                    if(dist < min) {
                        min = dist;
                        p.setLocation(coordenadas[0], coordenadas[1]);
                    }
                    break;
                case PathIterator.SEG_CLOSE:
                    break;
                default:
                    System.out.println("punto type: " + type);
            }
            path_it.next();
        }
        return p;
    }
 
	public static void dibujarLinea(Graphics g, int x1, int y1, int x2, int y2, int thickness, Color c){
		// The thick line is in fact a filled polygon
		g.setColor(c);
		int dX = x2 - x1;
		int dY = y2 - y1;
		// line length
		double lineLength = Math.sqrt(dX * dX + dY * dY);
		double scale = (double)(thickness) / (2 * lineLength);
		// The x,y increments from an endpoint needed to create a rectangle...
		double ddx = -scale * (double)dY;
		double ddy = scale * (double)dX;
		ddx += (ddx > 0) ? 0.5 : -0.5;
		ddy += (ddy > 0) ? 0.5 : -0.5;
		int dx = (int)ddx;
		int dy = (int)ddy;
		// Now we can compute the corner points...
		int xPoints[] = new int[4];
		int yPoints[] = new int[4];
		xPoints[0] = x1 + dx; yPoints[0] = y1 + dy;
		xPoints[1] = x1 - dx; yPoints[1] = y1 - dy;
		xPoints[2] = x2 - dx; yPoints[2] = y2 - dy;
		xPoints[3] = x2 + dx; yPoints[3] = y2 + dy;
		g.fillPolygon(xPoints, yPoints, 4);
	}


	private MouseInputAdapter mouseInputAdapter = new MouseInputAdapter(){
        Point2D.Double offset = new Point2D.Double();
        boolean draggingPunto2 = false;
        boolean draggingPunto3 = false;
        boolean dragginglineaMovible1 = false;
        boolean dragginglineaMovible3 = false;
 
        public void mousePressed(MouseEvent e){
            Point p = e.getPoint();
            if(punto3.contains(p)){
                offset.x = p.x - punto3.x;
                draggingPunto3 = true;
            }
            else if(punto2.contains(p)){
	                offset.x = p.x - punto2.x;
	                draggingPunto2 = true;
            	}
            	else if(lineaMovible1.contains(p)){
		                offset.x = p.x - lineaMovible1.x;
		                offset.y = p.y - lineaMovible1.y;
		                dragginglineaMovible1 = true;
		        	}
	            	else if(lineaMovible3.contains(p)){
			                offset.x = p.x - lineaMovible3.x;
			                offset.y = p.y - lineaMovible3.y;
			                dragginglineaMovible3 = true;
			        	}
        }
 
        public void mouseReleased(MouseEvent e){
        	draggingPunto2 = false;
        	draggingPunto3 = false;
        	dragginglineaMovible1 = false;
        	dragginglineaMovible3 = false;
        }
 
        public void mouseDragged(MouseEvent e){
            if(draggingPunto3){
                double x = e.getX() - offset.x;
                if( (x>(punto2.x+grosorPunto)) & (x<=(punto4.x+1)) ){
	                punto3.setFrame(x, punto3.y, punto3.width, punto3.height);
	                conectarPuntos();
	                repaint();
                }
            }
            else if(draggingPunto2){
	                double x = e.getX() - offset.x;
	                if( (x>punto1.x) & (x<(punto3.x-grosorPunto)) ){
		                punto2.setFrame(x, punto2.y, punto2.width, punto2.height);
		                conectarPuntos();
		                repaint();
	                }
            	}
	            else if(dragginglineaMovible1){
		                double y = e.getY() - offset.y;
		                if( (y>=margenVentana) & (y<=(altoVentana-margenVentana)) ){
		                	punto1.setFrame(punto1.x, y, punto1.width, punto1.height);
			                punto2.setFrame(punto2.x, y, punto2.width, punto2.height);
			                lineaMovible1.setFrame(punto1.x+2, (punto1.y+(grosorLinea/2)), (punto2.x-punto1.x), grosorLinea); // esta es la lineaMovible1
			                conectarPuntos();
			                repaint();
		                }
		        	}
		            else if(dragginglineaMovible3){
			                double y = e.getY() - offset.y;
			                if( (y>=margenVentana) & (y<=(altoVentana-margenVentana)) ){
				                punto3.setFrame(punto3.x, y, punto3.width, punto3.height);
				                punto4.setFrame(punto4.x, y, punto4.width, punto4.height);
				                lineaMovible3.setFrame(punto3.x, (punto3.y+(grosorLinea/2)), (punto4.x-punto3.x)+2, grosorLinea); // esta es la lineaMovible3
				                conectarPuntos();
				                repaint();
			                }
			        	}
        }
    };
    
    
}