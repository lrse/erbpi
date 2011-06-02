package extension.model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.JComponent;






public class ImageMap {
	public Icon icon;
	public HashMap<String,ImageMapFeature> features = new HashMap<String,ImageMapFeature>();
	public ImageMapFeature focusedFeature;
	JComponent ui;
	
	public ImageMap(Icon icon) {
		this.icon = icon;
		this.focusedFeature = null;
	}
	
	public ImageMapFeature getFocusedFeature() {
		return focusedFeature;
	}
	public void unfocusFeatures() {
		focusedFeature = null;
		if( ui != null )
			ui.repaint();
	}
	public void focusFeature(String id) {
		focusedFeature = features.get(id);
		if( ui != null ) {
			ui.repaint();
		}
	}
	public void setUi(JComponent ui) {
		this.ui = ui;
	}
	public JComponent getUi() {
		return ui;
	}
	
	public void addPoint(String id, Point p, Color color) {
		features.put(id,new ImageMapPoint(p,color));	
	}

	public void addLine(String id, Point p0, Point p1, int width, Color c) {
		features.put(id, new ImageMapLine(p0, p1, width, c));
	}
};
