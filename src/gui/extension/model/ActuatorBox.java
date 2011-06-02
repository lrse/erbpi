package extension.model;

import javax.swing.Icon;

public class ActuatorBox extends Box {
	private ActuatorType actuatorType;
	private boolean presentByDefault;
	
	public ActuatorBox(String id, String label, ActuatorType type) {
		super(id,label);
		actuatorType = type;
		this.presentByDefault = false;
	}
	public ActuatorBox(String id, String label, ActuatorType type, boolean presentByDefault) {
		this(id,label,type);
		this.presentByDefault = presentByDefault;
	}
	
	public Icon getImage() {
		return actuatorType.getImage();
	}
	
	public boolean canConnectFrom(Box b) {
		return true;
	}
	public String getType() {
		return "actuator";
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
