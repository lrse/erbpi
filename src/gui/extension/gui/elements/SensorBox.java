package extension.gui.elements;

import javax.swing.Icon;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import extension.model.Panel.Location;

public class SensorBox extends Box
{
	private SensorType sensorType;
	
	public SensorBox(String id, SensorType type) {
		super(id,null,Location.NONE);
		this.sensorType = type;		
	}

	public SensorType getSensorType()	{ return sensorType; }
	
	@Override public Icon getImage()				{ return sensorType.getImage(); }
	@Override public boolean showsHighlight()		{ return true; }
	@Override public BoxType getBoxType()			{ return BoxType.SENSOR; };
	
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
