package extension.gui;

import java.awt.Container;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.net.URL;
import java.awt.Point;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import extension.ExtensionApp;
import extension.model.ActuatorBox;
import extension.model.FunctionBox;
import extension.model.FunctionTemplate;
import extension.model.Panel;
import extension.model.Program;
import extension.model.SensorBox;
import extension.model.Panel.Handles;
import extension.model.Box;
import extension.utils.IconBank;

import extension.gui.JParametrosCaja;
import extension.gui.JParametrosCajaEnergia;


class JBox extends JPanel implements ActionListener {

    protected Panel.Handles handles;
    private ComponentDragger dragger;
    private Box box;

    private JLabel boxIcon;
    
    public JBox(Box box, Panel panel) {
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    	this.box = box;
    	box.setUi(this);	 
    	this.setOpaque(false);
    	
    	if( panel != null ) {
    		if( box instanceof FunctionBox || box instanceof SensorBox )
    			this.handles = panel.getHandles();
    		else
    			this.handles = Panel.Handles.NONE;
    	}
    	
    	Icon icon = null;
    	if( getBox() instanceof FunctionBox ) {
    		FunctionTemplate templ = ((FunctionBox)box).getTemplate();
    		if( templ != null )
    			icon = templ.getIcon();
    	} else if( getBox() instanceof SensorBox ) {
    		icon = ((SensorBox)box).getImage();
    	} else if( getBox() instanceof ActuatorBox ) {
    		icon = ((ActuatorBox)box).getImage();
    	}
    	
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
	    		if( box.getLocation().equals("izquierda") ) {
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
    	addMouseListener(new MouseAdapter() {
    		public void mouseEntered(MouseEvent e) {
    			((JBox)e.getSource()).box.setFocused(true);
    		}
    		public void mouseExited(MouseEvent e) {
    			((JBox)e.getSource()).box.setFocused(false);		
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
    	popup.add(item);
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

	public void actionPerformed(ActionEvent e) {
		if( e.getActionCommand() == "delete" ) {
			Container parent = getParent();
			if( parent != null ) {
				if( parent instanceof JBoxPanel )
					Program.getCurrentProgram().removeBox(box);
			}
		}
		if( e.getActionCommand() == "setup" ) {
			// obtengo el tipo de funcion (exitatoria, inhibitoria, parametrica, o energia)
			String tipo = (((FunctionBox) this.box).getTemplate()).id;
			// chequeo el tipo y llamo a la apliación correcta...
			if( tipo.equals("parametrica") ){
				// creo los puntos para pasarle a JParametrosCaja...
				Point A = new Point();
				Point B = new Point();
				A.x = ((FunctionBox) this.box).getX0();
				A.y = ((FunctionBox) this.box).getY0();
				B.x = ((FunctionBox) this.box).getX1();
				B.y = ((FunctionBox) this.box).getY1();
				// llamo a JParametrosCaja...
				JParametrosCaja setupBox = new JParametrosCaja( A, B, Program.getCurrentProgram(), this.box );
				setupBox.run();
			}
			else if( tipo.equals("energia") ){
						// creo el punto para pasarle a JParametrosCajaEnergia...
						Point A = new Point();
						Point B = new Point();
						A.x = ((FunctionBox) this.box).getX0();
						A.y = ((FunctionBox) this.box).getY0();
						B.x = ((FunctionBox) this.box).getX1();
						B.y = ((FunctionBox) this.box).getY1();
						// llamo a JParametrosCajaEnergia...
						JParametrosCajaEnergia setupBox = new JParametrosCajaEnergia( A, B, Program.getCurrentProgram(), this.box );
						setupBox.run();
					}
		}
	}

	public Box getBox() {
		return box;
	}
	
	private JComponent createTail(boolean flipX) {
    	URL url = ExtensionApp.class.getResource("images/conexion_cola.png");
    	Icon icon = IconBank.getByUrl(url, 20, 40);
    	if( flipX )
    		icon = IconBank.createRotatedImage(this, icon, 180);
		return new JLabel(icon);		
	}
	private JComponent createHead(boolean flipX) {
		URL url = ExtensionApp.class.getResource("images/conexion_punta.png");
    	ImageIcon icon = IconBank.getByUrl(url, 20, 40);
    	if( flipX )
    		icon = IconBank.createRotatedImage(this, icon, 180);
    	JLabel head = new JLabel(icon);
    	head.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e) {
				Program.getCurrentProgram().getConnectionMaker().start(box,e.getLocationOnScreen());
			}
			public void mouseReleased(MouseEvent e) {
				Program.getCurrentProgram().getConnectionMaker().stop();
			}
    	});
    	head.addMouseMotionListener(new MouseMotionAdapter(){
    		public void mouseDragged(MouseEvent e) {
				Program.getCurrentProgram().getConnectionMaker().updatePoint(e.getLocationOnScreen());
    		}
    	});
		
    	return head;
	}

}


