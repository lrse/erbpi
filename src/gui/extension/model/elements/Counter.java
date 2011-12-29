package extension.model.elements;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Counter extends VirtualElement
{
	static private int nextId = 0;
	
	private String[] operations = {"resetear","incrementar","decrementar"};
	
	public Counter() {
		super("contador."+nextId);
		nextId++;
	}
	
	public Counter(Element domSpec) {
		super(domSpec);
	}

	@Override
	public String[] getOperations() {
		return operations;
	}
	
	@Override
	public Element serialize(Document document)
	{
		Element counterElement = document.createElement("contador");
		
		super.serialize(counterElement);
		
		return counterElement;
	}
	
	@Override
	public String getType() {
		return "contador";
	}

	@Override
	public void deserialize(Element domSpec)
	{
		int idNumber = getNumberFromId();
		if ( nextId < idNumber )
			nextId = +1;
	}
}
