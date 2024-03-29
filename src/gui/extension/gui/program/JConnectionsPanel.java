package extension.gui.program;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import extension.gui.elements.Box;
import extension.model.BehaviorProgram;
import extension.model.ConnectionMaker;
import extension.model.ConnectionMakerListener;
import extension.model.ProgramListener;

class JConnection extends JComponent implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private JBox src;
	private JBox dst;
	private JProgramPanel diagram;
	private BehaviorProgram program;
	
	JConnection(JProgramPanel diagram, JBox src, JBox dst, BehaviorProgram program) {
		this.diagram = diagram;
		this.program = program;
		this.src = src;
		this.dst = dst;
		
		if( src == null )
			System.err.println("Cant create connection view. Source view is null.");
		
		if( dst == null )
			System.err.println("Cant create connection view. Target view is null.");
		
		setSize(10,10);
        addMouseListener(new PopupMenuMouseAdapter(getPopupMenu()));		
	}
	
	public JBox getSrc() {
		return src;
	}

	public JBox getDst() {
		return dst;
	}	
	
    private JPopupMenu getPopupMenu() {
    	JPopupMenu popup = new JPopupMenu();
    	JMenuItem item;
    
    	item = new JMenuItem("borrar conexión");
    	item.addActionListener(this);
    	item.setActionCommand("delete");
    	popup.add(item);
    	
    	return popup;
    }

	@Override
	public void paint(Graphics g) {
		doLayout();
		super.paint(g);
		g.setColor(Color.BLACK);
		g.fillOval(0,0,getWidth(),getHeight());
	}

	private Point getLocationInDiagram(Point p, Component comp) {
		Point answer = new Point(p.x,p.y);
		Component c = comp;
		while( c != diagram && c != null ) {
			Point pp = c.getLocation();
			answer.translate(pp.x, pp.y);
			c = c.getParent();
		}
		return answer;
	}
	
	@Override
	public void doLayout() {
		Point psrc = getSourceLocation();
		Point pdst = getDestinationLocation();
		
		setLocation((psrc.x + pdst.x) /2 - getWidth()/2, (psrc.y + pdst.y) /2 - getHeight()/2);
	}

	public Point getSourceLocation() {
		Rectangle r;
		if( getLocationInDiagram(new Point(0,0),src).x < getLocationInDiagram(new Point(0,0), dst).x ) 
			r = src.getRightHandleBounds();
		else
			r = src.getLeftHandleBounds();
		Point p = new Point((int)(r.getCenterX()),(int)(r.getCenterY()));
		return getLocationInDiagram(p, src);
	}

	public Point getDestinationLocation() {
		Rectangle r;
		if( getLocationInDiagram(new Point(0,0),dst).x < getLocationInDiagram(new Point(0,0), src).x ) 
			r = dst.getRightHandleBounds();
		else
			r = dst.getLeftHandleBounds();
		Point p = new Point((int)(r.getCenterX()),(int)(r.getCenterY()));
		return getLocationInDiagram(p, dst);
	}

	public void actionPerformed(ActionEvent e) {
		if( e.getActionCommand() == "delete" ) {
			program.removeConnection(src.getBox(), dst.getBox());
		}		
	}
}

class JConnectionsPanel extends JPanel implements ProgramListener, ConnectionMakerListener 
{
	private static final long serialVersionUID = 1L;
	private JProgramPanel diagram;
	private ConnectionMaker connectionMaker;

	private Stroke stroke = new BasicStroke(3);
	private Color color = Color.BLACK;
	
	private BehaviorProgram program;

	JConnectionsPanel(JProgramPanel diagram, BehaviorProgram program)
	{
		setLayout(new GridLayout(1,1));
		
		this.diagram = diagram;
		
		this.program = program;
		this.program.addListener(this);
		
		this.connectionMaker = this.program.getConnectionMaker();
		this.connectionMaker.addListener(this);

		// add connections
		for( Box dstBox: this.program.getBoxes() )
			for( Box srcBox: this.program.getConnectionsTo(dstBox) )
				connectionAdded(srcBox,dstBox);
	}
	
	void reload(BehaviorProgram program)
	{
		this.program.removeListener(this);
		this.connectionMaker.removeListener(this);
		
		this.removeAll();
		
		this.program = program;
		this.program.addListener(this);
		
		this.connectionMaker = this.program.getConnectionMaker();
		this.connectionMaker.addListener(this);

		// add connections
		for( Box dstBox: this.program.getBoxes() )
			for( Box srcBox: this.program.getConnectionsTo(dstBox) )
				connectionAdded(srcBox,dstBox);
	}
	
	public void connectionAdded(Box src, Box dst)
	{
		add(new JConnection(diagram,(JBox)(src.getUi()),(JBox)(dst.getUi()),program));
		repaint();
	}
	
	public void connectionRemoved(Box src, Box dst)
	{
		for( Component c: getComponents() )
			if( c instanceof JConnection ) {
				JConnection conn = (JConnection)c;
				if( conn.getSrc().getBox() == src && conn.getDst().getBox() == dst ) {
					remove(conn);
				}
			}
		repaint();
	}
	
	public void boxRemoved(Box box){}
	
	public void boxAdded( Box box ){}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		paintConnections(g2d);
		paintNewConnection(g2d);
		super.paint(g);
	}
		
	@Override
	public void doLayout() {
		for( Component c: getComponents() ) {
			c.doLayout();
		}
	}
	
	private void paintConnections(Graphics2D g2d) {
		for( Component c: getComponents() ) {
			if( c instanceof JConnection ) {
				Point psrc = ((JConnection)c).getSourceLocation();
				Point pdst = ((JConnection)c).getDestinationLocation();
				
		        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		                             RenderingHints.VALUE_ANTIALIAS_ON);
		        g2d.setStroke(stroke);
		        g2d.setColor(color);
				g2d.drawLine(psrc.x, psrc.y, pdst.x, pdst.y);
			}
		}
	}
	
	private void paintNewConnection(Graphics2D g2d) {
		if( connectionMaker.isEnabled() ) {
			Point srcPoint = (Point) connectionMaker.getSrcPoint().clone();
			Point dstPoint = (Point) connectionMaker.getDstPoint().clone();
			
			SwingUtilities.convertPointFromScreen(srcPoint, this);
			SwingUtilities.convertPointFromScreen(dstPoint, this);
						
			g2d.setStroke(stroke);
			if( connectionMaker.isAllowed() )
				g2d.setColor(Color.GREEN);
			else
				g2d.setColor(color);
			g2d.drawLine(srcPoint.x, srcPoint.y, dstPoint.x, dstPoint.y);
		}
	}

	public void newConnectionStarted(ConnectionMaker maker) {
		repaint();
	}
	
	public void newConnectionUpdated(ConnectionMaker maker) {
		repaint();
	}
	
	public void newConnectionStopped(ConnectionMaker maker) {
		repaint();
	}
}
