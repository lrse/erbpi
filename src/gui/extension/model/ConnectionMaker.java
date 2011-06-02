package extension.model;

import java.awt.Point;
import java.util.HashSet;

import extension.model.Box;
import extension.model.Program;

public class ConnectionMaker {
	private Program program;
	private boolean enabled = false;
	private Box src;
	private Box dst;
	private Point srcPoint;
	private Point dstPoint;
	private boolean allowed;
	
	public ConnectionMaker(Program program) {
		this.program = program;
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
	public void stop() {
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
	
	private HashSet<ConnectionMakerListener> listeners = new HashSet<ConnectionMakerListener>();
	public void addListener(ConnectionMakerListener listener) {
		listeners.add(listener);
	}
}
