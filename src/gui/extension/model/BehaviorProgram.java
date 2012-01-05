package extension.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import extension.gui.JRoboticaFrame;
import extension.gui.elements.ActuatorBox;
import extension.gui.elements.Box;
import extension.gui.elements.FunctionBox;
import extension.gui.elements.SensorBox;
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
	
	private Box getBoxById(String id) {
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
	
	boolean addConnection(Box src, Box dst) {
		if( !boxes.contains(src) || !boxes.contains(dst) )
			return false;
		
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
