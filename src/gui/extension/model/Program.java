package extension.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

abstract public class Program
{
	static private int nextId = 0;
	
	private int numericId;
	
	private String description;

	public Program()
	{
		this.numericId = nextId;
		this.description = getId();
		nextId++;
	}
	
	public Program(Element domSpec, Robot robot)
	{
		String[] split = domSpec.getAttribute("id").split("\\.");
		this.numericId = Integer.parseInt(split[split.length-1]);
		this.description = domSpec.getAttribute("descripcion");
		
		if ( numericId >= nextId )
			nextId = numericId+1;
		
		deserialize(domSpec, robot);
	}
	
	public String getDescription() { return this.description; }
	public void setDescription(String description) { this.description = description; }
	
	public String getId() { return "comportamiento."+numericId; }
	protected void serializeId(Element behaviorElement) { behaviorElement.setAttribute("id", getId()); }
	protected void serializeDescription(Element behaviorElement) { behaviorElement.setAttribute("descripcion",getDescription()); }
	
	abstract public Element serialize(Document document);
	abstract protected void deserialize(Element domSpec, Robot robot);
}
