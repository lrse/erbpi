package extension.gui.program;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import thirdparty.dragnghost.GhostBoxMotionHandler;
import thirdparty.dragnghost.GhostGlassPane;
import thirdparty.dragnghost.NewGhostBoxHandler;
import extension.gui.JRoboticaFrame;
import extension.gui.layouts.VerticalFlowLayout;
import extension.model.BehaviorNode;
import extension.model.BehaviorProgram;
import extension.model.FunctionTemplate;
import extension.model.GlobalConfig;
import extension.utils.XmlUtils;

public class JProgramFrame extends JDialog implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	// modelo del comportamiento que representa la ventana
	private BehaviorNode comportamiento = null;
	private BehaviorProgram programTemp = null;
	
	// panel con las herramientas para armar el comportamiento
	private JPanel toolMenuPanel = null;
	
	// panel donde se va armando el comportamiento
	private JProgramPanel programPanel = null;

	// variables para centrar la ventana en la pantalla y definir tamaño ventana...
	private Dimension tamanioPantalla;
	private Dimension tamanioVentana = new Dimension( 800,600 );
	
	public JProgramFrame( JFrame owner, BehaviorNode comportamiento )
	{
		super(owner,"ERBPI - Comportamiento - Nodo: " + comportamiento.getDescripcion() );
		
		GhostGlassPane glassPane = new GhostGlassPane();
		this.setGlassPane(glassPane);
		
		this.comportamiento = comportamiento;
		// el "programTemp" lo uso para modificar el comportamiento, y si al final presiono ACEPTAR entonces piso a "program", si no, no se guarda...
		// hacemos una "deep copy" (en lugar de "shallow copy") con la implementacion del método CLONE
		this.programTemp = (BehaviorProgram)((BehaviorProgram)comportamiento.getProgram()).clone();

		this.getContentPane().setLayout(new GridBagLayout());
		this.getContentPane().setBackground(Color.DARK_GRAY);
		
		createRobotPanel();
		createProgramPanel();
		createToolMenu();
		createSystemMenu();
	
		this.setSize( tamanioVentana );
		
		// ubico la ventana en el centro de la pantalla...
        tamanioPantalla = Toolkit.getDefaultToolkit().getScreenSize();
		tamanioVentana = getSize();
		setLocation(((tamanioPantalla.width - tamanioVentana.width)/2), (tamanioPantalla.height - tamanioVentana.height)/2);
		
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent e ) {
				actionBotonCerrarCancelar();
		} } );
		
		this.setVisible(true);
	}
	
	private void createSystemMenu(){
		
		JMenuBar menuBar;
		
		//Create the menu bar.
		menuBar = new JMenuBar();
		
		// Create file menu
		JMenu fileMenu = new JMenu("Archivo");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(fileMenu);
		
		JMenuItem openMenuItem = new JMenuItem("Abrir comportamiento guardado...",KeyEvent.VK_O);
		openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		openMenuItem.getAccessibleContext().setAccessibleDescription("Open Behavior");
		openMenuItem.addActionListener(this);
		fileMenu.add(openMenuItem);
		
		JMenuItem saveMenuItem = new JMenuItem("Guardar comportamiento como...",KeyEvent.VK_S);
		saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		saveMenuItem.getAccessibleContext().setAccessibleDescription("Save Behavior");
		saveMenuItem.addActionListener(this);
		fileMenu.add(saveMenuItem);

		// Create help menu
		JMenu helpMenu = new JMenu("Ayuda");
		helpMenu.setMnemonic(KeyEvent.VK_H);
		menuBar.add(helpMenu);
		
		JMenuItem helpMenuItem = new JMenuItem("Acerca de...",KeyEvent.VK_F1);
		helpMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		helpMenuItem.getAccessibleContext().setAccessibleDescription("Open help");
		helpMenuItem.addActionListener(this);
		helpMenu.add(helpMenuItem);
		
		setJMenuBar(menuBar);
	}
	
	private void createToolMenu()
	{
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(0, 5, 5, 5);
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.weighty = 1.0;
		
		toolMenuPanel = new JPanel();
		//toolMenuPanel.setLayout(new BoxLayout(toolMenuPanel,BoxLayout.Y_AXIS));
		//toolMenuPanel.setLayout(new FlowLayout());
		toolMenuPanel.setLayout(new VerticalFlowLayout(5,VerticalFlowLayout.CENTER,VerticalFlowLayout.CENTER));
		toolMenuPanel.setBackground(Color.LIGHT_GRAY);

		for (FunctionTemplate functionTemplate : GlobalConfig.getCurrentConfig().getFunctionTemplates())
		{
			// creo el boton que genera nuevos boxes para cierta funcion
			JNewBoxButton newBoxButton = new JNewBoxButton(functionTemplate);
			toolMenuPanel.add(newBoxButton);
			
			// agrego el handler al boton que se encarga de crear/destruir la 'ghostBox'
			NewGhostBoxHandler newGhostBoxHandler = new NewGhostBoxHandler((GhostGlassPane)getGlassPane(), "esto_no_se_usa");
			newBoxButton.addMouseListener(newGhostBoxHandler);
			
			// agrego el handler que se encarga de manejar que pasa cuando se dropea efectivamente una 'ghostbox'
			GhostBoxDropHandler ghostBoxDropHandler = new GhostBoxDropHandler(newBoxButton,programPanel);
			newGhostBoxHandler.addGhostDropListener(ghostBoxDropHandler);
			
			// agrego el handler que se encarga de mover la 'ghostBox' sobre el 'glassPane'
			GhostBoxMotionHandler ghostBoxMotionHandler = new GhostBoxMotionHandler((GhostGlassPane)getGlassPane());
			newBoxButton.addMouseMotionListener(ghostBoxMotionHandler);
		}

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
		// agrego los botones al Panel...
		toolMenuPanel.add(aceptar);
		toolMenuPanel.add(cancelar);

		
		this.getContentPane().add(toolMenuPanel, constraints);
	}
	
	private void createRobotPanel()
	{
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.gridx = 0;
		constraints.gridy = 0;
		
		JRobotPanel robotDiagramPanel = new JRobotPanel(JRoboticaFrame.getInstance().getRobot());
		this.getContentPane().add(robotDiagramPanel, constraints);
	}
	
	private void createProgramPanel()
	{
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(5, 0, 5, 5);
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridheight = 2;
		constraints.weightx = 1.0;
		
		programPanel = new JProgramPanel(this.programTemp);
		programPanel.setLayout(new BoxLayout(programPanel,BoxLayout.X_AXIS));
		programPanel.setBackground(Color.RED);
		
		this.getContentPane().add(programPanel, constraints);
	}
	
	private void actionBotonCerrarCancelar()
	{
		int save = saveChanges();
		
		if ( save != JOptionPane.CANCEL_OPTION )
		{
			if ( save == JOptionPane.YES_OPTION )
				this.comportamiento.setProgram(this.programTemp);
			
			this.dispose();
		}
	}
	
	private void actionBotonCerrarAceptar()
	{
		this.comportamiento.setProgram(this.programTemp);
		this.dispose();
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		if( e.getActionCommand() == "Abrir comportamiento guardado..." ){
			// File->Open or Boton->Open
			openBehavior();
		}
		else if( e.getActionCommand() == "Guardar comportamiento como..." ){
			// File->Save or Boton->Save
			saveBehavior();
		}
		else if( e.getActionCommand() == "Acerca de..." ){
			// File->F1 or Boton->F1
			new ProgramHelp();
		}
		else{
			System.err.println("ERROR: command " + e.getActionCommand() + " no está definido !!!");
		}
	}
	
	private int saveChanges()
	{
		Object[] buttonLabels = {"Sí","No","Cancelar"};
		
		int n = JOptionPane.showOptionDialog (
			// parent
			this,
			// message
			"Desea guardar los cambios realizados desde la última vez que salvo el comportamiento?",
			// title
			"Atencion!",
			// type
			JOptionPane.YES_NO_CANCEL_OPTION,
			// icon
			JOptionPane.QUESTION_MESSAGE,
			// icon
			null,
			// button labels
			buttonLabels,
			// default button label
			buttonLabels[1]
		);
		
		return n;
	}
	
	
	private void openBehavior()
	{
		Element rootElement = XmlUtils.openFileDialog(this);
		
		if ( rootElement!=null )
		{
			if ( rootElement.getNodeName() != "comportamiento" )
			{
				System.err.println("El archivo seleccionado no es un archivo de comportamiento valido");
			}
			else
			{
				this.programTemp = new BehaviorProgram(rootElement, JRoboticaFrame.getInstance().getRobot());
				
				this.getContentPane().remove(programPanel);
				createProgramPanel();
				this.getContentPane().validate();
			}
		}
	}
	
	private void saveBehavior()
	{
		Document document = this.programTemp.serialize();
		XmlUtils.saveFileDialog(this,document);
	}

}
