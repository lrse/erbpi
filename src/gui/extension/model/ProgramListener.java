package extension.model;

import java.awt.Point;

import extension.model.elements.Box;

public interface ProgramListener{
	public void connectionAdded( Box src, Box dst );
	public void connectionRemoved( Box src, Box dst );
	public void boxAdded( Box box );
	public void boxRemoved( Box box );
	public void boxSet( Box box, Point A, Point B ); // esta es para llamar a JParametrosCaja...
	public void robotChanged( Robot robot );
}
