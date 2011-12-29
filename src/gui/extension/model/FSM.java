package extension.model;

import java.awt.Point;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import extension.gui.fsm.JNode;
import extension.model.elements.Counter;
import extension.model.elements.SensorBox;
import extension.model.elements.Timer;
import extension.utils.XmlUtils;

public class FSM
{
	private Robot robot;
	
	private BehaviorNode initialNode = null;
	private Set<BehaviorNode> behaviors = null;
	private Set<Transition> transitions = null;

	public static boolean countersFrameOpen = false;
	public static boolean timersFrameOpen = false;
	
	private Set<Timer> timers = null;
	private Set<Counter> counters = null;
	
	private TransitionMaker transitionmaker;
	
	private Set<FSMListener> listeners = new HashSet<FSMListener>();
	
	public FSM(Robot robot)
	{
		this.robot = robot;
		
		initializeFields();
		
		BehaviorNode initialNode = this.addNewBehaviorNode(new Point(100, 100),new DumbProgram());
		this.setInitialNode(initialNode);
	}
	
	public FSM(Element domSpec)
	{
		initializeFields();
		
		deserialize(domSpec);
	}
	
	private void initializeFields()
	{
		this.transitionmaker = new TransitionMaker(this);
		this.behaviors = new HashSet<BehaviorNode>();
		this.transitions = new HashSet<Transition>();
		this.timers = new HashSet<Timer>();
		this.counters = new HashSet<Counter>();
	}
	
	public Iterable<Transition> getTransitionsFrom(Program program)
	{
		Collection<Transition> filteredTransitions = new LinkedList<Transition>();
		
		for ( Transition transition : transitions )
			if ( transition.hasSrc(program) )
				filteredTransitions.add(transition);
		
		return filteredTransitions;
	}
	
	private Iterable<Transition> getTransitions(Program program)
	{
		Collection<Transition> filteredTransitions = new LinkedList<Transition>();
		
		for ( Transition transition : transitions )
			if ( transition.hasSrc(program) || transition.hasDst(program) )
				filteredTransitions.add(transition);
		
		return filteredTransitions;
	}
	
	// ===========================================================
	
	private void setInitialNode(BehaviorNode initialNode)
	{
		if ( !behaviors.contains(initialNode) )
			throw new RuntimeException("FSM::setInitialNode - Initial node is not a valid node!");
		
		this.initialNode = initialNode;
	}
	
	public BehaviorNode getInitialNode() { return this.initialNode; }
	
	public BehaviorNode addNewBehaviorNode(Point position, Program program)
	{
		BehaviorNode behaviorNode = new BehaviorNode(program,position);
		return addBehaviorNode(behaviorNode);
	}
	
	private BehaviorNode addBehaviorNode(Element domSpec, Program program)
	{
		BehaviorNode behaviorNode = new BehaviorNode(domSpec, this.robot, program);
		return addBehaviorNode(behaviorNode);
	}
	
	private BehaviorNode addBehaviorNode(BehaviorNode behaviorNode)
	{
		behaviors.add(behaviorNode);
		
		for (FSMListener listener : listeners)
			listener.behaviorAdded(behaviorNode);
		
		return behaviorNode;
	}
	
	public void removeBehaviorNode(BehaviorNode behavior)
	{
		removeAsociatedTransitions(behavior);
		
		behaviors.remove(behavior);
		
		for (FSMListener listener : listeners)
			listener.behaviorRemoved(behavior);
	}
	
	public Transition addNewTransition(BehaviorNode source, BehaviorNode destination)
	{
		Transition transition = new Transition(source,destination);
		return addTransition(transition);
	}
	
	private Transition addTransition(Transition transition)
	{
		System.out.println("Transition added!");
		
		transitions.add(transition);
		
		for (FSMListener listener : listeners)
			listener.transitionAdded(transition);
		
		return transition;
	}
	
	public void removeTransition(Transition transition)
	{
		System.out.println("Transition removed!");
		
		transitions.remove(transition);
		
		for (FSMListener listener : listeners)
			listener.transitionRemoved(transition);
	}
	
	private void removeAsociatedTransitions(BehaviorNode behavior)
	{
		for( Transition transition : getTransitions(behavior.getProgram()) )
			removeTransition(transition);
	}
	
	public boolean transitionExists(JNode source, JNode destination)
	{
		for ( Transition transition : transitions )
			if ( transition.connects(source.getBehavior(),destination.getBehavior()) )
				return true;
		return false;
	}
	
// ====================== timers y contadores ===========================
	
	public Timer addNewTimer()
	{
		return addTimer(new Timer());
	}
	
	private Timer addTimer(Element domSpec)
	{
		return addTimer(new Timer(domSpec));
	}
	
	private Timer addTimer(Timer timer)
	{
		this.timers.add(timer);
		return timer;
	}
	
	public void removeTimer(Timer timer)
	{
		this.timers.remove(timer);
	}
	
	public Iterable<Timer> getTimers()
	{
		return timers;
	}
	
