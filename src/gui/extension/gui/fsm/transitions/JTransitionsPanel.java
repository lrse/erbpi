package extension.gui.fsm.transitions;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import extension.gui.fsm.JFSMPanel;
import extension.gui.fsm.JNode;
import extension.model.BehaviorNode;
import extension.model.FSM;
import extension.model.FSMListener;
import extension.model.Transition;
import extension.model.TransitionMaker;
import extension.model.TransitionMakerListener;

public class JTransitionsPanel extends JPanel implements FSMListener, TransitionMakerListener
{
	private static final long serialVersionUID = 1L;
	private FSM fsm = null;
	private TransitionMaker transitionMaker = null;
	private Map<Transition,JTransition> transitions = new HashMap<Transition, JTransition>();
	private JFSMPanel fsmPanel = null;
	
	private Stroke stroke = new BasicStroke(3);
	private Color color = Color.BLACK;
	
	public JTransitionsPanel(FSM fsm, JFSMPanel fsmPanel)
	{
		this.fsm = fsm;
		this.fsmPanel=fsmPanel;
		
		fsm.addListener(this);
		transitionMaker = fsm.getTransitionMaker();
		transitionMaker.addListener(this);
		
		setLayout(new GridLayout(1,1));
		
		// add behaviors
		for( BehaviorNode behavior: fsm.getBehaviors() )
			this.behaviorAdded(behavior);

		// add transitions
		for( Transition trs: fsm.getTransitions() )
			this.transitionAdded(trs);
	}
	
	@Override
	public void transitionAdded(Transition transition)
	{
		JTransition jTransition = new JTransition(transition,fsm,this);
		transitions.put(transition, jTransition);
		
		this.add(jTransition);
		
		UpdateLayout();
	}
	
	@Override
	public void transitionRemoved(Transition transition)
	{
		JTransition jTransition = transitions.remove(transition);
		
		if ( jTransition != null )
			this.remove(jTransition);
		else
			throw new IllegalArgumentException("La transicion no era parte del panel de transiciones.");
		
		UpdateLayout();
	}

	@Override
	public void behaviorRemoved(BehaviorNode behavior) {}
	
	@Override
	public void behaviorAdded(BehaviorNode behavior) {}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		paintTransitions(g2d);
		paintNewTransition(g2d);
		super.paint(g);
	}
		
	@Override
	public void doLayout() {
		for( Component c: getComponents() ) {
			c.doLayout();
		}
	}
	
	private void paintTransitions(Graphics2D g2d) {
		for( Component c: getComponents() ) {
			if( c instanceof JTransition )
			{
				Point psrc = ((JTransition)c).getSourceLocationOnParent();
				Point pdst = ((JTransition)c).getDestinationLocationOnParent();
				
		        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		        g2d.setStroke(stroke);
				
				float radio = JTransitionsPanel.getCircleRadio(psrc,pdst);
				Point center = JTransitionsPanel.getCircleCenter(psrc, pdst, radio, true);
				
				/*
				 * DEBUG
				 * 
				g2d.setColor(Color.BLUE);
				g2d.drawLine(psrc.x, psrc.y, center.x, center.y);
				g2d.drawLine(pdst.x, pdst.y, center.x, center.y);
				
				Point arcMiddle = JTransitionsPanel.getArcMiddle(psrc, pdst, true);
				
				g2d.setColor(Color.MAGENTA);
				g2d.drawLine(psrc.x, psrc.y, arcMiddle.x, arcMiddle.y);
				g2d.drawLine(pdst.x, pdst.y, arcMiddle.x, arcMiddle.y);
				 */
				
				int x = (int)(center.x-radio);
				int y = (int)(center.y-radio);
				int width = (int)(2*radio);
				int height = (int)(2*radio);
				int srcAngle = (int) (Math.atan2(psrc.y-center.y, psrc.x-center.x)*180/Math.PI);
				int endAngle = (int) (Math.atan2(pdst.y-center.y, pdst.x-center.x)*180/Math.PI);
				
				// si estoy dibujando el arco largo (a veces pasa)
				// hago un swap para dibujar el arco corto
				// TODO: no funca, a veces se dibuja el largo
				if ( srcAngle-endAngle < 0 || 180 < srcAngle-endAngle )
				{
					int temp = endAngle;
					endAngle = srcAngle;
					srcAngle = temp;
				}
				
				/*
				 * debug
				
				g2d.setColor(Color.GREEN);
				g2d.drawRect(x, y, width, height);
				
				g2d.setColor(Color.YELLOW);
				g2d.drawArc(x, y, width, height, 0, 360);
				
				 */
				
				// le tengo que restar a 360 porque los grados se miden en un sistema de coordenadas normal,
				// mientras que en las coordenadas de los graficos el 'y' crece hacia abajo 
				g2d.setColor(color);
				// System.out.println("srcAngle-endAngle: "+(srcAngle-endAngle)+" al reves: "+(endAngle-srcAngle));
				g2d.drawArc(x, y, width, height, 360-srcAngle, srcAngle-endAngle);
			}
		}
	}
	
	private static float getCircleRadio(Point src, Point dst)
	{
		return (float) src.distance(dst);
	}
	
	private static Point getCircleCenter(Point src, Point dst, float radio, boolean side)
	{
		Point P = new Point((src.x+dst.x)/2,(src.y+dst.y)/2);
		double feo = Math.sqrt( (radio*radio/src.distanceSq(dst)) - 0.25f );
		Point v = new Point(dst.y-src.y,src.x-dst.x);
		
		if (side)
			return new Point( (int)(P.x + feo*v.x) , (int)(P.y + feo*v.y) );
		else
			return new Point( (int)(P.x - feo*v.x) , (int)(P.y - feo*v.y) );
	}
	
	static Point getArcMiddle(Point src, Point dst, boolean side)
	{
		float radio = getCircleRadio(src, dst);
		
		Point P = new Point((src.x+dst.x)/2,(src.y+dst.y)/2);
		double aux = radio/src.distance(dst);
		double feo = aux - Math.sqrt( aux*aux - 0.25f );
		Point v = new Point(dst.y-src.y,src.x-dst.x);
		
		if (side)
			return new Point( (int)(P.x - feo*v.x) , (int)(P.y - feo*v.y) );
		else
			return new Point( (int)(P.x + feo*v.x) , (int)(P.y + feo*v.y) );
	}
	
	private void paintNewTransition(Graphics2D g2d) {
		if( transitionMaker.isEnabled() ) {
			Point srcPoint = (Point) transitionMaker.getSrcPoint().clone();
			Point dstPoint = (Point) transitionMaker.getDstPoint().clone();
			
			SwingUtilities.convertPointFromScreen(srcPoint, this);
			SwingUtilities.convertPointFromScreen(dstPoint, this);
						
			g2d.setStroke(stroke);
			if( transitionMaker.isAllowed() )
				g2d.setColor(Color.GREEN);
			else
				g2d.setColor(color);
			g2d.drawLine(srcPoint.x, srcPoint.y, dstPoint.x, dstPoint.y);
		}
	}

	public void newTransitionStarted(TransitionMaker maker) {
		UpdateLayout();
	}
	
	public void newTransitionUpdated(TransitionMaker maker) {
		UpdateLayout();
	}
	
	public void newTransitionStopped(TransitionMaker maker) {
		UpdateLayout();
	}
	
	JNode getView(BehaviorNode behavior) { return fsmPanel.getView(behavior); }
	
	private void UpdateLayout()
	{
		this.fsmPanel.validate();
		this.fsmPanel.repaint();
	}
}
