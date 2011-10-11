
package extension.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.io.*;
import java.util.*;


import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import extension.ExtensionApp;
import extension.model.FunctionTemplate;
import extension.model.GlobalConfig;
import extension.model.Program;
import extension.model.Robot;
import extension.model.persist.ProgramXml;
import extension.utils.FileUtils;
import extension.utils.IconBank;

import extension.gui.JRoboticaFrame;

import thirdparty.dragnghost.GhostComponentAdapter;
import thirdparty.dragnghost.GhostGlassPane;
import thirdparty.dragnghost.GhostMotionAdapter;

/*
 * Frame principal de la GUI
 */
public class JRoboticaFrame extends JFrame implements ActionListener {

	private JPanel programPanelHolder; // panel que contiene al panel con el programa
	private JRobotPanel robotPanel; // panel con el esquema del robot

	private JButton robotChooserButton; // boton que activa el selector de robot
	private JLabel robotNameLabel;
	private Robot robot;

	public JRoboticaFrame() {
		super("ExtensionRobotica - Laboratorio de Robotica - DC - UBA");

		GhostGlassPane glassPane = new GhostGlassPane();
		setGlassPane(glassPane);

		programPanelHolder = new JPanel();
		programPanelHolder.setLayout(new BoxLayout(programPanelHolder,
				BoxLayout.X_AXIS));
		programPanelHolder.setBackground(Color.RED);

		//robot = Robot.getRobotById("khepera");
		//robot = Robot.getRobotById("yaks");
		robot = Robot.getRobotById("exabot");
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		GridBagConstraints c = new GridBagConstraints();
		JPanel p;

		// c.weightx = 1.0;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 0, 15, 15);
		c.gridy = 0;

		c.gridx = 0;
		p = createMenuPanel();
		add(p, c);

		c.gridx = 1;
		p = createTemplatesPanel();
		add(p, c);

		c.gridx = 2;
		c.weightx = 1.0;
		c.insets = new Insets(0, 0, 15, 0);
		p = createRobotPanel();
		add(p, c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 3;
		c.weighty = 1.0;
		c.insets = new Insets(0, 0, 0, 0);
		c.fill = GridBagConstraints.BOTH;
		add(programPanelHolder, c);
		newProgram();

		setExtendedState(MAXIMIZED_BOTH);

		JRoboticaFrame.instance = this;
	}

	private void newProgram() {
		Program newProgram = new Program(robot);
		Program.setCurrentProgram(newProgram);
		programPanelHolder.removeAll();
		JProgramPanel diagramPanel = new JProgramPanel(newProgram);
		programPanelHolder.add(diagramPanel);
		programPanelHolder.updateUI();
	}

	private void executeProgram() {
		File file;
		try {
			// graba el programa en un temporal
			file = File.createTempFile("prg-", "");
			saveProgram(file);
			
			String exa_ip = new String("192.168.0.2");
			
			Process core_proc = null;
			if (robot.getId().equals("exabot")) {
				Socket s = new Socket(exa_ip, 7654);
				PrintWriter out = new PrintWriter(s.getOutputStream(), true);
				
				BufferedReader in = new BufferedReader(new FileReader(file));
				String text = new String();
				while (in.ready()) { text += in.readLine() + "\n"; }
				out.print(text);
				in.close();
				out.close();
				s.close();
			}
			else {
				// copia la ral adecuada
				File fileSrc = new File("../core/" + robot.getRal());
				File fileDst = new File("../core/libRAL.so");
				FileUtils.copyFile(fileSrc, fileDst);
				//System.out.println("-> la RAL configurada es: " + fileSrc); // muestro lo que copie...
				
				// prepara la ejecución del core
				String[] params = new String[] { "../core/core_exe", file.getAbsolutePath(), "core_log.txt" };
		
				// ejecuta el core
				core_proc = Runtime.getRuntime().exec(params);
				// imprime por pantalla lo que ejecutará...
				System.out.print("-> ejecutando el Core: ");
				for( String s : params ) System.out.print(s + " ");
				System.out.println(" ");
				
				//core_proc.destroy();
							
				// obtengo el PID del "core_proc"
				/*String[] params2 = new String[] { "/bin/bash", "-c",  "ps -ef | grep /lib/ld" };
				Process core_proc_pid = Runtime.getRuntime().exec(params2);
				BufferedReader core_proc_pid_br = new BufferedReader(new InputStreamReader(core_proc_pid.getInputStream()));
				String core_proc_pid_br_line = core_proc_pid_br.readLine();
				StringTokenizer core_proc_pid_br_line_st = new StringTokenizer( core_proc_pid_br_line );
				String core_proc_PID;
				core_proc_PID = core_proc_pid_br_line_st.nextToken();
				core_proc_PID = core_proc_pid_br_line_st.nextToken();*/
				//System.out.println("-> el PID del core es: " + core_proc_PID);
			}
						
			// abre la ventanita de ejecutando...
					// este era el codigo original de diego, pero no se quedaba esperando y seguía la
					// ejecución => ejecutaba el "core_proc.destroy();", entonces se terminaba...
					// por ahora lo dejo con esta versión de ventanita que es vieja, pero anda...
							/*JExecutionDialog dialog = new JExecutionDialog();
							Point p = new Point(100,200); // la ubicación donde aparecerá la ventanita...
							//SwingUtilities.convertPointToScreen(p, this);
							dialog.run(this.getLayeredPane(), p);
							*/
			JOptionPane.showOptionDialog(this, 
					"Ejecutando el programa...",
					"Ejecutando",
					JOptionPane.OK_OPTION, 
					JOptionPane.INFORMATION_MESSAGE, 
					null, 
					new Object[] {"Parar"},
					"Parar");
			
			// termina el core enviando el signal SIGINT // obtengo el PID del "core_proc"
			/*String[] params3 = new String[] { "/bin/bash", "-c",  "kill -int " + core_proc_PID };
			Process core_proc_SIGINT = Runtime.getRuntime().exec(params3);*/
			//System.out.println("-> hicimos 'kill -int " + core_proc_PID + "' y matamos el core...");
	
			if (robot.getId().equals("exabot")) {
				Socket s = new Socket(exa_ip, 7654);
				PrintWriter out = new PrintWriter(s.getOutputStream(), true);
				out.println("STOP");
				out.close();
				s.close();
			}
			else {	
				// destruyo todos los procesos...
				// en realidad esto no anda bien, lo comentamos...
				core_proc.destroy();
					//core_proc.destroy(); //core_proc_pid.destroy(); //core_proc_SIGINT.destroy();
	
				// imprime codigo de terminación del core
				// en realidad esto no anda bien, lo comentamos...
					//System.out.println(String.format("-> el Core terminó con 'exit error': %d", core_proc.exitValue()));
			}

		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, new String("Hubo problemas al intentar ejecutar:\n") + e.getMessage());
		}
	}

