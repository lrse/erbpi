package extension.model;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import extension.gui.JRoboticaFrame;
import extension.model.elements.ActuatorBox;
import extension.model.elements.Box;
import extension.model.elements.FunctionBox;
import extension.model.elements.SensorBox;
import extension.utils.XmlUtils;

public class BehaviorProgram extends Program implements Cloneable
{
	private Vector<Box> boxes;
	private ConnectionMaker connectionMaker;
	
	// para cada key, que representa una box destino, tiene la lista de sus box entradas
	// TODO: usar lo siguiente, o en realidad, subclaserar de grafo...
	// private Set<Connection> connections;
	private HashMap<Box,Vector<Box>> connections;
	
	private HashSet<ProgramListener> listeners;
	
	public BehaviorProgram()
	{
		super();
		
		this.boxes = new Vector<Box>();
		this.connectionMaker = new ConnectionMaker(this);
		this.connections = new HashMap<Box,Vector<Box>>();
		this.listeners = new HashSet<ProgramListener>();
		
		LoadDefaultBoxes( JRoboticaFrame.getInstance().getRobot() );
	}
	
	public BehaviorProgram( Element domSpec, Robot robot )
	{
		super(domSpec,robot);
	}

	public Object clone()
	{
		Document document = XmlUtils.getNewDocument();
		Robot robot = JRoboticaFrame.getInstance().getRobot();
		return new BehaviorProgram( this.serialize(document) , robot);
	}
	
	private void LoadDefaultBoxes(Robot robot)
	{
		for( SensorBox sensor: robot.getSensors() )
			addBox(sensor);
		
		for( ActuatorBox actuator: robot.getActuators() )
			addBox(new ActuatorBox(actuator.getId(),actuator.getActuatorType(), this, actuator.getLocation()) );
	}
	
	public ConnectionMaker getConnectionMaker() {
		return connectionMaker;
	}
	
	public void toggleBox(SensorBox box) {
		if( !boxes.contains(box) )
			addBox(box);
		else
			removeBox(box);
	}
	
	public void addBox(Box box)
	{
		if( !boxes.contains(box) )
		{
			boxes.add(box);
			for( ProgramListener listener: listeners )
				listener.boxAdded(box);
		}
		else
		{
			System.err.println("Program::addBox: el box "+box.getId()+" ya estaba contenido!!!\n");
		}
	}
	
	public void removeBox(Box box)
	{
		if( connections.containsKey(box) ) {
			Vector<Box> toBox = new Vector<Box>();
			for( Box b: connections.get(box) )
				toBox.add(b);
			for( Box src: toBox )
				removeConnection(src, box);
		}
		
		Vector<Box> allBoxes = new Vector<Box>();
		for( Box b: connections.keySet() ) 
			allBoxes.add(b);
		
		for( Box dst: allBoxes )
			if( connections.containsKey(dst) && connections.get(dst).contains(box) )
				removeConnection(box,dst);
		
		boxes.remove(box);
		
		for( ProgramListener listener: listeners )
			listener.boxRemoved(box);		
	}
	
	public Box getBoxById(String id) {
		for( Box box: boxes ) {
			if( box.getId().equals(id) )
				return box;
		}
		assert false;
		return null;
	}
	public Iterable<Box> getBoxes() {
		return boxes;
	}
	
	// devuelve las entradas de una box
	public Iterable<Box> getConnectionsTo(Box box) {
		Iterable<Box> answer = connections.get(box);
		if( answer == null )
			return new Vector<Box>();
		else
			return answer;
	}
	
	// devuelve las salidas de una box
	public Iterable<Box> getConnectionsFrom(Box box) {
		Vector<Box> answer = new Vector<Box>();
		for( Box dst: boxes ) {
			Vector<Box> v = connections.get(dst);
			if( v!=null && v.contains(box) )
				answer.add(dst);
		}
		return answer;
	}
	
	public boolean addConnection(Box src, Box dst) {
		if( !boxes.contains(src) || !boxes.contains(dst) )
			return false;
		System.out.println("new connection "+src.getId()+" -> "+dst.getId());
		if( !connections.containsKey(dst) )
			connections.put(dst,new Vector<Box>());
		connections.get(dst).add(src);

		for( ProgramListener listener: listeners )
			listener.connectionAdded(src,dst);
		
		return true;
	}
	
	public boolean removeConnection(Box src, Box dst) {
		if( !connections.containsKey(dst) || !connections.get(dst).contains(src) )
				return false;

		connections.get(dst).remove(src);
		for( ProgramListener listener: listeners )
			listener.connectionRemoved(src, dst);	
		
		return true;
	}
	
	public enum ConnectionDir {Up, Down, Other, None};

	private class BoxDirPair {
		public Box box;
		public ConnectionDir dir;
		public BoxDirPair(Box box, ConnectionDir dir) {
			this.box = box;
			this.dir = dir;
		}
	};
	
