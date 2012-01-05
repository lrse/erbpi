package extension.gui.elements;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class VirtualElement
{
	private String id;
	private String description;

	VirtualElement(String id)
	{
		this.id=id;
		this.description=id;
	}
	
	VirtualElement(Element domSpec)
	{
		this.id = domSpec.getAttribute("id");
		this.description = domSpec.getAttribute("descripcion");
		deserialize(domSpec);
	}
	
	public final String getId() { return id; }
	
	public final String getDescription() { return description; }

	public final void setDescription(String description) { this.description = description; }
	
	/**
	 *  Deberia devolver un arreglo con las operaciones
	 *  permitidas sobre ese elemento.
	 *  */
	public abstract String[] getOperations();
	
	/**
	 * Deberia devolver el tag xml para el comportamiento
	 * */
	public abstract Element serialize(Document document);
	
	/**
	 * Deberia tomar el tag xml generado por serialize()
	 * y devolver el objeto igual al que fue serializado.
	 * */
	public abstract void deserialize(Element domSpec);
	
	@Override
	/**
	 * Un nombre declarativo para mostrar al elemento en el programa.
	 * */
	public String toString() { return this.description; }
	
	protected int getNumberFromId()
	{
		String[] split = id.split("\\.");
		return Integer.parseInt(split[split.length-1]);
	}
	
	void serialize(Element element)
	{
		element.setAttribute("id", getId());
		element.setAttribute("descripcion", getDescription());
	}
}
