package extension.model;

import java.util.HashSet;

import javax.swing.JComponent;

import extension.model.BoxListener;
import extension.model.ConnectionMaker;
import extension.model.Program;


public class Box  {
	protected String id;
	protected String label;
	protected String location;
	protected JComponent ui = null;
	//protected Program program = null;
	
	public Box(String id) {
		this.id = id;
		this.label = id;
	}
	public Box(String id, String label) {
		this.id = id;
		this.label = (label == null || label.length() == 0)? id : label;
	}
	public String getId() {
		return id;
	}
	public String getLabel() {
		return label;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	
	public JComponent getUi() {
		return ui;
	}
	public void setUi(JComponent ui) {
		this.ui = ui;
	}

	/*
	public void setProgram(Program program) {
		this.program = program;
	}
	public Program getProgram() {
		return program;
	}
	*/
	
	public void setFocused(boolean b) {
		ConnectionMaker maker = Program.getCurrentProgram().getConnectionMaker();
		if( maker.isEnabled() ) {
			boolean allowed = this.canConnectFrom(maker.getSrc());
			maker.setDst(this, b && allowed);			
		}
		for( BoxListener listener: listeners )
			listener.focusChanged(this, b);
	}
	
	public boolean canConnectFrom(Box b) {
		return false;
	}
	
	private HashSet<BoxListener> listeners = new HashSet<BoxListener>();
	public void addListener(BoxListener listener) {
		listeners.add(listener);
	}
}




