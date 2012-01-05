package extension.model;

import java.util.HashSet;
import java.util.Set;

import extension.gui.elements.Box;
import extension.gui.elements.Box.BoxType;
import extension.model.program.PanelListener;

public class Panel implements ProgramListener
{
	private BehaviorProgram program;
	
	private String label;
	private BoxType type;
	private Location location;
	private Handles handles;
	
	public enum Location {LEFT,RIGHT,NONE};
	public enum Handles {LEFT, RIGHT, NONE, BOTH};
	
	private Set<Box> boxes;
	private HashSet<PanelListener> listeners;
	
	public Panel(BehaviorProgram program, String id, BoxType type, Location location)
	{
		this.label = id;
		this.type = type;
		this.handles = Handles.RIGHT;
		this.location = location;
		
		this.program = program;
		
		boxes = new HashSet<Box>();
		listeners = new HashSet<PanelListener>();
		
		program.addListener(this);
		for ( Box box : this.program.getBoxes() )
    		boxAdded(box);
	}
	
	public void reload(BehaviorProgram program)
	{
		for ( Box box : new HashSet<Box>(this.boxes) )
    		boxRemoved(box);
		this.program.removeListener(this);
		
		this.program=program;
		
		this.program.addListener(this);
		for ( Box box : this.program.getBoxes() )
    		boxAdded(box);
	}
	
	public Handles getHandles()				{ return handles; }
	public void setHandles(Handles handles)	{ this.handles = handles; }
	
	public BoxType getType()		{ return type; }
	public String getLabel()		{ return label; }
	public Location getLocation()	{ return location; }
	
	public boolean accepts(Box box)
	{
		return this.type==box.getBoxType() && this.location==box.getLocation();
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
	
	public void addListener(PanelListener listener) {
		if( !listeners.contains(listener) )
			listeners.add(listener);
	}
	
	public void removeListener(PanelListener listener) {
		listeners.remove(listener);
	}

	@Override public void connectionAdded(Box src, Box dst) {}

	@Override public void connectionRemoved(Box src, Box dst) {}

	@Override
	public void boxAdded(Box box)
	{
		if ( this.accepts(box) )
		{
			boxes.add(box);
			for( PanelListener listener: listeners )
				listener.boxAdded(box);
			
			System.out.println("new box added id: "+box.getId());
		}
	}

	@Override
	public void boxRemoved(Box box)
	{
		if ( boxes.remove(box) )
			for( PanelListener listener: listeners )
				listener.boxRemoved(box);
		else
			System.err.println("Panel::boxRemoved - trying to remove a box not contained");
	}
	
	public BehaviorProgram getProgram() { return this.program; }
	
	public Iterable<Box> getBoxes() { return this.boxes; }
}