	public Counter addNewCounter()
	{
		return addCounter(new Counter());
	}
	
	private Counter addCounter(Element domSpec)
	{
		return addCounter(new Counter(domSpec));
	}
	
	private Counter addCounter(Counter counter)
	{
		this.counters.add(counter);
		return counter;
	}
	
	public void removeCounter(Counter counter)
	{
		this.counters.remove(counter);
	}
	
	public Iterable<Counter> getCounters()
	{
		return counters;
	}
	
// ================= para comunicarse con la ui =========================
	
	public void addListener(FSMListener listener) {
		if( !listeners.contains(listener) )
			listeners.add(listener);
	}
	public void removeListener(FSMListener listener) {
		listeners.remove(listener);
	}
	
	// ==============================================
	
	public Set<BehaviorNode> getBehaviors() {
		return behaviors;
	}
	
	public BehaviorNode getBehaviorById(String id)
	{
		for ( BehaviorNode behavior : behaviors )
			if ( id.equals(behavior.getId()) )
				return behavior;
		
		return null;
	}
	
	public Set<Transition> getTransitions() {
		return transitions;
	}
	
	public Robot getRobot() {
		return robot;
	}
	
	public TransitionMaker getTransitionMaker(){
		return transitionmaker;
	}
	
	public Document serialize()
	{
		Document document = XmlUtils.getNewDocument();
		
		Element fsmElement = document.createElement("conducta");
		fsmElement.setAttribute("id_comportamiento_inicial", initialNode.getProgram().getId());
		document.appendChild(fsmElement);
		
		// serialize robot information
		
		Element robotElement = document.createElement("robot");
		robotElement.setAttribute("id", robot.getId());
		fsmElement.appendChild( robotElement );
		
		// serialize sensors
		// TODO: porque hace falta? esta info no deberia ser parte del robot?
		
		Element sensorsElement = document.createElement("sensores");
		fsmElement.appendChild( sensorsElement );
		
		for ( SensorBox sensor : robot.getSensors() )
			sensorsElement.appendChild( sensor.serialize(document) );
		
		// serialize timers
		
		Element timersElement = document.createElement("timers");
		fsmElement.appendChild( timersElement );
		
		for ( Timer timer : timers )
			timersElement.appendChild( timer.serialize(document) );
		
		// serialize counters
		
		Element countersElement = document.createElement("contadores");
		fsmElement.appendChild( countersElement );
		
		for ( Counter counter : counters )
			countersElement.appendChild( counter.serialize(document) );
		
		// serialize behaviors
		
		for ( BehaviorNode behavior : behaviors )
			fsmElement.appendChild( behavior.serialize(document) );
		
		// serialize transitions
		
		Element transitionsElement = document.createElement("transiciones");
		fsmElement.appendChild( transitionsElement );
		
		for( Transition transition : transitions )
			transitionsElement.appendChild( transition.serialize(document) );
		
		return document;
	}
	
	public void deserialize(Element domSpec)
	{
		// Robot
		String robotId = ( (Element)domSpec.getElementsByTagName("robot").item(0) ).getAttribute("id");
		this.robot = Robot.getRobotById(robotId);
		
		// Initial node
		// TODO guardar/cargar la posicion del xml 
		String initialNodeId = domSpec.getAttribute("id_comportamiento_inicial");
		
		// Sensors are defined by the robot, no need to parse them
		
		// Timers
		NodeList timerList = ((Element)domSpec.getElementsByTagName("timers").item(0)).getElementsByTagName("timer");
		for ( int i=0; i<timerList.getLength(); i++  )
		{
			Element timerElement = (Element)timerList.item(i);
			addTimer( timerElement );
		}
		
		// Counters
		NodeList counterList = ((Element)domSpec.getElementsByTagName("contadores").item(0)).getElementsByTagName("contador");
		for ( int i=0; i<counterList.getLength(); i++  )
			addCounter( (Element)counterList.item(i) );
		
		// Behaviors
		NodeList behaviorList = domSpec.getElementsByTagName("comportamiento");
		for ( int i=0; i<behaviorList.getLength(); i++  )
		{
			Element behaviorSpec = (Element) behaviorList.item(i);
			
			// TODO feo. deberia haber una factory que te devuelva el programa correcto
			Program program = 
				( behaviorSpec.getAttribute("id").equals(initialNodeId) ) ? 
				new DumbProgram(behaviorSpec, this.robot) :
				new BehaviorProgram(behaviorSpec, this.robot);
			
			BehaviorNode node = addBehaviorNode( behaviorSpec, program );
			
			if ( node.getId().equals(initialNodeId) )
				setInitialNode(node);
		}
		
		// Transitions
		Element firstChild = XmlUtils.getFirstChildByTagName(domSpec, "transiciones");
		NodeList transitionList = firstChild.getElementsByTagName("transicion");
		int num = transitionList.getLength();
		for ( int i=0; i<num; i++  )
			addTransition( new Transition((Element)transitionList.item(i), this) );
	}
}
