package extension.gui.fsm.transitions;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import extension.gui.fsm.JNode;
import extension.gui.program.PopupMenuMouseAdapter;
import extension.model.FSM;
import extension.model.Transition;
import extension.utils.IconBank;

class JTransition extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private Transition transition;
	private FSM fsm;
	private JTransitionsPanel jTransitionPanel;
	private Icon icon = null;
	
	JTransition(Transition transition, FSM fsm, JTransitionsPanel parent) {
		super(new GridLayout(1, 1));
		this.fsm = fsm;
		this.jTransitionPanel = parent;
		this.transition = transition;
		
		this.icon = IconBank.getByUrl("images/transition_arrow.png", 25, 25);
		JLabel iconLabel = new JLabel(icon);
		this.add(iconLabel);
		
		this.setSize(icon.getIconWidth(), icon.getIconHeight());
		this.setOpaque(false);
		
        addMouseListener(new PopupMenuMouseAdapter(getPopupMenu()));		
	}
	
	private JNode getSrc() { return jTransitionPanel.getView(transition.getSrc()); }
	private JNode getDst() { return jTransitionPanel.getView(transition.getDst()); }
	
    private JPopupMenu getPopupMenu()
    {
    	JPopupMenu popup = new JPopupMenu();
    	JMenuItem item;
    
    	item = new JMenuItem("borrar conexión");
    	item.addActionListener(this);
    	item.setActionCommand("delete");
    	popup.add(item);
    	
    	item = new JMenuItem("editar");
    	item.addActionListener(this);
    	item.setActionCommand("edit");
    	popup.add(item);
    	
    	return popup;
    }

	@Override
	public void paint(Graphics g) {
		doLayout();
		
		Graphics2D g2D = (Graphics2D) g;
		g2D.rotate(getLineAngle(), icon.getIconWidth()/2, icon.getIconHeight()/2);
		
		super.paint(g2D);
	}

	@Override
	public void doLayout() {
		setLocation(getMiddleArcLocationOnParent());
		super.doLayout();
	}
	
	private Point getMiddleArcLocationOnParent()
	{
		Point psrc = getSourceLocationOnParent();
		Point pdst = getDestinationLocationOnParent();
		
		Point middle = JTransitionsPanel.getArcMiddle(psrc, pdst, true);
		return new Point( middle.x-getWidth()/2 , middle.y-getHeight()/2 );
	}
	
	private double getLineAngle()
	{
		Point psrc = getSourceLocationOnParent();
		Point pdst = getDestinationLocationOnParent();
		
		return Math.atan2(pdst.y-psrc.y,pdst.x-psrc.x);
	}
	
	public Point getSourceLocationOnParent() {
		Point position = this.getSrc().getCenterOnScreen();
		SwingUtilities.convertPointFromScreen(position,this.jTransitionPanel);
		return position;
	}
	public Point getDestinationLocationOnParent() {
		Point position = this.getDst().getCenterOnScreen();
		SwingUtilities.convertPointFromScreen(position,this.jTransitionPanel);
		return position;
	}

	public void actionPerformed(ActionEvent e) {
		if( e.getActionCommand() == "delete" ) {
			fsm.removeTransition(getTransition());
		}
		else if( e.getActionCommand() == "edit" ) {
			new JEditTransitionFrame(this.transition);
		}
	}

	private Transition getTransition() {
		return transition;
	}
}
