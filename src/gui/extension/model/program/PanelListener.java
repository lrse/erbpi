package extension.model.program;

import extension.model.elements.Box;

public interface PanelListener
{
	public void boxAdded( Box box );
	public void boxRemoved( Box box );
}
