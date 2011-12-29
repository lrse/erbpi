package extension.model;

import extension.model.elements.ActuatorBox;
import extension.model.elements.Box;
import extension.model.elements.FunctionBox;
import extension.model.elements.SensorBox;

public class Panel
{
	private String label;
	private Type type;
	private Location location;
	private Handles handles;
	
	public enum Type {INPUT, BOXES, OUTPUT};
	public enum Location {LEFT,RIGHT,NONE};
	public enum Handles {LEFT, RIGHT, NONE, BOTH};
	
	public Panel(String id, Type type, Location location)
	{
		this.label = id;
		this.type = type;
		this.handles = Handles.RIGHT;
		this.location = location;
	}
	
	public Handles getHandles()				{ return handles; }
	public void setHandles(Handles handles)	{ this.handles = handles; }
	
	public Type getType()			{ return type; }
	public String getLabel()		{ return label; }
	public Location getLocation()	{ return location; }
	
	public boolean accepts(Box box)
	{
		Location boxLocation = box.getLocation();
		
		if( type == Type.INPUT && box instanceof SensorBox )
			return true;
		else if( type == Type.OUTPUT && box instanceof ActuatorBox )
			return ( location==boxLocation );
		else if( type == Type.BOXES && box instanceof FunctionBox )
			return ( location==boxLocation ); 
		else if ( type!=Type.INPUT && type!=Type.OUTPUT && type!=Type.BOXES )
				System.err.println("box type not supported!!!");
		
		return false;
	}
	
	static public Location deserializeLocation(String str)
	{
		if (str.equals("left"))			return Location.LEFT;
		else if (str.equals("right"))	return Location.RIGHT;
		else							return Location.NONE;
	}
	
	static public String serializeLocation(Location location)
	{
		if (location==Location.LEFT)		return "left";
		else if (location==Location.RIGHT)	return "right";
		else								return "none";
	}
}
