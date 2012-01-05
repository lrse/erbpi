package extension.gui.elements;

import javax.swing.Icon;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
	
	@Override public Icon getImage()					{ return actuatorType.getImage(); }
	@Override public boolean showsHighlight()			{ return true; }
	@Override protected boolean canConnectFrom(Box b)	{ return true; }
	@Override public BoxType getBoxType()				{ return BoxType.ACTUATOR; };
	
	@Override 
	public Element serialize(Document document)
	{
		Element actuatorElement = document.createElement("actuador");
		actuatorElement.setAttribute("id", getId());
		
		return actuatorElement;
	}

	@Override
	public void deserialize(Element domSpec)
	{
		super.deserialize(domSpec);
		// TODO Auto-generated method stub
	}
}
