package extension.model;

import javax.swing.Icon;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SensorBox extends Box {
	private SensorType sensorType;
	private boolean presentByDefault;
	
	public SensorBox(String id, SensorType type) {
		super(id);
		this.sensorType = type;		
	}
	public SensorBox(String id, SensorType type, boolean presentByDefault) {
		this(id,type);
		this.presentByDefault = presentByDefault;
	}

	public SensorType getSensorType() {
		return sensorType;
	}
	public String getType() {
		return "sensor";
	}
	public Icon getImage() {
		return sensorType.getImage();
	}
	public boolean isPresentByDefault() {
		return presentByDefault;
	}

	public void setFocused(boolean b) {		
		if( b )
			Program.getCurrentProgram().getRobot().getImageMap().focusFeature(id);
		else 
			Program.getCurrentProgram().getRobot().getImageMap().unfocusFeatures();
		super.setFocused(b);
	}
}
