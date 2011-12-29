package extension.gui.fsm;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

import extension.model.BehaviorNode;

abstract public class JNode extends JLayeredPane
{
	private static final long serialVersionUID = 1L;
	
	protected JFSMPanel fsmPanel = null;
	private BehaviorNode behavior = null;
	
	private Icon icon = null;
	protected JLabel iconLabel;
	
	private boolean draggingTransition = false;
	
	public JNode(BehaviorNode behavior, JFSMPanel fsmPanel)
	{
		//super(new GridLayout(1, 1));
		//this.setOpaque(false);
		
		this.behavior = behavior;
		this.fsmPanel = fsmPanel;
		
		this.icon = getNewIcon();
		createIconLabel();
		this.add(iconLabel, JLayeredPane.DEFAULT_LAYER);
		
		this.addMouseListener(new MouseNodeHandler());
		this.addMouseMotionListener(new MotionNodeHandler());
		
		// esto es para que FSMPanel se de cuenta y repinte lo que cambió...
		this.revalidate();
		this.fsmPanel.repaint();
	}
	
	abstract protected Icon getNewIcon();
	
	protected void createIconLabel()
	{
		// defino el icono (imagen) con descripción y botón de cierre...
		iconLabel = new JLabel(icon);
		iconLabel.setIconTextGap(-15);
		iconLabel.setBounds(0, 0, icon.getIconWidth(), icon.getIconHeight());
		iconLabel.setVerticalAlignment(JLabel.TOP);
	}
	
	public int getWidth()	{ return icon.getIconWidth(); }
	public int getHeight()	{ return icon.getIconHeight(); }
	
	public BehaviorNode getBehavior()	{ return behavior; }
	
	public Point getCenterOnScreen(){
		Point nodePosition = this.getLocationOnScreen();
		Dimension nodeSize = this.getSize();
		return new Point(
			(int) ( nodePosition.getX()+nodeSize.getWidth()/2 ),
			(int) ( nodePosition.getY()+nodeSize.getHeight()/2 )
		);
	}
	
	private class MouseNodeHandler extends MouseAdapter{
		public void mousePressed( MouseEvent e ){
			if( !e.isShiftDown() ){
				draggingTransition=true;
				JNode node = (JNode)e.getSource();
				fsmPanel.getFSM().getTransitionMaker().start(node,node.getCenterOnScreen());
			}
		}
		
		public void mouseReleased( MouseEvent e ){
			if( draggingTransition ){
				draggingTransition=false;
				fsmPanel.getFSM().getTransitionMaker().stop();
			}
		}
		
		public void mouseEntered( MouseEvent e ){
			JNode node = (JNode)e.getComponent();
			fsmPanel.getFSM().getTransitionMaker().setDstNode(node);
		}

	    public void mouseExited( MouseEvent e ){
	    	fsmPanel.getFSM().getTransitionMaker().unsetDstNode();
	    }
		
	}
	
	private class MotionNodeHandler extends MouseMotionAdapter{
		public void mouseDragged(MouseEvent e){
			if( !draggingTransition ){// Drag Node
				JNode node = (JNode)e.getComponent();
				node.setLocation( node.getX()-node.getWidth()/2+e.getX(), node.getY()-node.getHeight()/2+e.getY() );
			}
			fsmPanel.getFSM().getTransitionMaker().updatePoint(e.getLocationOnScreen());
		}
	}
}
