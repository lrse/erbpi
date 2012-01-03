package extension.utils;

public class UniqueIdGenerator
{
	private int nextId = 0;
	
	public int getNewId()
	{
		int newId = nextId;
		nextId++;
		return newId;
	}
	
	public void updateId(int newId)
	{
		if ( newId >= nextId )
			nextId = newId+1;
	}
}
