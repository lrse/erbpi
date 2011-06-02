package extension.model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;

public abstract class ImageMapFeature {
	public abstract void paint(Graphics g, Point offset);

	public void paint(Graphics g) {
		paint(g, new Point());
	}
}

class ImageMapPoint extends ImageMapFeature {
	private Point point;
	private Color color;
	
	public ImageMapPoint(Point point, Color color) {
		this.point = point;
		this.color = color;
	}
	
	public void paint(Graphics g, Point offset) {
		g.setColor(color);
		g.fillOval(offset.x + point.x - 5, offset.y + point.y - 5, 10, 10);
	}
}

class ImageMapLine extends ImageMapFeature {
	private Point p0, p1;
	private int width;
	private Color color;
	
	public ImageMapLine(Point p0, Point p1, int width, Color color) {
		this.p0 = p0;
		this.p1 = p1;
		this.width = width;
		this.color = color;
	}
	
	public void paint(Graphics g, Point offset) {
		Graphics2D g2d = (Graphics2D)g;
		g2d.setColor(color);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setStroke(new BasicStroke(width));
		
		g2d.drawLine(offset.x + p0.x - width/2, offset.y + p0.y - width/2,
				     offset.x + p1.x - width/2, offset.y + p1.y - width/2);
				
	}
}
