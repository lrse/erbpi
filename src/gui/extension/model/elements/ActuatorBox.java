package extension.model.elements;

import javax.swing.Icon;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import extension.model.ActuatorType;
import extension.model.BehaviorProgram;
import extension.model.Panel.Location;

public class ActuatorBox extends Box
{
	private ActuatorType actuatorType;
	
	public ActuatorBox(String id, ActuatorType type, BehaviorProgram program, Location location)
	{
		super(id,program,location);
		
		if (location==Location.NONE)
			System.err.println("ActuatorBox location cant be NONE!!!");
		
		actuatorType = type;
	}
	
	public ActuatorType getActuatorType()				{ return actuatorType; }
	
	@Override public String getType()					{ return "actuator"; }
	@Override public Icon getImage()					{ return actuatorType.getImage(); }
	@Override public boolean showsHighlight()			{ return true; }
	@Override protected boolean canConnectFrom(Box b)	{ return true; }
	
	@Override 
	public Element serialize(Document document)
	{
		Element actuatorElement = document.createElement("actuador");
		actuatorElement.setAttribute("id", getId());
		
		Element entriesElement = document.createElement("entradas");
		actuatorElement.appendChild( entriesElement );
		
		for ( Box input : program.getConnectionsTo(this) )
		{
			Element entryElement = document.createElement("entrada");
			entriesElement.appendChild( entryElement );
			
			entryElement.setAttribute("id", input.getId());
		}
		
		return actuatorElement;
	}

	@Override
	public void deserialize(Element domSpec)
	{
		super.deserialize(domSpec);
		// TODO Auto-generated method stub
	}
}
