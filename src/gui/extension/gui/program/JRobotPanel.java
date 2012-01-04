package extension.gui.program;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JPanel;

import extension.gui.layouts.VerticalFlowLayout;
import extension.model.ImageMapFeature;
import extension.model.Robot;

/*
 * Panel que contiene el esquema del robot, el menu de sensores y el menu de eleccion de robot
 */
class JRobotPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	private JRobotIconPanel robotIconPanel;	// esquema de robot
	
	JRobotPanel(Robot robot)
	{
		setLayout(new VerticalFlowLayout());
		setBackground(Color.LIGHT_GRAY);
		
		// agrego el label con el nombre del robot
		JLabel nameLabel = new JLabel(robot.getName());
		Font curFont = nameLabel.getFont();
		nameLabel.setFont(new Font(curFont.getFontName(), curFont.getStyle(), 20));
		add(nameLabel);
		
		// agrego el imagemap de los objetos del robot
		robotIconPanel = new JRobotIconPanel(robot);
		add(robotIconPanel);
		robot.getImageMap().setUi(this);
	}
	
	public JRobotIconPanel getIconPanel() {
		return robotIconPanel;
	}
}

class JRobotIconPanel extends JLabel
{
	private static final long serialVersionUID = 1L;
	private Robot robot;
	
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
