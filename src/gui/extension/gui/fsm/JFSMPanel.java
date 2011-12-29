package extension.gui.fsm;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLayeredPane;

import extension.gui.fsm.transitions.JTransitionsPanel;
import extension.model.BehaviorNode;
import extension.model.BehaviorProgram;
import extension.model.FSM;
import extension.model.FSMListener;
import extension.model.Transition;

public class JFSMPanel extends JLayeredPane implements FSMListener, ActionListener
{
	private static final long serialVersionUID = 1L;
	
	private static final int TRANSITION_LAYER = JLayeredPane.DEFAULT_LAYER;
	private static final int NODE_LAYER = JLayeredPane.DEFAULT_LAYER;
	//private static final int BUTTON_LAYER = JLayeredPane.DRAG_LAYER;

	private FSM fsm;
	private Map<BehaviorNode,JNode> nodeViews = new HashMap<BehaviorNode, JNode>();
	private JTransitionsPanel transitionsPanel;
	private JButton timersButton = null;
	private JButton countersButton = null;
	
	public JFSMPanel(FSM fsm)
	{
		this.fsm = fsm;
		this.fsm.addListener(this);
		
		this.transitionsPanel = new JTransitionsPanel(fsm,this);
		transitionsPanel.setOpaque(false);
		add(transitionsPanel,JFSMPanel.TRANSITION_LAYER);
		
		for (BehaviorNode behavior : this.fsm.getBehaviors())
			behaviorAdded(behavior);
				
		this.addMouseListener(new NewIconHandler());
	}
	
	// para los JPanel que son agregados a un JLayeredPane hay que definir el size explicitamente
	@Override
	public void doLayout() {
		this.transitionsPanel.setBounds(0, 0, this.getWidth(), this.getHeight());
		//this.timersButton.setBounds(this.getWidth()-2*50, 0, 50, 50);
		//this.countersButton.setBounds(this.getWidth()-50, 0, 50, 50);
		//this.salirButton.setBounds(this.getWidth()-40,0,40, 40);
		super.doLayout();
	}
	
	// ===========================================================
	
	@Override
	public void behaviorAdded( BehaviorNode behavior )
	{
		//System.out.println("add node " + behavior.Id() + "\n");
		
		JNode node = ( fsm.getInitialNode() == behavior ) ?
				new JInitialNode(behavior,this) :
				new JBehaviorNode(behavior,this);
		
		node.setBounds((int)behavior.getPosition().getX()-node.getWidth()/2, (int)behavior.getPosition().getY()-node.getHeight()/2, node.getWidth(), node.getHeight());
		
		// agrego el nodo al panel en la capa mas baja
		this.add(node,JFSMPanel.NODE_LAYER);
		
		this.nodeViews.put(behavior,node);
		
		// esto hace falta para que el JPanel actualice y dibuje los hijos agregados en tiempo de ejecucion 
		this.revalidate();
		this.repaint();
	}
	
	@Override
	public void behaviorRemoved( BehaviorNode behavior )
	{
		JNode node = nodeViews.get(behavior);
		nodeViews.remove(behavior);
		
		this.remove(node);
		
		// esto hace falta para que el JPanel actualice y dibuje los hijos agregados en tiempo de ejecucion 
		this.revalidate();
		this.repaint();
	}
	
	@Override
	public void transitionAdded( Transition transition )
	{
	}
	@Override
	public void transitionRemoved( Transition transition )
	{
	}
	
	private class NewIconHandler extends MouseAdapter
	{
		@Override
		public void mouseClicked(MouseEvent e){
			Point position = new Point(e.getX(),e.getY());
			fsm.addNewBehaviorNode(position,new BehaviorProgram());
		}
	}

	public FSM getFSM() {
		return this.fsm;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ( e.getActionCommand() == "openTimers" ) {
			if( FSM.timersFrameOpen == false )
				new JTimerFrame(this.fsm);
		}
		else if ( e.getActionCommand() == "openCounters" ) {
			if( FSM.countersFrameOpen == false )
				new JCounterFrame(this.fsm);
		}
		else if ( e.getActionCommand() == "nuevo" ) {
		}
		
        
	}

	public void setToolsButtons( JButton timers, JButton counters){
		this.timersButton = timers;
//		timersButton.setToolTipText("timers");
//		timersButton.setActionCommand("openTimers");
		timersButton.addActionListener(this);

		this.countersButton = counters;
//		countersButton.setToolTipText("counters");
//		countersButton.setActionCommand("openCounters");
		countersButton.addActionListener(this);
	}

	public JNode getView(BehaviorNode behavior) { return nodeViews.get(behavior); }
}