	private void saveProgram(File file) {
		try {
			ProgramXml.save(file, Program.getCurrentProgram());

		} catch (ParserConfigurationException e) {
			return;
		} catch (TransformerConfigurationException e) {
			return;
		} catch (TransformerException e) {
			return;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void openProgram(File f) {
		//borro el programPanelHolder
		programPanelHolder.removeAll();
		// borro el robotPanel
		robotPanel.removeAll();
		try {
			Program newProgram = ProgramXml.load(f);
			Program.setCurrentProgram(newProgram);
			JProgramPanel diagramPanel = new JProgramPanel(newProgram);
			programPanelHolder.add(diagramPanel);
			programPanelHolder.updateUI();
			// armo el nuevo robotPanel	
			robot = newProgram.getRobot();
			JRobotPanel nuevoRobot = new JRobotPanel( robot, this );
			robotPanel.add(nuevoRobot);
			// lo updateo...
			robotPanel.updateUI();
			// actualizo el nombre del menu de robot...
			robotNameLabel.setText(robot.getName());
			// repinto!
			repaint();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private JPanel createMenuPanel() {
		FlowLayout layout = new FlowLayout();
		layout.setVgap(20);
		JPanel menuPanel = new JPanel(layout);

		menuPanel.setBackground(Color.GRAY);

		menuPanel.add(createButton("Nuevo", "newProgram", "nuevo"));
		menuPanel.add(createButton("Abrir", "openProgram", "abrir"));
		menuPanel.add(createButton("Guardar", "saveProgram", "guardar"));
		menuPanel.add(createButton("Ejecutar", "executeProgram", "ejecutar"));
		menuPanel.add(createButton("Salir", "exitApplication", "salir"));

		return menuPanel;
	}

	private JButton createButton(String name, String command, String iconName) {
		JButton button = new JButton(name);
		button.setBorder(null);
		button.setActionCommand(command);
		button.addActionListener(this);
		button.setVerticalTextPosition(JButton.BOTTOM);
		button.setHorizontalTextPosition(JButton.CENTER);
		button.setBackground(Color.GRAY);
		button.setForeground(Color.WHITE);
		button.setVerticalAlignment(JButton.CENTER);
		Font curFont = button.getFont();
		button.setFont(new Font(curFont.getFontName(), curFont.getStyle(), 10));

		String s = String.format("images/menu_%s.png", iconName);
		URL url = ExtensionApp.class.getResource(s);
		ImageIcon icon = IconBank.getByUrl(url, 40, 40);
		button.setIcon(icon);
		return button;
	}

	private JPanel createTemplatesPanel() {
		FlowLayout layout = new FlowLayout();
		layout.setVgap(20);
		JPanel panel = new JPanel(layout);
		panel.setBackground(Color.GRAY);

		for (FunctionTemplate functionTemplate : GlobalConfig.getCurrentConfig().getFunctionTemplates()) {
			JBoxTemplate b = new JBoxTemplate(functionTemplate);
			panel.add(b);
			GhostComponentAdapter adapter = new GhostComponentAdapter(
					(GhostGlassPane) getGlassPane(), "action_1");
			b.addMouseListener(adapter);
			b.addMouseMotionListener(new GhostMotionAdapter(
					(GhostGlassPane) getGlassPane()));

			adapter.addGhostDropListener(new BoxTemplateDropManager(b,
					programPanelHolder));
		}
		return panel;
	}

	private JPanel createRobotPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		GridBagConstraints c = new GridBagConstraints();

		panel.setBackground(Color.LIGHT_GRAY);
		c.anchor = GridBagConstraints.FIRST_LINE_END;

		robotPanel = new JRobotPanel(robot, this);
		panel.add(robotPanel, BorderLayout.EAST);
		createRobotMenu(panel);
		return panel;
	}

	private void createRobotMenu(JPanel panel) {
		robotChooserButton = new JButton("");

		robotChooserButton.setMargin(new Insets(0, 0, 0, 0));
		BoxLayout b = new BoxLayout(robotChooserButton, BoxLayout.X_AXIS);
		robotChooserButton.setLayout(b);
		robotChooserButton.setBackground(Color.GRAY);
		robotChooserButton.setActionCommand("chooseRobot");
		robotChooserButton.addActionListener(this);

		JLabel seleccionar = new JLabel(IconBank.getByUrl(
				"images/seleccionar.png", 30, 30));
		seleccionar.setBackground(Color.GRAY);
		seleccionar.setOpaque(true);
		robotChooserButton.add(seleccionar);

		JLabel label = new JLabel("");
		Font curFont = label.getFont();
		robotNameLabel = new JLabel(robot.getName());
		robotNameLabel.setForeground(Color.WHITE);
		robotNameLabel.setFont(new Font(curFont.getFontName(), curFont.getStyle(), 20));
		robotChooserButton.add(robotNameLabel);

		panel.add(robotChooserButton, BorderLayout.SOUTH);
	}

	public void run() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {

		if( e.getActionCommand().startsWith("changeRobot:") ) {
			String robotId = e.getActionCommand().substring("changeRobot:".length());
			robot = Robot.getRobotById(robotId);
			Program.getCurrentProgram().setRobot(robot);
			robotNameLabel.setText(robot.getName());
			//borro el programPanelHolder
			programPanelHolder.removeAll();
			// borro el robotPanel
			robotPanel.removeAll();
			newProgram();
			programPanelHolder.updateUI();
			// armo el nuevo robotPanel	
			JRobotPanel nuevoRobot = new JRobotPanel( robot, this );
			robotPanel.add(nuevoRobot);
			// lo updateo...
			robotPanel.updateUI();
			// repinto!
			repaint();
		}
		else if (e.getActionCommand().equals("chooseRobot")) {
			JRobotChooserDialog dialog = new JRobotChooserDialog(this);
			Point p = robotChooserButton.getLocationOnScreen();
			dialog.setSize(robotChooserButton.getSize());
			dialog.run(getLayeredPane(), p);
			

			
		}

		else if (e.getActionCommand() == "newProgram") {
			newProgram();
		} else if (e.getActionCommand() == "executeProgram") {
			executeProgram();
		} else if (e.getActionCommand() == "exitApplication") {
			System.exit(0);
		} else if (e.getActionCommand() == "openProgram") {
			JFileChooser fc = new JFileChooser(new File("../comportamientos"));
			int returnVal = fc.showOpenDialog(this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				openProgram(file);
			}
		} else if (e.getActionCommand() == "saveProgram") {
			JFileChooser fc = new JFileChooser(new File("../comportamientos"));
			System.out.println(fc.getCurrentDirectory().toString());
			int returnVal = fc.showSaveDialog(this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				saveProgram(file);
			}
		}
	}

	private static JRoboticaFrame instance = null;

	public static JRoboticaFrame getInstance() {
		return instance;
	}
}

class JExecutionDialog extends JInlineDialog {

	public JExecutionDialog() {
		rootPane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		JLabel label = new JLabel("Ejecutando...");
		label.setForeground(Color.WHITE);
		Font curFont = label.getFont();
		label.setFont(new Font(curFont.getFontName(), curFont.getStyle(), 16));

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 3;
		c.ipady = 10;
		rootPane.add(label, c);

		c.gridy = 1;
		c.gridwidth = 1;
		c.gridx = 0;
		rootPane.add(createButton("play", "ejecutar"), c);
		c.gridx = 1;
		rootPane.add(createButton("pause", "pausa"), c);
		c.gridx = 2;
		rootPane.add(createButton("exit", "salir"), c);

		rootPane.setBounds(30, 100, 200, 100);
		glassPane.removeMouseListener(glassPane.getMouseListeners()[0]);
	}

	private JButton createButton(String command, String iconName) {
		JButton button = new JButton();
		button.setBorder(null);
		button.setActionCommand(command);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}
		});
		button.setBackground(Color.GRAY);
		button.setForeground(Color.WHITE);
		button.setVerticalAlignment(JButton.CENTER);

		String s = String.format("images/menu_%s.png", iconName);
		URL url = ExtensionApp.class.getResource(s);
		ImageIcon icon = IconBank.getByUrl(url, 40, 40);
		button.setIcon(icon);
		return button;
	}
}

