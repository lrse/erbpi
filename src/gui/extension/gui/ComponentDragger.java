package extension.gui;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class ComponentDragger implements MouseListener, MouseMotionListener {
	private Point point;
	private Component component;
	
	public ComponentDragger(Component comp) {
		component = comp;
	}
	public boolean isDragging() {
		return point != null;
	}
	public void mousePressed(MouseEvent e) {
		point = new Point(e.getLocationOnScreen());
	}
	public void mouseReleased(MouseEvent e) {
		point = null;
		component.getParent().doLayout();
		component.getParent().getParent().repaint();
	}
	
	public void mouseDragged(MouseEvent e) {
		if( point != null ) {
			Point pori = component.getLocation();
			Point pDelta = new Point((int)(e.getXOnScreen() - point.x), (int)(e.getYOnScreen() - point.getY()));
			Point p = component.getLocation();
			p.translate(pDelta.x, pDelta.y);
			int maxx = component.getParent().getWidth() - component.getWidth();
			int maxy = component.getParent().getHeight() - component.getHeight();
			if( (p.x > maxx) || (p.x < 0) )
				p.x = pori.x;
			if( (p.y > maxy) || (p.y < 0) )
				p.y = pori.y;			
			component.setLocation(p);	
			point = e.getLocationOnScreen();
			component.getParent().getParent().repaint();
		}
	}

	public void mouseClicked(MouseEvent arg0) {
	}
	public void mouseEntered(MouseEvent arg0) {
	}
	public void mouseExited(MouseEvent arg0) {
	}
	public void mouseMoved(MouseEvent arg0) {
	}	
}
