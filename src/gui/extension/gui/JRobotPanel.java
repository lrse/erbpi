package extension.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import extension.ExtensionApp;
import extension.model.ImageMapFeature;
import extension.model.Program;
import extension.model.Robot;
import extension.model.SensorBox;
import extension.model.SensorType;
import extension.utils.IconBank;

/*
 * Panel que contiene el esquema del robot, el menu de sensores y el menu de eleccion de robot
 */
public class JRobotPanel extends JPanel implements ActionListener {
	
	private Robot robot;
	private JButton sensorsChooserButton;	// activa el menu para elegir los sensores
	private JButton robotChooserButton;		// activa el menu para elegir robot
	private JRobotIconPanel robotIconPanel;	// esquema de robot
	private JFrame ownerFrame;
	
	public JRobotPanel(Robot robot, JFrame ownerFrame) {
		this.robot = robot;
		this.ownerFrame = ownerFrame;
		robot.getImageMap().setUi(this);

		setAlignmentX(0);
		setOpaque(false);
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		robotIconPanel = new JRobotIconPanel(robot);
		add(robotIconPanel);
		
		add(javax.swing.Box.createHorizontalGlue());
		createSensorMenu();
	}
	
	private void createSensorMenu() {
		sensorsChooserButton = new JButton("");
		sensorsChooserButton.setMargin(new Insets(0,0,0,0));
		//sensorButton.setIcon(icon);
		BoxLayout b = new BoxLayout(sensorsChooserButton, BoxLayout.X_AXIS);
		sensorsChooserButton.setLayout(b);
		sensorsChooserButton.setBackground(Color.GRAY);
		sensorsChooserButton.setActionCommand("chooseSensors");
		sensorsChooserButton.addActionListener(this);

		JLabel seleccionar = new JLabel(IconBank.getByUrl("images/seleccionar.png", 30, 30));
		seleccionar.setBackground(Color.GRAY);
		seleccionar.setOpaque(true);
		sensorsChooserButton.add(seleccionar);
		
		for( SensorType sensorType: robot.getSensorTypes() ) {
			JLabel lbl = new JLabel(sensorType.getImage());
			sensorsChooserButton.add(lbl);
		}		
		add(sensorsChooserButton,0);
	}
	
	public JRobotIconPanel getIconPanel() {
		return robotIconPanel;
	}

	public void actionPerformed(ActionEvent arg0) {
		if( arg0.getActionCommand().equals("chooseSensors") ) {
		
			JSensorsChooserDialog dialog = new JSensorsChooserDialog(ownerFrame, robot, this);
			Point p = sensorsChooserButton.getLocationOnScreen();
			//dialog.setLocationOnScreen(p.x + 30, p.y + 22); // + sensorsChooserButton.getHeight());
			//dialog.setVisible(true);
			dialog.run(ownerFrame.getLayeredPane(), p);
		}
	}
}

class JRobotIconPanel extends JLabel {
	
	Robot robot;
	
	public JRobotIconPanel(Robot robot) {
		super("");
		this.robot = robot;
	
		if( robot.getImageMap().icon != null ) {
			setIcon(robot.getImageMap().icon);
		}
	}

	public void paint(Graphics g) {
		super.paint(g);
		ImageMapFeature feature = robot.getImageMap().getFocusedFeature();
		if( feature != null ) {
			feature.paint(g);
		}
	}
}

class JSensorsChooserDialog extends JInlineDialog {
	
	private HashMap<JToggleButton,SensorBox> buttons;
	private Robot robot;
	private JRobotPanel robotPanel;
	private JFrame ownerFrame;
	
	public JSensorsChooserDialog(JFrame owner, Robot robot, JRobotPanel robotPanel) {
		super();
		buttons = new HashMap<JToggleButton,SensorBox>();
		this.robot = robot;
		this.robotPanel = robotPanel;
		this.ownerFrame = owner;
		
		HashSet<String> ids = new HashSet<String>();
		for( SensorBox s: robot.getSensors() ) {
			
			// esto es lo del número
			// se puede cambiar por otra cosa???
			
			int ndx = s.getId().indexOf('.');
			
		
			String id = s.getId().substring(ndx+1);
			ids.add(id);
		}
		
		int nSensorTypes = 0;
		for( SensorType st: robot.getSensorTypes() )
			nSensorTypes++;
		


		rootPane.setLayout(new GridLayout(ids.size(), nSensorTypes));
		
		ArrayList<String> idsArray = new ArrayList<String>(ids);
		Collections.sort(idsArray, new Comparator<String>() {
			public int compare(String arg0, String arg1) {
				int i1 = Integer.parseInt(arg0);
				int i2 = Integer.parseInt(arg1);
				if( i1 < i2 ) return -1;
				else if( i1 == i2 ) return 0;
				else return 1;
			}
		});
		for( String id: idsArray ) {
			for( SensorType sensorType: robot.getSensorTypes() ) {
				String fullid = String.format("%s.%s", sensorType.getId(), id);
				SensorBox sensor = robot.getSensorById(fullid);
				JComponent b;
				if( sensor != null ) {
					b = createButton(sensor);
				} else {
					b = new JLabel("");
				}
				b.setPreferredSize(new Dimension(40,40));
				rootPane.add(b);					
			}
		}
		
		int w = 40 * nSensorTypes;
		int h = 40 * ids.size();
		
		rootPane.setBounds(0,0,w,h);
		rootPane.doLayout();
	}
	
	public JToggleButton createButton(SensorBox sensor) {
		
		JToggleButton b = new JToggleButton();
		b.setIcon(IconBank.getByUrl("images/activar_no.png",40,40));
		b.setSelectedIcon(IconBank.getByUrl("images/activar_si.png",40,40));
		b.setSelected(Program.getCurrentProgram().containsBox(sensor));
		b.setActionCommand("toggle");
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SensorBox sensor = buttons.get(e.getSource());
				Program.getCurrentProgram().toggleBox(sensor);
			}
		});
		
		buttons.put(b, sensor);
		b.addMouseListener(new MouseAdapter() {

			public void mouseEntered(MouseEvent e) {
				buttons.get(e.getComponent()).setFocused(true);
				ownerFrame.getLayeredPane().repaint(robotPanel.getIconPanel().getBounds());
				super.mouseEntered(e);
			}

			public void mouseExited(MouseEvent e) {
				buttons.get(e.getComponent()).setFocused(false);
				ownerFrame.getLayeredPane().repaint(robotPanel.getIconPanel().getBounds());
				super.mouseExited(e);
			}
		});
		return b;
	}
}

/*
 * Menu para elegir el robot
 */
class JRobotSelector extends JPanel {
	private Robot robot;
	private JLabel label;
	public JRobotSelector(Robot robot) {
		this.robot = robot;
		this.label = new JLabel(robot.getName());
		
		setLayout(new FlowLayout(FlowLayout.LEFT));
		
		setBackground(Color.GRAY);
		setOpaque(true);
		label.setForeground(Color.WHITE);
		Font curFont = label.getFont();
		label.setFont(new Font(curFont.getFontName(), curFont.getStyle(), 20));
		
		URL url = ExtensionApp.class.getResource("images/seleccionar.png");
		Icon icon = IconBank.getByUrl(url, 30, 30);
		label.setIcon(icon);
		add(label);
	}
}