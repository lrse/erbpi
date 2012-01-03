package extension.model;

import extension.model.elements.Box;

public interface ProgramListener
{
	public void connectionAdded( Box src, Box dst );
	public void connectionRemoved( Box src, Box dst );
	public void boxAdded( Box box );
	public void boxRemoved( Box box );
}
