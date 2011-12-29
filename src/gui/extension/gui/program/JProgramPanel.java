package extension.gui.program;

import java.awt.Component;
import java.awt.Point;

import javax.swing.BoxLayout;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import extension.model.BehaviorProgram;
import extension.model.Panel;
import extension.model.elements.Box;

public class JProgramPanel extends JLayeredPane 
{
	private static final long serialVersionUID = 1L;
	private BehaviorProgram program;
	private JPanel boxesPanel;
	private JConnectionsPanel connectionsPanel;
	
	public JProgramPanel(BehaviorProgram program)
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
	
	private void createComponentColumns()
	{
		Panel actuadoresIzq = new Panel("actuadores_izq",Panel.Type.OUTPUT,Panel.Location.LEFT);
		Panel cajasIzq = new Panel("cajas_izq",Panel.Type.BOXES,Panel.Location.LEFT);
		Panel sensores = new Panel("sensores",Panel.Type.INPUT,Panel.Location.NONE);
		Panel cajasDer = new Panel("cajas_der",Panel.Type.BOXES,Panel.Location.RIGHT);
		Panel actuadoresDer = new Panel("actuadores_der",Panel.Type.OUTPUT,Panel.Location.RIGHT);
		
		cajasIzq.setHandles(Panel.Handles.LEFT);
		cajasDer.setHandles(Panel.Handles.RIGHT);
		
		boxesPanel.add(new JBoxPanel(actuadoresIzq, program));
		boxesPanel.add(new JBoxPanel(cajasIzq, program));
		boxesPanel.add(new JBoxPanel(sensores, program));
		boxesPanel.add(new JBoxPanel(cajasDer, program));
		boxesPanel.add(new JBoxPanel(actuadoresDer, program));
		
		for( Box box: program.getBoxes() )
		{
			for( Component child : boxesPanel.getComponents() )
					( (JBoxPanel)child ).boxAdded(box);
		}
		
	}
	public void doLayout() {
		boxesPanel.setBounds(0, 0, getWidth(), getHeight());
		connectionsPanel.setBounds(0, 0, getWidth()-2, getHeight()-2);
	}

	public JBoxPanel getPanelAt(Point p) {
		return (JBoxPanel)boxesPanel.getComponentAt(p);
	}
	
	public BehaviorProgram getProgram()
	{
		return program;
	}
}
