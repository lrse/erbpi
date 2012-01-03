package extension.model.elements;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import extension.utils.UniqueIdGenerator;

public class Counter extends VirtualElement
{
	static private UniqueIdGenerator idPool = new UniqueIdGenerator();
	
	private String[] operations = {"resetear","incrementar","decrementar"};
	
	public Counter() {
		super("contador."+Counter.idPool.getNewId());
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
	public void deserialize(Element domSpec)
	{
		int idNumber = getNumberFromId();
		Counter.idPool.updateId(idNumber);
	}
}
