package extension.gui.elements;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import extension.utils.UniqueIdGenerator;

public class Timer extends VirtualElement
{
	static private UniqueIdGenerator idPool = new UniqueIdGenerator();
	
	private String[] operations = {"resetear"};
	
	public Timer() {
		super("timer."+idPool.getNewId());
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
		
		Timer.idPool.updateId(idNumber);
	}
	
}
