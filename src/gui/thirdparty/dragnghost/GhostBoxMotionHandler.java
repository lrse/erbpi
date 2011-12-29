package thirdparty.dragnghost;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.SwingUtilities;

public class GhostBoxMotionHandler extends MouseMotionAdapter
{
    private GhostGlassPane glassPane;

	public GhostBoxMotionHandler(GhostGlassPane glassPane) {
		this.glassPane = glassPane;
	}

	public void mouseDragged(MouseEvent e)
    {
        Component c = e.getComponent();

        Point p = (Point) e.getPoint().clone();
        SwingUtilities.convertPointToScreen(p, c);
        SwingUtilities.convertPointFromScreen(p, glassPane);
        ((GhostDragObjectExtension)(glassPane.getExtension())).setPoint(p);

        glassPane.repaint();
    }
}