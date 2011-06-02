package extension.gui;

import java.awt.Point;

import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import extension.model.ActuatorBox;
import extension.model.Box;
import extension.model.Diagram;
import extension.model.Panel;
import extension.model.Program;
import extension.model.Robot;
import extension.model.SensorBox;

public class JProgramPanel extends JLayeredPane {	
	private Program program;
	
	private JPanel boxesPanel;
	private JConnectionsPanel connectionsPanel;
	
	public JProgramPanel(Program program) {
		this.program = program;
		
		this.boxesPanel = new JPanel();
		boxesPanel.setLayout(new BoxLayout(boxesPanel, BoxLayout.X_AXIS));		
		add(boxesPanel,new Integer(2));

		/*
		for( Panel panel: diagram.getPanels() ) {
			
			for( Box b: panel.getDefaultBoxes() )
				program.addBox(b);
			
			JBoxPanel p = new JBoxPanel(this, panel);
			boxesPanel.add(p);
			panelsMap.put(panel, p);
		}
		*/
		createDoubleDiagram();
		
		this.connectionsPanel = new JConnectionsPanel(this);
		connectionsPanel.setOpaque(false);
		add(connectionsPanel,new Integer(3));
		
	}
	private void createDoubleDiagram() {
		Panel actuadoresIzq = new Panel("actuadores_izq",Panel.Type.OUTPUT);
		Panel cajasIzq = new Panel("cajas_izq",Panel.Type.BOXES);
		Panel sensores = new Panel("sensores",Panel.Type.INPUT);
		Panel cajasDer = new Panel("cajas_der",Panel.Type.BOXES);
		Panel actuadoresDer = new Panel("actuadores_der",Panel.Type.OUTPUT);
		
		actuadoresIzq.addLocationId("izquierda");
		cajasIzq.addLocationId("izquierda");
		cajasIzq.setHandles(Panel.Handles.LEFT);
		cajasDer.setHandles(Panel.Handles.RIGHT);
		cajasDer.addLocationId("derecha");
		actuadoresDer.addLocationId("derecha");
		
		boxesPanel.add(new JBoxPanel(this, actuadoresIzq));
		boxesPanel.add(new JBoxPanel(this, cajasIzq));
		boxesPanel.add(new JBoxPanel(this, sensores));
		boxesPanel.add(new JBoxPanel(this, cajasDer));
		boxesPanel.add(new JBoxPanel(this, actuadoresDer));
		
		for( SensorBox sensor: program.getRobot().getSensors() ) {
			if( sensor.isPresentByDefault() )
				program.addBox(sensor);
		}
		for( ActuatorBox actuator: program.getRobot().getActuators() ) {
			if( actuator.isPresentByDefault() ) {
				program.addBox(actuator);		
			}		
		}
	}
	public void doLayout() {
		boxesPanel.setBounds(0, 0, getWidth(), getHeight());
		connectionsPanel.setBounds(0, 0, getWidth()-2, getHeight()-2);
	}

	public JBoxPanel getPanelAt(Point p) {
		JBoxPanel answer = (JBoxPanel)boxesPanel.getComponentAt(p);
		return answer;
	}
}
