package extension.gui.fsm.transitions;

import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import extension.gui.JRoboticaFrame;
import extension.gui.elements.Counter;
import extension.gui.elements.Timer;
import extension.gui.elements.VirtualElement;
import extension.model.FSM;

public class Action
{
	private VirtualElement element;
	private String operation;
	
	public Action()
	{
		this.element = Action.getElements().firstElement();
		this.operation = this.element.getOperations()[0];
	}
	
	public Action(Element actionElement, FSM fsm)
	{
		// TODO me quede aca! completar
		
		String tipo = actionElement.getAttribute("tipo");
		
		if ( tipo.equals("timer") )
		{
			
			String timerId = actionElement.getAttribute("id_timer");
			this.element = getTimerById(timerId,fsm);
			if ( this.element == null )
				throw new RuntimeException(" Action::Action(Element actionElement, FSM fsm) timer with id "+timerId+" doesnt exist");
			
			this.operation = (this.element.getOperations())[0];
			
		}
		else if ( tipo.equals("contador") )
		{
			String counterId = actionElement.getAttribute("id_contador");
			this.element = getCounterById(counterId,fsm);
			if ( this.element == null )
				throw new RuntimeException(" Action::Action(Element actionElement, FSM fsm) counter with id "+counterId+" doesnt exist");
			
			String operationId = actionElement.getAttribute("accion");
			this.operation = getOperationById(operationId);
			if ( this.operation == null )
				throw new RuntimeException(" Action::Action(Element actionElement, FSM fsm) operation with id "+operationId+" doesnt exist");
		}
		else
		{
			
		}
		
//		<actualizacion id_timer="timer.0" tipo="timer"/>
//      <actualizacion accion="incrementar" id_contador="contador.0" tipo="contador"/>
		
//		this.element = domSpec.getAttribute("umbral");
//		this.operation = domSpec.getAttribute("umbral");
	}
	
	static public Vector<VirtualElement> getElements()
	{
		Vector<VirtualElement> elements = new Vector<VirtualElement>();
		
		for (Timer timer : JRoboticaFrame.getInstance().getFSM().getTimers())
			elements.add(timer);
		
		for (Counter counter : JRoboticaFrame.getInstance().getFSM().getCounters())
			elements.add(counter);
		
		return elements;
	}
	
	public Element serialize(Document document)
	{
		// serialize action
		
		Element actionElement = document.createElement("actualizacion");
		
		if ( element instanceof Timer )
		{
			actionElement.setAttribute("tipo", "timer");
			actionElement.setAttribute("id_timer", element.getId());
		}
		else
		{
			actionElement.setAttribute("tipo", "contador");
			actionElement.setAttribute("id_contador", element.getId());
			actionElement.setAttribute("accion", getOperation());
		}
		
		return actionElement;
	}
	
	private VirtualElement getTimerById(String timerId, FSM fsm)
	{
		for (Timer timer : fsm.getTimers())
			if ( timerId.equals( timer.getId() ) )
				return timer;
		return null;
	}
	
	private VirtualElement getCounterById(String counterId, FSM fsm)
	{
		for (Counter counter : fsm.getCounters())
			if ( counterId.equals( counter.getId() ) )
				return counter;
		return null;
	}
	
	private String getOperationById(String opId)
	{
		for (String op : this.element.getOperations())
			if ( opId.equals(op) )
				return op;
		return null;
	}
	
	public VirtualElement getElement() { return this.element; }
	public void setElement(VirtualElement element) { this.element = element; }
	
	public String getOperation() { return this.operation; }
	public void setOperation(String operation) { this.operation = operation; }
}
