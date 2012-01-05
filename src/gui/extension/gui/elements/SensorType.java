package extension.gui.elements;

import javax.swing.Icon;

public class SensorType {
	private String name;
	private String id;
	private Icon image;
	
	public SensorType(String id, String name, Icon image) {
		this.id = id;
		this.name = name;
		this.image = image;
	}
	
	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public Icon getImage() {
		return image;
	}		
}

