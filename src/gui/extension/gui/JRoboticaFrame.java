
package extension.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import extension.gui.fsm.FSMHelp;
import extension.gui.fsm.JFSMPanel;
import extension.gui.program.JProgramFrame;
import extension.model.BehaviorNode;
import extension.model.FSM;
import extension.model.Robot;
import extension.utils.FileUtils;
import extension.utils.IconBank;
import extension.utils.XmlUtils;
import extension.utils.execution.JExecutionFrame;
import extension.utils.execution.JRemoteExecutionFrame;

/*
 * Frame principal de la GUI
 */
public class JRoboticaFrame extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 1L;

	private JPanel programPanelHolder; 	// panel que contiene al panel con el programa

	private FSM fsm = null;
	
	private JButton nuevoButton = null;
	private JButton abrirButton = null;
	private JButton guardarButton = null;
	private JButton ejecutarButton = null;
	private JButton salirButton = null;
	private JButton timersButton = null;
	private JButton countersButton = null;

	private JPanel toolsPanel;

	// variables para centrar la ventana en la pantalla y definir tamaño ventana...
	private Dimension tamanioPantalla;
	private Dimension tamanioVentana = new Dimension( 800,600 );
	
	public JRoboticaFrame()
	{
		super("ERBPI.robotica.dc.uba.ar");
		
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		
		AddPanels();

		CreateSystemMenu();

		setSize( tamanioVentana );

		// ubico la ventana en el centro de la pantalla...
        tamanioPantalla = Toolkit.getDefaultToolkit().getScreenSize();
		tamanioVentana = getSize();
		setLocation(((tamanioPantalla.width - tamanioVentana.width)/2), (tamanioPantalla.height - tamanioVentana.height)/2);
		
		JRoboticaFrame.instance = this;
	}
	
	private void AddPanels()
	{
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(/*top*/0, /*left*/5, /*bottom*/5, /*right*/0);
		
		// AddMenuPanel();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 0.7;
		//constraints.weighty = 1.0;
		//constraints.gridwidth = 3;
		JPanel menuPanel = createMenuPanel();
		add( menuPanel, constraints );

		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.weightx = 1.5;
		//constraints.weighty = 1.0;
		//constraints.gridwidth = 3;
		toolsPanel = createToolsPanel();
		add( toolsPanel, constraints );

		constraints.insets = new Insets(/*top*/0, /*left*/5, /*bottom*/5, /*right*/5);		
		constraints.gridx = 2;
		constraints.gridy = 0;
		constraints.weightx = 0.01;	// lo pongo bien chiquito así se ajusta automático al boton... 
		//constraints.weighty = 1.0;
		//constraints.gridwidth = 3;
		JPanel exitPanel = createExitPanel();
		add( exitPanel, constraints );

		
		// AddProgramPanelHolder();
		constraints.gridwidth = 3;
		constraints.insets = new Insets(/*top*/0, /*left*/0, /*bottom*/0, /*right*/0);
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.gridwidth = 3;
		programPanelHolder = new JPanel();
		programPanelHolder.setLayout(new BoxLayout(programPanelHolder,BoxLayout.X_AXIS));
		programPanelHolder.setBackground(Color.GRAY);
		add( programPanelHolder, constraints );
	}

	private void CreateSystemMenu()
	{
		
		JMenuBar menuBar;
		
		//Create the menu bar.
		menuBar = new JMenuBar();
		
		// Create file menu
		JMenu fileMenu = new JMenu("Archivo");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(fileMenu);
		
		JMenuItem newMenuItem = new JMenuItem("Nuevo",KeyEvent.VK_N);
		newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		newMenuItem.getAccessibleContext().setAccessibleDescription("Create new Project");
		newMenuItem.addActionListener(this);
		fileMenu.add(newMenuItem);
		
		JMenuItem openMenuItem = new JMenuItem("Abrir",KeyEvent.VK_O);
		openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		openMenuItem.getAccessibleContext().setAccessibleDescription("Open Project");
		openMenuItem.addActionListener(this);
		fileMenu.add(openMenuItem);
		
		JMenuItem saveMenuItem = new JMenuItem("Guardar",KeyEvent.VK_S);
		saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		saveMenuItem.getAccessibleContext().setAccessibleDescription("Save Project");
		saveMenuItem.addActionListener(this);
		fileMenu.add(saveMenuItem);
		
		JMenuItem runMenuItem = new JMenuItem("Correr",KeyEvent.VK_R);
		runMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
		runMenuItem.getAccessibleContext().setAccessibleDescription("Run project");
		runMenuItem.addActionListener(this);
		fileMenu.add(runMenuItem);
		
		JMenuItem closeMenuItem = new JMenuItem("Cerrar",KeyEvent.VK_W);
		closeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
		closeMenuItem.getAccessibleContext().setAccessibleDescription("Close Project");
		closeMenuItem.addActionListener(this);
		fileMenu.add(closeMenuItem);
		
		JMenuItem quitMenuItem = new JMenuItem("Salir",KeyEvent.VK_Q);
		quitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		quitMenuItem.getAccessibleContext().setAccessibleDescription("Exit Program");
		quitMenuItem.addActionListener(this);
		fileMenu.add(quitMenuItem);
		
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
	
	private void newProgram()
	{
		JFrame frame = new JFrame();
		String list[] = {"exabot","exabotsim","khepera","yaks"};
		String robotId = (String)JOptionPane.showInputDialog(frame,"Elija el Robot con el que trabajará...","Seleccionar Robot",JOptionPane.QUESTION_MESSAGE,null,list,null);
		
		if ( robotId != null )
		{
			Robot robot = Robot.getRobotById(robotId);
			FSM fsm = new FSM(robot);
			
			startProgram(fsm);
		}
	}
	
	private void executeProgram()
	{
		try {
			// imprimo el directorio actual por razones de debug
			System.out.println("directorio actual: "+(new java.io.File(".")).getCanonicalPath());
		} catch (IOException e) {
			System.err.println("Error tratando de encontrar el directorio actual.");
			e.printStackTrace();
			return;
		}

		File tempProgramFile = null;
		
		// grabo el programa actual en un archivo temporal
		Document document = fsm.serialize();
		try {
			tempProgramFile = XmlUtils.saveTemporalFile(document);
		} catch (IOException e) {
			System.err.println("Error creando el archivo temporal.");
			e.printStackTrace();
			return;
		}
		
		if (getRobot().getId().equals("exabot"))
		{
			new JRemoteExecutionFrame(tempProgramFile, "192.168.0.2", 7654);
		}
		else {
			// copio la ral adecuada
			File fileSrc = new File("../core/" + getRobot().getRal());
			File fileDst = new File("../core/libRAL.so");
			
			try {
				FileUtils.copyFile(fileSrc, fileDst);
			} catch (IOException e) {
				System.err.println("Error copiando "+fileSrc.getAbsolutePath()+" a "+fileDst.getAbsolutePath());
				e.printStackTrace();
				return;
			}
			
			System.out.println("-> la RAL configurada es: " + fileSrc);
			
			String corePath = "../core/core_exe";
			
			String cmdString = corePath + " " + tempProgramFile.getAbsolutePath() + " core_log.txt";
			System.out.println("-> ejecutando el Core: "+cmdString);
			
			new JExecutionFrame(cmdString);
		}
	}

	private void saveProgram()
	{
		Document document = fsm.serialize();
		XmlUtils.saveFileDialog(this, document);
	}

	private void openProgram()
	{
		Element rootElement = XmlUtils.openFileDialog(this);
		
		if (rootElement!=null)
		{
			if ( rootElement.getNodeName() != "conducta" )
			{
				JOptionPane.showMessageDialog(this, "El archivo seleccionado no es un archivo de conducta valido", "Error", JOptionPane.WARNING_MESSAGE);
			}
			else
			{
				if (activeProjectExists())
					closeProgram();
				
				startProgram(new FSM(rootElement));
			}
		}
	}
	
	private void startProgram(FSM fsm)
	{
		this.fsm = fsm;
		
		JFSMPanel fsmProgramPanel = new JFSMPanel(fsm);

		// habilito los botones del toolPanel		
		ejecutarButton.setEnabled(true);
		timersButton.setEnabled(true);
		countersButton.setEnabled(true);
		
		// le transfiero los botones Timers y Counters a JFSMPanel		
		fsmProgramPanel.setToolsButtons( this.timersButton, this.countersButton );
		
		programPanelHolder.removeAll();
		programPanelHolder.add(fsmProgramPanel);
		programPanelHolder.updateUI();
	}
	
	private void closeProgram()
	{
		// TODO: falta que pregunte
		// si quiero guardar los cambios
		
		this.fsm=null;
		
		// deshabilito los botones del toolPanel		
		ejecutarButton.setEnabled(false);
		timersButton.setEnabled(false);
		countersButton.setEnabled(false);
		
		programPanelHolder.removeAll();
		programPanelHolder.updateUI();
	}
	
	private JPanel createMenuPanel()
	{
		FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
		layout.setVgap(5);
		JPanel menuPanel = new JPanel(layout);

		menuPanel.setBackground(Color.GRAY);

		Icon nuevoIcon = IconBank.getByUrl("images/menu_nuevo.png", 40, 40);
		nuevoButton = new JButton(nuevoIcon);
		//nuevoButton.setBounds(0,0,40, 40);
		nuevoButton.setBorder(null);
		nuevoButton.setBackground(Color.GRAY);
		nuevoButton.setVerticalAlignment(JButton.CENTER);
		nuevoButton.setToolTipText("nuevo");
		nuevoButton.setActionCommand("Nuevo");
		nuevoButton.addActionListener(this);
		menuPanel.add( nuevoButton );
		
		Icon abrirIcon = IconBank.getByUrl("images/menu_abrir.png", 40, 40);
		abrirButton = new JButton(abrirIcon);
		//abrirButton.setBounds(40,0,40, 40);
		abrirButton.setBorder(null);
		abrirButton.setBackground(Color.GRAY);
		abrirButton.setVerticalAlignment(JButton.CENTER);
		abrirButton.setToolTipText("abrir");
		abrirButton.setActionCommand("Abrir");
		abrirButton.addActionListener(this);
		menuPanel.add( abrirButton );

		Icon guardarIcon = IconBank.getByUrl("images/menu_guardar.png", 40, 40);
		guardarButton = new JButton(guardarIcon);
		//guardarButton.setBounds(80,0,40, 40);
		guardarButton.setBorder(null);
		guardarButton.setBackground(Color.GRAY);
		guardarButton.setVerticalAlignment(JButton.CENTER);
		guardarButton.setToolTipText("guardar");
		guardarButton.setActionCommand("Guardar");
		guardarButton.addActionListener(this);
		menuPanel.add( guardarButton );

		return menuPanel;
	}

	private JPanel createToolsPanel() {
		//FlowLayout layout = new FlowLayout();
		FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
		layout.setVgap(5);
		JPanel menuPanel = new JPanel(layout);

		menuPanel.setBackground(Color.GRAY);

		Icon ejecutarIcon = IconBank.getByUrl("images/menu_ejecutar.png", 40, 40);
		ejecutarButton = new JButton(ejecutarIcon);
		//ejecutarButton.setBounds(160,0,40, 40);
		ejecutarButton.setBorder(null);
		ejecutarButton.setBackground(Color.GRAY);
		ejecutarButton.setVerticalAlignment(JButton.CENTER);
		ejecutarButton.setToolTipText("ejecutar");
		ejecutarButton.setActionCommand("Correr");
		ejecutarButton.addActionListener(this);
		ejecutarButton.setEnabled(false); // recién cuando se cree un JFSMPanel lo voy a habilitar !!!
		menuPanel.add( ejecutarButton );

		Icon timerIcon = IconBank.getByUrl("images/menu_timer.png", 40, 40);
		timersButton = new JButton(timerIcon);
		//timersButton.setBounds(280,0,40, 40);
		timersButton.setBorder(null);
		timersButton.setBackground(Color.GRAY);
		timersButton.setVerticalAlignment(JButton.CENTER);
		timersButton.setToolTipText("temporizadores");
		timersButton.setActionCommand("openTimers");
		//timersButton.addActionListener(this); // porque el listener lo tiene JFSMPanel !!!
		timersButton.setEnabled(false);	 // recién cuando se cree un JFSMPanel lo voy a habilitar !!!
		menuPanel.add( timersButton );
		
		Icon counterIcon = IconBank.getByUrl("images/menu_contador.png", 40, 40);
		countersButton = new JButton(counterIcon);
		//countersButton.setBounds(320,0,40, 40);
		countersButton.setBorder(null);
		countersButton.setBackground(Color.GRAY);
		countersButton.setVerticalAlignment(JButton.CENTER);
		countersButton.setToolTipText("contadores");
		countersButton.setActionCommand("openCounters");
		//countersButton.addActionListener(this); // porque el listener lo tiene JFSMPanel !!!
		countersButton.setEnabled(false);  // recién cuando se cree un JFSMPanel lo voy a habilitar !!!
		menuPanel.add( countersButton );
	
		return menuPanel;
	}
		
	private JPanel createExitPanel() {
		//FlowLayout layout = new FlowLayout();
		FlowLayout layout = new FlowLayout(FlowLayout.RIGHT);
		layout.setVgap(5);
		JPanel menuPanel = new JPanel(layout);

		menuPanel.setBackground(Color.GRAY);

		Icon salirIcon = IconBank.getByUrl("images/menu_salir.png", 40, 40);
		salirButton = new JButton(salirIcon);
		//salirButton.setBounds(this.getWidth()-40,0,40, 40);
		salirButton.setBorder(null);
		salirButton.setBackground(Color.GRAY);
		salirButton.setVerticalAlignment(JButton.CENTER);
		salirButton.setToolTipText("salir");
		salirButton.setActionCommand("Salir");
		salirButton.addActionListener(this);
		menuPanel.add( salirButton );

		return menuPanel;
	}
	
	private boolean activeProjectExists() { return (fsm!=null); }
	
	public void run()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if( e.getActionCommand() == "Nuevo" ){
			// File->New or Boton->New
				newProgram();
		}
		else if( e.getActionCommand() == "Abrir" ){
			// File->Open or Boton->Open
			//abrirPrograma();
			openProgram();
		}
		else if( e.getActionCommand() == "Guardar" ){
			// File->Save or Boton->Save
			saveProgram();
		}
		else if( e.getActionCommand() == "Cerrar" ){
			// File->Close 
			closeProgram();
		}
		else if( e.getActionCommand() == "Salir" ){
			// File->Quit or Boton->Quit
			closeProgram();
			System.exit(0);
		}
		else if( e.getActionCommand() == "Correr" ){
			// File->Run or Boton->Run
			executeProgram();
		}
		else if( e.getActionCommand() == "Acerca de..." ){
			// File->F1 or Boton->F1
			new FSMHelp();
		}
		else{
			System.err.println("ERROR: command " + e.getActionCommand() + " no está definido !!!");
		}
	}

	public Robot getRobot()
	{
		return fsm.getRobot();
	}
	
	public FSM getFSM() {
		return this.fsm;
	} 
	
	public void OpenProgramFrame(BehaviorNode comportamiento )
	{
		new JProgramFrame( this, comportamiento );
	}
	
	private static JRoboticaFrame instance = null;

	public static JRoboticaFrame getInstance() { return instance; }
}
