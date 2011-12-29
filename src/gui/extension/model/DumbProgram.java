package extension.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import extension.gui.JRoboticaFrame;

public class DumbProgram extends Program
{
	public DumbProgram()
	{
		super();
	}
	
	public DumbProgram( Element domSpec, Robot robot )
	{
		super(domSpec,robot);
	}

	@Override
	public Element serialize(Document document)
	{
		// serialize behavior

		Element behaviorElement = document.createElement("comportamiento");
		
		serializeId(behaviorElement);
		serializeDescription(behaviorElement);
		
		// serialize transitions
		// TODO estas deberian estar afuera de los comportamientos.
		// Estan asi por performance del core, asi que habria que tocarlo un poquito
		
		Element transitionsElement = document.createElement("transiciones");
		behaviorElement.appendChild(transitionsElement);
		
		for( Transition transition : JRoboticaFrame.getInstance().getFSM().getTransitionsFrom(this) )
			transitionsElement.appendChild( transition.serialize(document) );
		
		// return full serialized behavior
		
		return behaviorElement;
	}

	@Override
	protected void deserialize(Element domSpec, Robot robot) {}
}
