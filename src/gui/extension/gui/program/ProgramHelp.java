package extension.gui.program;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class ProgramHelp extends JFrame implements MouseListener {
	
	private JPanel contenidoPanel = null;
	private JPanel contenidoTitulo = null;
	
	public ProgramHelp(){
		
		super("ERBPI.Comportamiento.Help");

		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.getContentPane().setBackground(Color.DARK_GRAY);
		this.addMouseListener(this);

		this.contenidoPanel = contenidoHelp();
		this.contenidoTitulo = contenidoTitulo();
		this.getContentPane().add( contenidoTitulo, BorderLayout.NORTH );
		
		// variables para centrar la ventana en la pantalla y definir tamaño ventana...
		Dimension tamanioPantalla = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension tamanioVentana = new Dimension( 600,600 );
		// ubico la ventana en el centro de la pantalla...
		this.setSize( tamanioVentana );
		this.setResizable( false );
		this.setLocation(((tamanioPantalla.width - tamanioVentana.width)/2), (tamanioPantalla.height - tamanioVentana.height)/2);
		
		// Set vertical and horizontal scrollbar...  
		final JScrollPane scrollBar = new JScrollPane( contenidoPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
		javax.swing.SwingUtilities.invokeLater( new Runnable(){ // le seteo varias cosas
			   public void run(){ 
				   scrollBar.getVerticalScrollBar().setValue(0); // que el scroll arranque arriba...
				   scrollBar.getVerticalScrollBar().setUnitIncrement(16); // que scrollee de a varias lineas...
			   }
		});
		scrollBar.addMouseListener(this); // para que también se cierre con el click...
		this.add(scrollBar);
		
		this.setVisible(true);
	}

	 
	@Override
	public void mouseClicked(MouseEvent e){ dispose(); }
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {	}

    static void agregarTitulo( JPanel titulo ){
		JLabel lab1 = new JLabel( "ERBPI - Configurar Comportamiento Nodo" , JLabel.CENTER );
		JLabel lab2 = new JLabel( "Laboratorio de Robótica y Sistemas Embebidos - DC.UBA.AR" , JLabel.CENTER );
		JLabel lab3 = new JLabel( "AYUDA" , JLabel.CENTER );
		lab1.setForeground(Color.white);
		lab1.setAlignmentY(TOP_ALIGNMENT);
		lab2.setForeground(Color.white);
		lab2.setAlignmentY(TOP_ALIGNMENT);
		lab3.setForeground(Color.white);
		lab3.setAlignmentY(TOP_ALIGNMENT);
		titulo.add(lab1);
		titulo.add(lab2);
		titulo.add(lab3);
    }

    static void agregarItemIzquierdo( String texto, JPanel formulario ){
		JLabel lab = new JLabel( texto , JLabel.RIGHT );
		lab.setForeground(Color.white);
		lab.setVerticalAlignment(SwingConstants.TOP);
		formulario.add(lab);
    }

    static void agregarItemDerecho( String texto, JPanel formulario ){
		JTextArea lab = new JTextArea( texto );
		lab.setWrapStyleWord(true);
		lab.setLineWrap(true);
		lab.setEditable(false);
		lab.setAlignmentY(TOP_ALIGNMENT);
		lab.setForeground(Color.white);
		lab.setBackground( Color.DARK_GRAY );
		formulario.add(lab);
    }

    static JPanel contenidoTitulo(){
    	JPanel titulo = new JPanel( new GridLayout( 3 , 1 , 10 /*hgap*/ , 20 /*vgap*/ ) );
		titulo.setBackground(Color.DARK_GRAY);
		agregarTitulo( titulo );
		return titulo;
    }
    
	static JPanel contenidoHelp(){
    	JPanel formulario = new JPanel( new GridLayout( 6 , 2 , 10 /*hgap*/ , 10 /*vgap*/ ) );
		formulario.setBackground(Color.DARK_GRAY);
		
		agregarItemIzquierdo( "COMPLETAR 1: ", formulario );
		agregarItemDerecho( "Completar una descripción detallada de como usar bien esto...", formulario );
		
		agregarItemIzquierdo( "COMPLETAR 2: ", formulario );
		agregarItemDerecho( "Completar una descripción detallada de como usar bien esto...", formulario );

		agregarItemIzquierdo( "Botón Aceptar: ", formulario );
		agregarItemDerecho( "Guardar el comportamiento del Nodo y volver a la ventana anterior", formulario );

		agregarItemIzquierdo( "Botón Cancelar: ", formulario );
		agregarItemDerecho( "Cancelar y volver a la ventana anterior sin guardar.", formulario );

		agregarItemIzquierdo( "Abrir: ", formulario );
		agregarItemDerecho( "Ctrl+O.", formulario );

		agregarItemIzquierdo( "Guardar: ", formulario );
		agregarItemDerecho( "Ctrl+S.", formulario );

		return formulario;
    }
}
