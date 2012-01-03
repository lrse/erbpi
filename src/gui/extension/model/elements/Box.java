package extension.model.elements;

import javax.swing.Icon;
import javax.swing.JComponent;

import org.w3c.dom.Element;

import extension.model.BehaviorProgram;
import extension.model.ConnectionMaker;
import extension.model.Panel;
import extension.model.Panel.Location;

/*
 * Clase abstracta de la cual derivan SensorBox, FunctionBox y ActuatorBox.
 */

public abstract class Box extends VirtualElement implements Cloneable
{
	public enum BoxType {SENSOR, FUNCTION, ACTUATOR};
	
	private Location location;
	protected JComponent ui = null;
	
	// TODO esta variable se va cuando las conecciones
	// se serializen en el programa y no como entrada de cada box
	protected BehaviorProgram program;
	
	public Box( String id, BehaviorProgram program, Location location ){
		super(id);
		this.location = location;
		this.program = program;
	}
	
	public Box( Element domSpec, BehaviorProgram program )
	{
		super(domSpec);
		
		this.program=program;
		
		deserialize(domSpec);
	}
	
	public abstract Icon getImage();
	public abstract boolean showsHighlight();
	public abstract BoxType getBoxType();
	
	@Override
	public void deserialize(Element domSpec)
	{
		this.location = deserializeLocation(domSpec);
	}
	
	protected String serializeLocation() { return Panel.serializeLocation(this.location); }
	private Location deserializeLocation(Element domSpec) { return Panel.deserializeLocation(domSpec.getAttribute("location")); }
	
	@Override
	public Object clone() throws CloneNotSupportedException{
		try{
			Box boxClonado = (Box) super.clone();
			return boxClonado;
		}
		catch(CloneNotSupportedException e){
			return null;
		}
	  }
	
	public JComponent getUi()			{ return ui; }
	public void setUi(JComponent ui)	{ this.ui = ui; }
	
	public void setFocused(ConnectionMaker maker, boolean focused)
	{
		if( maker.isEnabled() ) {
			boolean allowed = this.canConnectFrom(maker.getSrc());
			maker.setDst(this, focused && allowed);			
		}
	}
	
	protected boolean canConnectFrom(Box b)		{ return false; }
	public final Location getLocation()			{ return location;	}
	
	/**
	 * No existen operaciones por software a estos elementos.
	 * */
	@Override public String[] getOperations() { return new String[0]; }
}