	public ConnectionDir getConnectionDir(Box src, Box dst) {
		ConnectionDir answer = ConnectionDir.None;
		Stack<BoxDirPair> stack = new Stack<BoxDirPair>();
		stack.push(new BoxDirPair(src,ConnectionDir.None));
		while( !stack.isEmpty() ) {
			BoxDirPair boxDir = stack.pop();
			for( Box box2: getConnectionsFrom(boxDir.box) ) {
				if( box2 == dst ) {
					if( answer == ConnectionDir.None )
						answer = boxDir.dir;
					else if( answer != boxDir.dir )
						answer = ConnectionDir.Other;
				}
				if( box2 instanceof FunctionBox ) {
					ConnectionDir newDir;
					BoxDirPair newBoxDir = new BoxDirPair(box2, ConnectionDir.None);
					FunctionBox functionBox = (FunctionBox)box2;
					if( functionBox.getY1() > 1000 )
						newDir = ConnectionDir.Up;
					else 
						newDir = ConnectionDir.Down;

					if( boxDir.dir == ConnectionDir.None )
						newBoxDir.dir = newDir;
					else if( boxDir.dir != newDir )
						newBoxDir.dir = ConnectionDir.Other;
					
					stack.push(newBoxDir);
				}
			}
		}
		return answer;
	}
	
	public void addListener(ProgramListener listener) {
		if( !listeners.contains(listener) )
			listeners.add(listener);
	}
	
	public void removeListener(ProgramListener listener) {
		listeners.remove(listener);
	}
	
	public Document serialize()
	{
		Document document = XmlUtils.getNewDocument();
		
		Element rootElement = serialize(document);
		document.appendChild(rootElement);
		
		return document;
	}
	
	public Element serialize(Document document)
	{
		// serialize behavior
		
		Element behaviorElement = document.createElement("comportamiento");
		
		serializeId(behaviorElement);
		serializeDescription(behaviorElement);
		
		// serialize function boxes 
		
		Element boxesElement = document.createElement("cajas");
		behaviorElement.appendChild(boxesElement);
		
		for ( Box box : boxes )
			if ( box instanceof FunctionBox )
				boxesElement.appendChild( box.serialize(document) );
		
		// serialize actuator boxes
		// TODO dejar que los exporte la FSM, son del robot.
		// Hay que cambiar como lo espera el core porque los pone aca por performance
		// TODO mover a la clase hija porque el codigo esta copiado
		
		Element actuatorsElement = document.createElement("actuadores");
		behaviorElement.appendChild(actuatorsElement);
		
		for ( Box box : boxes )
			if ( box instanceof ActuatorBox )
				actuatorsElement.appendChild( box.serialize(document) );
		
		// serialize transitions
		// TODO estas deberian estar afuera de los comportamientos.
		// Estan asi por performance del core, asi que habria que tocarlo un poquito
		
		Element transitionsElement = document.createElement("transiciones");
		behaviorElement.appendChild(transitionsElement);
		
		for( Transition transition : JRoboticaFrame.getInstance().getFSM().getTransitionsFrom(this) )
			transitionsElement.appendChild( transition.serialize(document) );
		
		// serialize box connections
		
		Element connectionsElement = document.createElement("conecciones");
		behaviorElement.appendChild(connectionsElement);
		
		for( Box dstBox : connections.keySet() )
			for( Box srcBox : connections.get(dstBox) )
			{
				Element connectionElement = document.createElement("coneccion");
				connectionElement.setAttribute("src", srcBox.getId());
				connectionElement.setAttribute("dst", dstBox.getId());
				
				connectionsElement.appendChild( connectionElement );
			}
		
		// return full serialized behavior
		
		return behaviorElement;
	}

	@Override
	protected void deserialize(Element domSpec, Robot robot)
	{
		// TODO es feo que esto este aca, y sobre todo en 2 lugares
		// (ver el otro constructor)
		this.boxes = new Vector<Box>();
		this.connectionMaker = new ConnectionMaker(this);
		this.connections = new HashMap<Box,Vector<Box>>();
		this.listeners = new HashSet<ProgramListener>();
		
		LoadDefaultBoxes(robot);
		
		// Cajas
		NodeList functionList = ((Element)domSpec.getElementsByTagName("cajas").item(0)).getElementsByTagName("caja");
		for ( int i=0; i<functionList.getLength(); i++  )
			addBox(new FunctionBox( (Element)functionList.item(i), this ));
		
		// Conecciones
		NodeList connectionList = ((Element)domSpec.getElementsByTagName("conecciones").item(0)).getElementsByTagName("coneccion");
		for ( int i=0; i<connectionList.getLength(); i++  )
		{
			Element connection = (Element)connectionList.item(i);
			Box src = getBoxById(connection.getAttribute("src"));
			Box dst = getBoxById(connection.getAttribute("dst"));
			addConnection(src, dst);
		}
	}
}