class JRobotChooserDialog extends JInlineDialog {
	private JFrame ownerFrame;
	
	public JRobotChooserDialog(JFrame owner) {
		//super(owner);
		this.ownerFrame = owner;
		GridLayout layout = new GridLayout(5,1,0,10);
		rootPane.setLayout(layout);
// -> acá pincha !!!
		for( Robot robot: Robot.getRobots() ) {
			JButton button = createButton(robot.getName(),robot.getId());
			rootPane.add(button,layout);
		}
		rootPane.setSize(rootPane.getPreferredSize());
	}

	public void setSize(Dimension dim) {
		dim.height = rootPane.getBounds().height;
		super.setSize(dim);
		rootPane.setSize(dim);
	}

	private JButton createButton(String robotName, String robotId) {
		JButton button = new JButton(robotName);
		button.setBorder(null);
		button.setBackground(Color.GRAY);
		button.setForeground(Color.WHITE);
		button.setVerticalAlignment(JButton.CENTER);
		Font curFont = button.getFont();
		button.setFont(new Font(curFont.getFontName(), curFont.getStyle(), 20));
		button.setActionCommand(String.format("changeRobot:%s",robotId));
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		button.addActionListener((ActionListener) ownerFrame);
		button.setAlignmentX(LEFT_ALIGNMENT);
		return button;
	}
}