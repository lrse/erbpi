package extension.model.elements;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Timer extends VirtualElement
{
	static private int nextId = 0;
	
	private String[] operations = {"resetear"};
	
	public Timer() {
		super("timer."+nextId);
		nextId++;
	}
	
	public Timer(Element domSpec) {
		super(domSpec);
	}
	
	@Override
	public String[] getOperations() {
		return operations;
	}
	
	@Override
	public Element serialize(Document document)
	{
		Element counterElement = document.createElement("timer");
		
		super.serialize(counterElement);
		
		return counterElement;
	}
	
	@Override
	public void deserialize(Element domSpec)
	{
		int idNumber = getNumberFromId();
		if ( nextId < idNumber )
			nextId = +1;
	}
	
	@Override
	public String getType() {
		return "timer";
	}
}
