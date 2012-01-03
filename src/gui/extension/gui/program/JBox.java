package extension.gui.program;

import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import extension.ExtensionApp;
import extension.gui.JRoboticaFrame;
import extension.model.BehaviorProgram;
import extension.model.Panel;
import extension.model.Panel.Handles;
import extension.model.Panel.Location;
import extension.model.elements.ActuatorBox;
import extension.model.elements.Box;
import extension.model.elements.FunctionBox;
import extension.model.elements.SensorBox;
import extension.utils.IconBank;


public class JBox extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;
	//    private Panel.Handles handles;
    private ComponentDragger dragger;
    private Box box;
    BehaviorProgram program;

    private JLabel boxIcon;
    
    public JBox(Box box, Panel panel)
    {
		super();
		
    	this.box = box;
    	this.program=panel.getProgram();
    	
    	this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    	
    	box.setUi(this);	 
    	this.setOpaque(false);
    	
//    	if( panel != null ) {
//    		if( box instanceof FunctionBox || box instanceof SensorBox )
//    			this.handles = panel.getHandles();
//    		else
//    			this.handles = Panel.Handles.NONE;
//    	}
    	
    	Icon icon = box.getImage();
    	
    	JComponent leftComponent = null;
    	JComponent rightComponent = null;
    	
    	if( panel != null ) {
	    	if( box instanceof SensorBox ) {
	    		leftComponent = createHead(true);
	    		rightComponent = createHead(false);
	    	} else if( box instanceof FunctionBox) {
	    		if( panel.getHandles() == Handles.LEFT ) {
	    			leftComponent = createHead(true);
	    			if( ((FunctionBox)box).getTemplate().acceptInputs )
	    				rightComponent = createTail(true);
	    			icon = IconBank.createMirroredImage(this, icon);
	    		} else {
	    			if( ((FunctionBox)box).getTemplate().acceptInputs )
	    				leftComponent = createTail(false);
	    			rightComponent = createHead(false);
	    		}    		
	    	} else {
	    		if( box.getLocation().equals(Location.LEFT) ) {
	    			rightComponent = createTail(true);
					icon = IconBank.createMirroredImage(this, icon);
	    		}
	    		else
	    			leftComponent = createTail(false);
	    	}
	    	if( leftComponent != null)
	    		add(leftComponent);
    	}
    	boxIcon = new JLabel(icon);
    	add(boxIcon);
    	if( rightComponent != null ) {
    		add(rightComponent);
    	}
    	
    	dragger = new ComponentDragger(this);
        addMouseListener(new PopupMenuMouseAdapter(getPopupMenu()));
    	addMouseListener(dragger);
    	addMouseMotionListener(dragger);
    	
    	// este listener le avisa al connectionMaker
    	// que soy el potencial destino de una coneccion
    	// y hace el highlight de los componentes
    	// en el imagemap del robot
    	AddFocusHandlers();
    }
    
    private void AddFocusHandlers()
    {
    	// este listener le avisa al connectionMaker
    	// que soy el potencial destino de una coneccion
    	// y hace el highlight de los componentes
    	// en el imagemap del robot
    	
    	addMouseListener(new MouseAdapter()
    	{
    		public void mouseEntered(MouseEvent e)
    		{
    			JBox boxGui = (JBox)e.getSource();
    			Box box = boxGui.box;
    			
    			box.setFocused(boxGui.getProgram().getConnectionMaker(),true);
    			
    			// si soy parte del imagemap, hago el highlight correspondiente
    			if (box.showsHighlight())
    				JRoboticaFrame.getInstance().getRobot().getImageMap().focusFeature(box.getId());
    		}
    		public void mouseExited(MouseEvent e)
    		{
    			JBox boxGui = (JBox)e.getSource();
    			box.setFocused(boxGui.getProgram().getConnectionMaker(),false);
    			
    			// si soy parte del imagemap, anulo los highlights
    			if (box.showsHighlight())
    				JRoboticaFrame.getInstance().getRobot().getImageMap().unfocusFeatures();
    		}    		
    	});
    }
        
    public Rectangle getLeftHandleBounds() {
    	Rectangle rect = getVisibleRect();
		rect.grow(-6,-6);
    	return new Rectangle(rect.x-3,rect.y+rect.height/2-3, 6,6);
    }
    public Rectangle getRightHandleBounds() {
    	Rectangle rect = getVisibleRect();
		rect.grow(-6,-6);
    	return new Rectangle(rect.x+rect.width-3,rect.y+rect.height/2-3, 6,6);
    }
    
    private JPopupMenu getPopupMenu() {
    	JPopupMenu popup = new JPopupMenu();
    	JMenuItem item, item2;
    
    	item = new JMenuItem("borrar");
    	item.addActionListener(this);
    	item.setActionCommand("delete");
    	item2 = new JMenuItem("configurar");
    	item2.addActionListener(this);
    	item2.setActionCommand("setup");
    	//popup.add(item);
    	//System.out.println( this.box.getClass() );
		if( !((this.box instanceof SensorBox) || (this.box instanceof ActuatorBox)) ){
			// le agregro el borrar, si no, no. Es para sacar opcion de borrar a actuadores y sensores, siempre muestra todos por defecto y no pueden cambiarse...
			//System.out.println( "este no es sensor/actuador => agrego 'borrar'" );
			popup.add(item);
		}
		if( this.box instanceof FunctionBox ){
			// obtengo el tipo de funcion (exitatoria, inhibitoria, parametrica, o energia)
			String tipo = (((FunctionBox) this.box).getTemplate()).id;
			// chequeo el tipo y veo si llamo a la aplicación...
			if( tipo.equals("parametrica") || tipo.equals("energia") )
				popup.add(item2);
		}
		return popup;
    }
	
    public boolean isDragging() {
    	return dragger.isDragging();
    }

	public void actionPerformed(ActionEvent e)
	{
		// si quiero borrar la box
		if( e.getActionCommand() == "delete" )
		{
			Container parent = getParent();
			if( parent != null ) {
				if( parent instanceof JBoxPanel )
					program.removeBox(box);
			}
		}
		
		// si quiero configurar (esto es solo para cajas de tipo 'FunctionBox')
		if( e.getActionCommand() == "setup" )
		{
			FunctionBox functionBox = (FunctionBox) this.box;
			
			// obtengo el tipo de funcion (exitatoria, inhibitoria, parametrica, o energia)
			String tipo = functionBox.getTemplate().id;
			// chequeo el tipo y llamo a la apliación correcta...
			if( tipo.equals("parametrica") )
			{
				JParametrosCaja setupBox = new JParametrosCaja( functionBox );
				setupBox.run();
			}
			else if( tipo.equals("energia") )
			{
				JParametrosCajaEnergia setupBox = new JParametrosCajaEnergia( functionBox );
				setupBox.run();
			}
		}
	}

	public Box getBox() { return box; }
	
	private JComponent createTail(boolean flipX)
	{
    	URL url = ExtensionApp.class.getResource("images/conexion_cola.png");
    	Icon icon = IconBank.getByUrl(url, 20, 40);
    	if( flipX )
    		icon = IconBank.createRotatedImage(this, icon, 180);
		return new JLabel(icon);		
	}
	
	private JComponent createHead(boolean flipX)
	{
		// create label with image
		URL url = ExtensionApp.class.getResource("images/conexion_punta.png");
    	ImageIcon icon = IconBank.getByUrl(url, 20, 40);
    	if( flipX )
    		icon = IconBank.createRotatedImage(this, icon, 180);
    	JLabel head = new JLabel(icon);
    	
    	// create events that handle dragging
    	
    	head.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e) {
				program.getConnectionMaker().start(box,e.getLocationOnScreen());
			}
			public void mouseReleased(MouseEvent e) {
				program.getConnectionMaker().stop();
			}
    	});
    	
    	head.addMouseMotionListener(new MouseMotionAdapter(){
    		public void mouseDragged(MouseEvent e) {
    			program.getConnectionMaker().updatePoint(e.getLocationOnScreen());
    		}
    	});
		
    	return head;
	}

	public BehaviorProgram getProgram()
	{
		return program;
	}
}


