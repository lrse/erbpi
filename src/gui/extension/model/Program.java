package extension.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;
import java.util.Vector;
import java.awt.Point;

import extension.gui.JProgramPanel;

public class Program {
	
	private Robot robot;
	private Vector<Box> boxes;
	private HashMap<Box,Vector<Box>> connections;
	private JProgramPanel ui;
	
	private ConnectionMaker connectionMaker;
	
	private static Program currentProgram = null;
	public static Program getCurrentProgram() {
		return currentProgram;
	}
	public static void setCurrentProgram(Program program) {
		currentProgram = program;
	}
	
	public Program(Robot robot) {
		this.robot = robot;
		this.connectionMaker = new ConnectionMaker(this);
		boxes = new Vector<Box>();
		connections = new HashMap<Box,Vector<Box>>();
		ui = null;
	}
	
	public JProgramPanel getUi() {
		return ui;
	}
	public void setUi(JProgramPanel ui) {
		this.ui = ui;
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
	
	public boolean containsBox(SensorBox box) {
		return boxes.contains(box);
	}
	
	public void addBox(Box box) {
		if( !boxes.contains(box) ) {
			boxes.add(box);
			for( ProgramListener listener: listeners )
				listener.boxAdded(box);
		}
	}
	
	public void removeBox(Box box) {		
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
	
	public void setBox( Box box, Point A, Point B ){		
		for( ProgramListener listener: listeners )
			listener.boxSet(box,A,B);		
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
	public Iterable<Box> getConnectionsTo(Box box) {
		Iterable<Box> answer = connections.get(box);
		if( answer == null )
			return new Vector<Box>();
		else
			return answer;
	}
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
	
	public Robot getRobot() {
		return robot;
	}
	public void setRobot(Robot robot) {
		this.robot = robot;
		for( ProgramListener listener: listeners )
			listener.robotChanged(robot);			
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
	
	private HashSet<ProgramListener> listeners = new HashSet<ProgramListener>();
	public void addListener(ProgramListener listener) {
		if( !listeners.contains(listener) )
			listeners.add(listener);
	}
	public void removeListener(ProgramListener listener) {
		listeners.remove(listener);
	}
}
