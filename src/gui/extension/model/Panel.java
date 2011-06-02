package extension.model;

import java.util.Stack;
import java.util.Vector;

import extension.model.ActuatorBox;
import extension.model.Box;
import extension.model.FunctionBox;
import extension.model.Program;
import extension.model.SensorBox;


public class Panel {
	private String id;
	private String label;
	private Type type;
	private Vector<String> locationIds;
	private Handles handles;
	private Vector<Box> defaultBoxes;
	
	public enum Type {INPUT, BOXES, OUTPUT};
	public enum Handles {LEFT, RIGHT, NONE, BOTH};
	
	public Panel(String id, Type type) {
		this.id = id;
		this.label = id;
		this.type = type;
		this.locationIds = new Vector<String>();
		this.handles = Handles.RIGHT;
		this.defaultBoxes = new Vector<Box>();
	}
	public Panel(String id, String label, Type type) {
		this.id = id;
		this.label = label == null? id : label;
		this.type = type;
		this.locationIds = new Vector<String>();
		this.handles = Handles.RIGHT;
		this.defaultBoxes = new Vector<Box>();
	}
	
	public void setHandles(Handles handles) {
		this.handles = handles;
	}
	public Handles getHandles() {
		return handles;
	}	
	public Type getType() {
		return type;
	}
	public String getLabel() {
		return label;
	}

	public void addLocationId(String locationId) {
		if( !locationIds.contains(locationId) )
			locationIds.add(locationId);
	}
	
	public boolean accepts(Box box, Program program) {
		return accepts(box, program, false);
	}
	
	public boolean accepts(Box box, Program program, boolean forced) {
		if( type == Type.INPUT && box instanceof SensorBox )
			return true;
		else if( type == Type.OUTPUT && box instanceof ActuatorBox ) {
			String locationId = ((ActuatorBox)box).getLocation();
			if( locationId == null || locationIds.contains(locationId) )
				return true;
			else
				return false;
		} else if( type == Type.BOXES && box instanceof FunctionBox ) {
			Stack<Box> stack = new Stack<Box>();
			stack.push(box);
			while( !stack.empty() ) {
				Box b = stack.pop();
				for( Box dst: program.getConnectionsFrom(b) ) {
					String location = dst.getLocation();
					if( location != null && locationIds.contains(location) ) {
						return true;
					}
					stack.push(dst);
				}
			}
			return forced;
		} else
			return false;
	}
	
	public void addDefaultBox(Box box) {
		defaultBoxes.add(box);
	}
	public Iterable<Box> getDefaultBoxes() {
		return defaultBoxes;
	}
}
