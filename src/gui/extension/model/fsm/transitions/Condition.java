package extension.model.fsm.transitions;

import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import extension.gui.JRoboticaFrame;
import extension.model.FSM;
import extension.model.elements.Counter;
import extension.model.elements.SensorBox;
import extension.model.elements.Timer;
import extension.model.elements.VirtualElement;

public class Condition
{
	private static String[] operators = {"<","<=","=",">=",">"};
	
	public VirtualElement triggerElement = null;
	public String operator = null;
	public Number threshold = null;

	public Condition()
	{
		this.triggerElement = getDefaultTriggerElement();
		this.operator = getDefaultOperator();
		this.threshold = getDefaultThreshold();
	}
	
	public Condition(Element conditionElement, FSM fsm)
	{
		// deserializer trigger
		
		String triggerId = conditionElement.getAttribute("id_elemento");
		this.triggerElement = getTriggerElementById(triggerId,fsm);
		if ( this.triggerElement == null )
			throw new RuntimeException(" Condition::Condition(Element conditionElement,FSM fsm) trigger with id "+triggerId+" doesnt exist");
		
		// deserialize operator
		
		String operatorId = conditionElement.getAttribute("comparacion");
		this.operator = getOperatorById(operatorId);
		if ( this.triggerElement == null )
			throw new RuntimeException(" Condition::Condition(Element conditionElement,FSM fsm) operator with id "+operatorId+" is not a valid operator");
		
		// deserialize threshold
		
		this.threshold = Double.parseDouble(conditionElement.getAttribute("umbral"));
	}
	
	public VirtualElement getTriggerElement() {
		return triggerElement;
	}

	public void setTriggerElement(VirtualElement triggerElement) {
		this.triggerElement = triggerElement;
	}
	
	public Number getThreshold() {
		return threshold;
	}

	public void setThreshold(Number threshold) {
		this.threshold = threshold;
	}
	
	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}
	
	static public Vector<VirtualElement> getTriggerElements()
	{
		Vector<VirtualElement> triggerElements = new Vector<VirtualElement>();
		
		for (SensorBox sensor : JRoboticaFrame.getInstance().getRobot().getSensors())
			triggerElements.add(sensor);
		
		for (Counter counter : JRoboticaFrame.getInstance().getFSM().getCounters())
			triggerElements.add(counter);
		
		for (Timer timer : JRoboticaFrame.getInstance().getFSM().getTimers())
			triggerElements.add(timer);
		
		return triggerElements;
	}
	
	private VirtualElement getTriggerElementById(String id, FSM fsm)
	{
		for (SensorBox sensor : fsm.getRobot().getSensors())
			if ( id.equals(sensor.getId()) )
				return sensor;
		
		for (Counter counter : fsm.getCounters())
			if ( id.equals(counter.getId()) )
				return counter;
		
		for (Timer timer : fsm.getTimers())
			if ( id.equals(timer.getId()) )
				return timer;
		
		return null;
	}
	
	private VirtualElement getDefaultTriggerElement()
	{
		// TODO esto es horrible. requiere que exista al menos un sensor, sino tira una excepcion.
		// tal vez deberia devolver null?
		return JRoboticaFrame.getInstance().getRobot().getSensors().iterator().next();
	}
	
	static public String[] getOperators()
	{
		return operators;
	}
	
	private String getDefaultOperator()
	{
		return operators[0];
	}
	
	public Number getDefaultThreshold()
	{
		return new Double(0.0);
	}
	
	public Element serialize(Document document)
	{
		// serialize condition
		
		Element conditionElement = document.createElement("condicion");
		conditionElement.setAttribute("id_elemento", triggerElement.getId());
		
		conditionElement.setAttribute("comparacion", getOperator());
		conditionElement.setAttribute("umbral", getThreshold().toString());
		
		return conditionElement;
	}
	
	private String getOperatorById(String opId)
	{
		for (String op : operators)
			if ( opId.equals(op) )
				return op;
		
		return null;
	}
	
}
