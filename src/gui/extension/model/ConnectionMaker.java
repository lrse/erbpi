package extension.model;

import java.awt.Point;
import java.util.HashSet;

import extension.model.elements.Box;

public class ConnectionMaker implements Cloneable{
	private BehaviorProgram program;
	private boolean enabled = false;
	private Box src;
	private Box dst;
	private Point srcPoint;
	private Point dstPoint;
	private boolean allowed;
	
	private HashSet<ConnectionMakerListener> listeners = new HashSet<ConnectionMakerListener>();
	
	public ConnectionMaker(BehaviorProgram program) {
		this.program = program;
	}
	
	public Object clone(){
		try{
			System.out.println("CONNECTIONMAKER CLONANDOSE...");

			ConnectionMaker connectionMakerClonado = (ConnectionMaker) super.clone();
			
			return connectionMakerClonado;
		}
		catch(CloneNotSupportedException e){
			System.out.println("CLONE ERROR: " + e);
			return null;
		}
	}

	public void start(Box src, Point p) {
		enabled = true;
		this.src = src;
		this.srcPoint = p;
		this.dstPoint = p;
		this.dst = null;
		this.allowed = false;
		for( ConnectionMakerListener listener: listeners )
			listener.newConnectionStarted(this);
	}
	
	public void updatePoint(Point p) {
		this.dstPoint = p;
		for( ConnectionMakerListener listener: listeners )
			listener.newConnectionUpdated(this);
	}
	
	public void setDst(Box dst, boolean allowed) {
		this.dst = dst;
		this.allowed = allowed;
		for( ConnectionMakerListener listener: listeners )
			listener.newConnectionUpdated(this);
	}
	
	// se llama al soltar la conexion
	public void stop()
	{
		// si estaba sobre algo valido, hago la conexion efectiva
		if( allowed )
			program.addConnection(src,dst);
		
		enabled = false;
		
		for( ConnectionMakerListener listener: listeners )
			listener.newConnectionStopped(this);
	}	
	
	public boolean isEnabled() {
		return enabled;
	}
	public boolean isAllowed() {
		return allowed;
	}
	public Box getSrc() {
		return src;
	}
	public Point getSrcPoint() {
		return srcPoint;
	}
	public Point getDstPoint() {
		return dstPoint;
	}
	
	public void addListener(ConnectionMakerListener listener) {
		listeners.add(listener);
	}
}
