package extension.gui.program;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

class ComponentDragger implements MouseListener, MouseMotionListener {
	private Point lastEventPositionOnScreen;
	private Component component;
	
	ComponentDragger(Component comp) {
		component = comp;
	}
	public boolean isDragging() {
		return lastEventPositionOnScreen != null;
	}
	public void mousePressed(MouseEvent e) {
		lastEventPositionOnScreen = new Point(e.getLocationOnScreen());
	}
	public void mouseReleased(MouseEvent e) {
		lastEventPositionOnScreen = null;
		component.getParent().doLayout();
		component.getParent().getParent().repaint();
	}
	
	public void mouseDragged(MouseEvent e) {
		if( lastEventPositionOnScreen != null )
		{
			Point positionDelta = new Point((int)(e.getXOnScreen() - lastEventPositionOnScreen.x), (int)(e.getYOnScreen() - lastEventPositionOnScreen.getY()));
			Point componentPosition = component.getLocation();
			componentPosition.translate(positionDelta.x, positionDelta.y);
			
			int maxx = component.getParent().getWidth() - component.getWidth();
			int maxy = component.getParent().getHeight() - component.getHeight();
			
			// reviso que no se salga de los limites de la caja padre 
			if (componentPosition.x > maxx) componentPosition.x = maxx; else if (componentPosition.x < 0) componentPosition.x = 0;
			if (componentPosition.y > maxy) componentPosition.y = maxy; else if (componentPosition.y < 0) componentPosition.y = 0;
			
			component.setLocation(componentPosition);	
			lastEventPositionOnScreen = e.getLocationOnScreen();
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
