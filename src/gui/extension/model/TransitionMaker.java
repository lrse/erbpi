package extension.model;

import java.awt.Point;
import java.util.HashSet;

import extension.gui.fsm.JNode;

public class TransitionMaker {
	private FSM fsm;
	
	private JNode srcNode = null;
	private JNode dstNode = null;
	private Point srcPoint = null;
	private Point dstPoint = null;
	
	private boolean draggingInProgress = false;
	private boolean validDestinyExists = false;
	
	public TransitionMaker(FSM fsm) {
		this.fsm = fsm;
	}
	
	// el punto p tiene que estar en screen coordinates
	public void start(JNode src, Point p)
	{
		this.draggingInProgress = true;
		this.validDestinyExists = false;
		
		this.srcNode = src;
		this.srcPoint = p;
		this.dstPoint = p;
		this.dstNode = null;
		
		for( TransitionMakerListener listener: listeners )
			listener.newTransitionStarted(this);
	}
	
	public void stop()
	{
		if( validDestinyExists && !fsm.transitionExists(srcNode, dstNode) )
			fsm.addNewTransition(srcNode.getBehavior(),dstNode.getBehavior());
		
		draggingInProgress = false;
		
		for( TransitionMakerListener listener: listeners )
			listener.newTransitionStopped(this);
	}
	
	// el punto p tiene que estar en screen coordinates
	public void updatePoint(Point p)
	{
		this.dstPoint = p;
		for( TransitionMakerListener listener: listeners )
			listener.newTransitionUpdated(this);
	}
	
	public void setDstNode(JNode dst)
	{
		if (dst!=this.srcNode)
		{
			this.dstNode = dst;
			this.validDestinyExists = true;
			
			for( TransitionMakerListener listener: listeners )
				listener.newTransitionUpdated(this);
		}
	}
	
	public void unsetDstNode()
	{
		this.dstNode = null;
		this.validDestinyExists = false;
		
		for( TransitionMakerListener listener: listeners )
			listener.newTransitionUpdated(this);
	}
	
	public boolean isEnabled() {
		return draggingInProgress;
	}
	public boolean isAllowed() {
		return validDestinyExists;
	}
	public JNode getSrc() {
		return srcNode;
	}
	public Point getSrcPoint() {
		return srcPoint;
	}
	public Point getDstPoint() {
		return dstPoint;
	}
	
	private HashSet<TransitionMakerListener> listeners = new HashSet<TransitionMakerListener>();
	public void addListener(TransitionMakerListener listener) {
		listeners.add(listener);
	}
}
