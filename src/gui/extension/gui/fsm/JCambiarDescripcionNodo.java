package extension.gui.fsm;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

class JCambiarDescripcionNodo{
	
	private static JFrame ventanaEditDescription;
	private static JTextField resultado;
	private static JButton botonAceptar;
	private static JPanel formulario;
	private static Action enterAction;
	private static ButtonListener buttonListener;
    
	static private JLabel iconLabel = null;
	static private JBehaviorNode behaviorNode = null;
	static private JFSMPanel fsmPanel;
    
	JCambiarDescripcionNodo( JBehaviorNode behavior , JLabel iconlabel, JFSMPanel fsmpanel ){
    	
		behaviorNode = behavior;
		iconLabel = iconlabel;
		fsmPanel = fsmpanel;
        
		ventanaEditDescription = new JFrame("Cambiar Nombre del Comportamiento");
		ventanaEditDescription.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		ventanaEditDescription.getContentPane().add( hacerFormulario(), BorderLayout.NORTH );
		JPanel p2 = new JPanel();
		p2.add(botonAceptar);
		ventanaEditDescription.getContentPane().add(p2, BorderLayout.SOUTH);
		ventanaEditDescription.pack();
		
		// variables para centrar la ventana en la pantalla y definir tamaño ventana...
		Dimension tamanioPantalla = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension tamanioVentana = new Dimension( 400,150 );
		// ubico la ventana en el centro de la pantalla...
		ventanaEditDescription.setSize( tamanioVentana );
		ventanaEditDescription.setResizable( true );
		ventanaEditDescription.setLocation(((tamanioPantalla.width - tamanioVentana.width)/2), (tamanioPantalla.height - tamanioVentana.height)/2);
		
		ventanaEditDescription.setVisible(true);
    }
    
    private static JPanel hacerFormulario(){
		formulario = new JPanel( new GridLayout( 2,2 ) );
		
		resultado = new JTextField();
		resultado.setColumns(15);
		resultado.setText( behaviorNode.getBehavior().getDescripcion() );
		
		JLabel lab2 = new JLabel( "Nombre actual: " , JLabel.RIGHT );
		formulario.add(lab2);
		lab2 = new JLabel( behaviorNode.getBehavior().getDescripcion() , JLabel.LEFT );
		formulario.add(lab2);
		
		JLabel lab = new JLabel( "Cambiar por: " , JLabel.RIGHT );
		lab.setLabelFor( resultado );
		formulario.add(lab);
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		p.add( resultado );
		formulario.add(p);
		
		buttonListener = new ButtonListener();
		botonAceptar = new JButton( "Aceptar" );
		botonAceptar.addActionListener( buttonListener ); 
		    
		enterAction = new EnterAction(); // defines an AbstractAction item that will program the action to occur when the enter key is pressed
		resultado.getInputMap().put( KeyStroke.getKeyStroke( "ENTER" ), "doEnterAction" ); // the following two lines do the magic of key binding. the first line gets the resultado's InputMap and pairs the "ENTER" key to the action "doEnterAction"...
		resultado.getActionMap().put( "doEnterAction", enterAction ); // ...then this line pairs the AbstractAction enterAction to the action "doEnterAction".
		 
		return formulario;
    }
     
    private static class EnterAction extends AbstractAction{ // class EnterAction is an AbstractAction that defines what will occur when the enter key is pressed....
		private static final long serialVersionUID = 1L;
		public void actionPerformed( ActionEvent tf ) {
			// System.out.println( "The Enter key has been pressed." );
			botonAceptar.doClick(); // pressing the enter key then 'presses' the enter button by calling the button's doClick() method...
		}
    }
    
    private static class ButtonListener implements ActionListener{ // class ButtonListener defines the action to occur when the enter button is pressed...
    	public void actionPerformed( ActionEvent bp ){
    		// System.out.println( "The enter button was pressed." );
    		behaviorNode.getBehavior().setDescription( resultado.getText() ); // tomo el valor 2do del array, el nuevo nombre...
    		ventanaEditDescription.dispose();
    		// saco el icono del layer y lo vuelvo a agregar con la modificación!!! 
    		behaviorNode.remove( iconLabel );
    		iconLabel.setText( behaviorNode.getBehavior().getDescripcion() );
    		behaviorNode.add( iconLabel, JLayeredPane.DEFAULT_LAYER);
    		// esto hace falta para que el JPanel actualice y dibuje los hijos agregados en tiempo de ejecucion 
    		behaviorNode.revalidate(); // JNode.revalidate();
    		fsmPanel.repaint(); // JNode.FSMPanel.repaint();
    	}
    }
}


