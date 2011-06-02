package extension.model;

import java.util.Vector;

import extension.model.Panel;

public class Diagram {
	private String id;
	private String label;
	private Type type;
	private Vector<Panel> panels;

	public enum Type {SINGLE, DOUBLE};
	
	public Diagram(String id, Type type) {
		this.id = id;
		this.label = id;
		this.type = type;
		this.panels = new Vector<Panel>();
	}
	public Diagram(String id, String label, Type type) {
		this.id = id;
		this.label = label == null? id : label;
		this.type = type;
		this.panels = new Vector<Panel>();
	}
	
	public void addPanel(Panel panel) {
		panels.add(panel);
	}
	public Iterable<Panel> getPanels() {
		return panels;
	}
	
	public Type getType() {
		return type;
	}
}
