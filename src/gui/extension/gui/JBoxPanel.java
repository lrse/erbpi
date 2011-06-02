package extension.gui;

import extension.model.Box;
import extension.model.FunctionBox;
import extension.model.Panel;
import extension.model.Program;
import extension.model.ProgramListener;
import extension.model.Robot;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.Point;

import javax.swing.JPanel;

public class JBoxPanel extends JPanel implements ProgramListener {
	   
	private Panel panel;
	
	public JBoxPanel(Component parent, Panel panel) {
		super();
		this.panel = panel;
		this.setBackground(Color.LIGHT_GRAY);
		
		Program.getCurrentProgram().addListener(this);
		
    	setLayout(new BoxColumnLayout());	    	
    	//setBorder(new LineBorder(Color.BLACK));

		Program program = Program.getCurrentProgram();
		for( Box boxModel: program.getBoxes() ) {
			if( panel.accepts(boxModel, program) ) {
				JBox box = new JBox(boxModel, panel);
				addBox(box);
			}
		}
	}
	
	public Dimension getPreferredSize() {
    	return new Dimension(100,200);
    }
	
	
    public Dimension getMaximumSize() {
    	if( panel.getType() == Panel.Type.BOXES )
    		return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    	else {
    		int x = 0;
    		for( Component comp: getComponents() )
    			x = Math.max(x, comp.getWidth());
    		return new Dimension(x+50, Integer.MAX_VALUE);
    	}
    }
	
    public Panel getPanel() {
    	return panel;
    }
    
	public void addBox(JBox box) {
		Box modelBox = box.getBox();
		Program.getCurrentProgram().addBox(modelBox);
					
		box.addMouseMotionListener(new MouseMotionAdapter() {

			public void mouseDragged(MouseEvent event) {
				Component comp = event.getComponent();
				Container parent = comp.getParent();
				int y = event.getY() + comp.getY();
				int zOrder = parent.getComponentZOrder(event.getComponent());
				
				if( (zOrder > 0 && y < getComponent(zOrder-1).getY()) ||
				    (zOrder < parent.getComponentCount()-1 && y > getComponent(zOrder+1).getY()) )
						doLayout();
				
				super.mouseDragged(event);
			}			
		});
		
		super.add(box);
		doLayout();
		box.updateUI();
	}
	
	public void boxRemoved(Box box) {
		if( box.getUi() != null && box.getUi().getParent() == this ) {
			remove(box.getUi());
			doLayout();
		}
	}
	
	public void boxAdded(Box box) {
		if( panel.accepts(box, Program.getCurrentProgram()) ) {
			JBox boxUi = new JBox(box, panel);
			addBox(boxUi);
		}
	}
	
	public void boxSet( Box box, Point A, Point B ){
		if( box instanceof FunctionBox ){
			((FunctionBox) box).setX0(A.x);
			((FunctionBox) box).setY0(A.y);
			((FunctionBox) box).setX1(B.x);
			((FunctionBox) box).setY1(B.y);
		}
	}
	
	public void connectionAdded(Box src, Box dst) {
		doLayout();
	}

	public void connectionRemoved(Box src, Box dst) {
		doLayout();
	}	

	public void robotChanged(Robot robot) {
	}
}