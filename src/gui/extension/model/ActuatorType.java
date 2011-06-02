package extension.model;

import javax.swing.Icon;

public class ActuatorType {
	private String name;
	private String id;
	private Icon image;
	
	public ActuatorType(String id, String name, Icon image) {
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

