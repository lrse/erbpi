package extension.gui.program;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import extension.gui.elements.Box.BoxType;
import extension.model.BehaviorProgram;
import extension.model.Panel;

class JProgramPanel extends JLayeredPane 
{
	private static final long serialVersionUID = 1L;
	private BehaviorProgram program;
	private JPanel boxesPanel;
	private JConnectionsPanel connectionsPanel;
	
	private List<Panel> panels = new LinkedList<Panel>();
	
	JProgramPanel(BehaviorProgram program)
	{
		this.program = program;
		
		this.boxesPanel = new JPanel();
		boxesPanel.setLayout(new BoxLayout(boxesPanel, BoxLayout.X_AXIS));		
		add(boxesPanel,new Integer(2));
		
		createComponentColumns();
		
		this.connectionsPanel = new JConnectionsPanel(this,program);
		connectionsPanel.setOpaque(false);
		add(connectionsPanel,new Integer(3));
	}
	
	void reloadProgram(BehaviorProgram program)
	{
		this.program = program;
		
		for ( Panel panel : panels )
			panel.reload(this.program);
		
		this.connectionsPanel.reload(this.program);
	}
	
	private void createComponentColumns()
	{
		Panel actuadoresIzq = new Panel(program,"actuadores_izq",BoxType.ACTUATOR,Panel.Location.LEFT);
		Panel cajasIzq = new Panel(program,"cajas_izq",BoxType.FUNCTION,Panel.Location.LEFT);
		Panel sensores = new Panel(program,"sensores",BoxType.SENSOR,Panel.Location.NONE);
		Panel cajasDer = new Panel(program,"cajas_der",BoxType.FUNCTION,Panel.Location.RIGHT);
		Panel actuadoresDer = new Panel(program,"actuadores_der",BoxType.ACTUATOR,Panel.Location.RIGHT);
		
		panels.add(actuadoresIzq);
		panels.add(cajasIzq);
		panels.add(sensores);
		panels.add(cajasDer);
		panels.add(actuadoresDer);
		
		cajasIzq.setHandles(Panel.Handles.LEFT);
		cajasDer.setHandles(Panel.Handles.RIGHT);
		
		boxesPanel.add(new JBoxPanel(actuadoresIzq));
		boxesPanel.add(new JBoxPanel(cajasIzq));
		boxesPanel.add(new JBoxPanel(sensores));
		boxesPanel.add(new JBoxPanel(cajasDer));
		boxesPanel.add(new JBoxPanel(actuadoresDer));
	}
	
	public void doLayout() {
		boxesPanel.setBounds(0, 0, getWidth(), getHeight());
		connectionsPanel.setBounds(0, 0, getWidth()-2, getHeight()-2);
	}

	JBoxPanel getPanelAt(Point p) {
		return (JBoxPanel)boxesPanel.getComponentAt(p);
	}
	
	public BehaviorProgram getProgram()
	{
		return program;
	}
}
