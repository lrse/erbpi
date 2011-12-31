package extension.gui.program;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;

import extension.model.elements.FunctionBox;

/*
 * Aplicación que permite configurar los parametros de las funciones
 */

class JParametrosCajaEnergia extends JPanel
{
	private static final long serialVersionUID = 1L;
	
    private FunctionBox box;
    private Point puntoARet = new Point();
    private Point puntoBRet = new Point();
	// valores para normalización de valores...
	static int minX =    0;
	static int maxX =  100;
	static int minY = -100;
	static int maxY =  100;
	// seteo variables de tamaños de las cosas... 
	static int anchoVentana = 200;	// el mínimo es 200, y tiene que ser múltiplo de 100, si no no anda...
	static int altoVentana = 300;	// el mínimo es 300, y tiene que ser múltiplo de 100, si no no anda...
	static int margenVentana = 50;	// 100
	static int margenGrilla1 = 53;	// 103
	static int margenGrilla2 = 47;	// 97
	static int grosorLinea = 4;
	static int grosorPunto = 8;
	static int grosorPunto2 = 1;
	static int centroGrillaVertical = (((altoVentana-(2*margenVentana))/2)+margenVentana);
	static int centroGrillaHorizontal = (((anchoVentana-(2*margenVentana))/2)+margenVentana);
	static int centroGrillaHorizontal2 = centroGrillaHorizontal + (grosorPunto/2);
	Color colorLinea = Color.black;
	Color colorPunto = Color.red;
	Color colorTexto = Color.black;
	// seteo posicion inicial del punto...
	Ellipse2D.Double punto2 = new Ellipse2D.Double(margenVentana, centroGrillaHorizontal, grosorPunto, grosorPunto); // el 1ro de los que se mueven horizontalmente
    // para las fuentes...
    Font oldFont = getFont();
    Font newFont = new Font("Monospaced", Font.BOLD, oldFont.getSize());
    // creo la ventana
    JFrame frameVentana = new JFrame("Modificación de Parámetros de Energia");

    
	JParametrosCajaEnergia( FunctionBox box )
	{
    	this.box = box;	
		// normalizo valores y seteo los puntos según los recibidos
		punto2 = desnormalizarPunto(new Point(box.getX0(),box.getY0()));
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

	
	void run(){
        Graphics g = this.getGraphics();
        g.dispose();
	}

	
	private void actionBotonCerrarCancelar(){
		frameVentana.dispose();
	}

	private void actionBotonCerrarAceptar()
	{
		// seteo los valores de los puntos de la funcion llamando a "setBox"...
		this.puntoARet.x = minX;
		this.puntoARet.y = (int)normalizarPunto(punto2).y;
		this.puntoBRet.x = maxX;
		this.puntoBRet.y = (int)normalizarPunto(punto2).y;
		
		this.box.setX0(puntoARet.x);
		this.box.setY0(puntoARet.y);
		this.box.setX1(puntoBRet.x);
		this.box.setY1(puntoBRet.y);
		
		frameVentana.dispose();
	}

	
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        // DIBUJO LA GRILLA
        g2.setPaint(Color.gray);
        for (int i=0; i <= altoVentana-2*margenGrilla2; i+=10)
        	g2.drawLine(centroGrillaHorizontal2-7, margenGrilla1+i, centroGrillaHorizontal2+7, margenGrilla1+i);
        // DIBUJO EL EJE DE COORDERANAS
    	g2.drawLine(centroGrillaHorizontal2-7, centroGrillaVertical+1, centroGrillaHorizontal2+7, centroGrillaVertical+1);
    	g2.drawLine(centroGrillaHorizontal2-7, centroGrillaVertical+2, centroGrillaHorizontal2+7, centroGrillaVertical+2);
    	g2.drawLine(centroGrillaHorizontal2-7, centroGrillaVertical+4, centroGrillaHorizontal2+7, centroGrillaVertical+4);
    	g2.drawLine(centroGrillaHorizontal2-7, centroGrillaVertical+5, centroGrillaHorizontal2+7, centroGrillaVertical+5);
    	g2.drawLine(centroGrillaHorizontal2-1, margenGrilla1, centroGrillaHorizontal2-1, altoVentana-margenGrilla2);
    	g2.drawLine(centroGrillaHorizontal2+0, margenGrilla1, centroGrillaHorizontal2+0, altoVentana-margenGrilla2);
    	g2.drawLine(centroGrillaHorizontal2+1, margenGrilla1, centroGrillaHorizontal2+1, altoVentana-margenGrilla2);
        // DIBUJO EL PUNTO
        g2.setPaint(colorPunto);			// seteo color Punto2
        g2.draw(punto2);					// dibujo Punto2
        g2.fill(punto2);					// relleno Punto2
        // DIBUJO LOS TEXTOS LOS EJES...
        g2.setFont(oldFont);
        g2.setPaint(colorTexto);			// seteo color de los textos
        g2.drawString(maxY+"%", centroGrillaHorizontal2-11, margenVentana-5);         
        g2.drawString(minY+"%", centroGrillaHorizontal2-14, altoVentana-margenVentana+20);         
        g2.drawString(minX+"%", centroGrillaHorizontal2-28, centroGrillaVertical+7);         
        // DIBUJO LOS TEXTOS DE LOS VALORES DE LOS PUNTOS...        
        g2.setFont(newFont);
        g2.drawString("Valor = " + (int)normalizarPunto(punto2).y, ((anchoVentana-2*margenVentana)/2)+20, altoVentana-5);         
         
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
    	int yActual = (int)(punto.y);
    	// calculo nuevo valores desnormalizados
    	int xDesnormalizado = centroGrillaHorizontal; 
    	int yDesnormalizado = (altoVentana/2)-(((altoVentana-2*margenVentana)/2)*yActual/maxY);
    	// saturo por si las dudas...
    	if( yDesnormalizado > altoVentana-margenVentana ) 		yDesnormalizado = altoVentana-margenVentana;
    	if( yDesnormalizado < margenVentana ) 					yDesnormalizado = margenVentana;    		
    	// seteo el punto a devolver
    	Ellipse2D.Double puntoDesormalizado = new Ellipse2D.Double(xDesnormalizado, yDesnormalizado, grosorPunto, grosorPunto);
    	return puntoDesormalizado;
    }
    
	private MouseInputAdapter mouseInputAdapter = new MouseInputAdapter(){
        Point2D.Double offset = new Point2D.Double();
        boolean draggingPunto2 = false;
 
        public void mousePressed(MouseEvent e){
            Point p = e.getPoint();
            if(punto2.contains(p)){
                offset.x = p.x - punto2.x;
                offset.y = p.y - punto2.y;
                draggingPunto2 = true;
           	}
        }
 
        public void mouseReleased(MouseEvent e){
        	draggingPunto2 = false;
        }
 
		public void mouseDragged(MouseEvent e){
			if(draggingPunto2){
				double y = e.getY() - offset.y;
				if( (y >= margenVentana) & (y <= (altoVentana-margenVentana)) ){
					punto2.setFrame(punto2.x, y, punto2.width, punto2.height);
					repaint();
				}
			}
		}
    };
}