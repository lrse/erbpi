package thirdparty.dragnghost;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

class GhostDragObjectExtension implements GlassPaneExtension {
	private JPanel panel;
	private AlphaComposite composite;
    private BufferedImage image;
    private Point location;
	
    public GhostDragObjectExtension(JPanel panel, BufferedImage image, Point location) {
    	this.panel = panel;
    	this.image = image;
    	this.location = location;
        composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
    }
    
    public void setPoint(Point location)
    {
        this.location = location;
    }
    
    public void paint(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        g2.setComposite(composite);
        g2.drawImage(image,
                     (int) (location.getX() - (image.getWidth(panel)  / 2)),
                     (int) (location.getY() - (image.getHeight(panel) / 2)),
                     null);
    }    
};

public class GhostGlassPane extends JPanel implements MouseListener 
{
    private GlassPaneExtension extension = null;
    
    public GhostGlassPane()
    {
    	setVisible(true);
        setOpaque(false);
    }
    
    public void setExtension(GlassPaneExtension extension) {
    	this.extension = extension;
    	setVisible(extension != null);
    }
    
    public GlassPaneExtension getExtension() {
    	return extension;
    }

    public void paintComponent(Graphics g)
    {
    	if (extension != null)
    		extension.paint(g);
    }

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		if (extension != null && extension instanceof MouseListener)
			((MouseListener)extension).mouseEntered(arg0);
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		if (extension != null && extension instanceof MouseListener)
			((MouseListener)extension).mouseExited(arg0);
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}
}