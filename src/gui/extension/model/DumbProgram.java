package extension.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

class DumbProgram extends Program
{
	public DumbProgram()
	{
		super();
		
		setDescription("nodo inicial");
	}
	
	DumbProgram( Element domSpec, Robot robot )
	{
		super(domSpec,robot);
		
		setDescription("nodo inicial");
	}

	@Override
	public Element serialize(Document document)
	{
		// serialize behavior

		Element behaviorElement = document.createElement("comportamiento");
		
		serializeId(behaviorElement);
		serializeDescription(behaviorElement);
		
		// return full serialized behavior
		
		return behaviorElement;
	}

	@Override
	protected void deserialize(Element domSpec, Robot robot) {}
}
