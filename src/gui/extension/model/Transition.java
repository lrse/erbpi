package extension.model;

import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import extension.gui.fsm.transitions.Action;
import extension.gui.fsm.transitions.Condition;

public class Transition
{
	private BehaviorNode source;
	private BehaviorNode destination;
	public Set<Condition> conditions = new HashSet<Condition>();
	public Set<Action> actions = new HashSet<Action>();
	
	Transition(BehaviorNode source, BehaviorNode destination)
	{
		if ( source==null )
			System.err.println("Source is null.");
		
		if ( destination==null )
			System.err.println("destination is null.");
		
		this.source = source;
		this.destination = destination;
	}
	
	Transition( Element domSpec, FSM fsm )
	{
		this.source = fsm.getBehaviorById(domSpec.getAttribute("id_origen"));
		this.destination = fsm.getBehaviorById(domSpec.getAttribute("id_destino"));
		
		if ( source==null )
			System.err.println("Source is null. Behavior id = "+domSpec.getAttribute("id_origen"));
		
		if ( destination==null )
			System.err.println("destination is null. Behavior id = "+domSpec.getAttribute("id_destino"));
		
		NodeList conditionList = ((Element)domSpec.getElementsByTagName("condiciones").item(0)).getElementsByTagName("condicion");
		for ( int i=0; i<conditionList.getLength(); i++  )
			this.addCondition( new Condition( (Element)conditionList.item(i), fsm ) );
		
		NodeList actionList = ((Element)domSpec.getElementsByTagName("actualizaciones").item(0)).getElementsByTagName("actualizacion");
		for ( int i=0; i<actionList.getLength(); i++  )
			this.addAction( new Action( (Element)actionList.item(i), fsm ) );
	}
	
	public Condition addNewCondition()
	{
		Condition newCondition = new Condition();
		this.conditions.add(newCondition);
		return newCondition;
	}
	
	public Action addNewAction()
	{
		Action newAction = new Action();
		this.actions.add(newAction);
		return newAction;
	}
	
	public void addCondition(Condition condition) { this.conditions.add(condition); }
	public void removeCondition(Condition condition) { this.conditions.remove(condition); }
	
	public void addAction(Action action) { this.actions.add(action); }
	public void removeAction(Action action) { this.actions.remove(action); }
	
	public BehaviorNode getSrc() { return source; }
	public BehaviorNode getDst() { return destination; }
	
	boolean hasSrc(Program program) { return program==this.source.getProgram(); }
	boolean hasDst(Program program) { return program==this.destination.getProgram(); }
	
	boolean connects(BehaviorNode src,BehaviorNode dst) { return src==this.source && dst==this.destination; }
	
	Element serialize(Document document)
	{
		// serialize transition
		
		Element transitionElement = document.createElement("transicion");
		transitionElement.setAttribute("id_origen", source.getProgram().getId());
		transitionElement.setAttribute("id_destino", destination.getProgram().getId());
		
		// serialize conditions
		
		Element conditionsElement = document.createElement("condiciones");
		transitionElement.appendChild(conditionsElement);
		
		for ( Condition condition : conditions )
			conditionsElement.appendChild( condition.serialize(document) );
		
		// serialize actions
		
		Element actionsElement = document.createElement("actualizaciones");
		transitionElement.appendChild(actionsElement);
		
		for ( Action action : actions )
			actionsElement.appendChild( action.serialize(document) );
		
		// return full serialized transition
		
		return transitionElement;
	}
}
