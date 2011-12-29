package extension.model.elements;

import javax.swing.Icon;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import extension.model.SensorType;
import extension.model.Panel.Location;

public class SensorBox extends Box
{
	private SensorType sensorType;
	
	public SensorBox(String id, SensorType type) {
		super(id,null,Location.NONE);
		this.sensorType = type;		
	}

	public SensorType getSensorType()	{ return sensorType; }
	
	@Override public String getType()				{ return "sensor"; }
	@Override public Icon getImage()				{ return sensorType.getImage(); }
	@Override public boolean showsHighlight()		{ return true; }
	
	public Element serialize(Document document)
	{
		Element sensorElement = document.createElement("sensor");
		sensorElement.setAttribute("id", getId());
		
		return sensorElement;
	}

	@Override
	public void deserialize(Element domSpec)
	{
		super.deserialize(domSpec);
		// TODO Auto-generated method stub
	}
}
