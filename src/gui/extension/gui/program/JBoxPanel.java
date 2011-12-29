package extension.gui.program;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import extension.gui.layouts.BoxColumnLayout;
import extension.model.Panel;
import extension.model.BehaviorProgram;
import extension.model.ProgramListener;
import extension.model.Robot;
import extension.model.elements.Box;
import extension.model.elements.FunctionBox;

public class JBoxPanel extends JPanel implements ProgramListener
{
	private static final long serialVersionUID = 1L;
	private Panel panel;
	private BehaviorProgram program;
	
	public JBoxPanel(Panel panel, BehaviorProgram program) {
		super();
		this.panel = panel;
		this.program = program;
		
		setBackground(Color.LIGHT_GRAY);
		setBorder(new LineBorder(Color.DARK_GRAY));
		
		program.addListener(this);
		
    	setLayout(new BoxColumnLayout(program));
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
    
	private void addBox(JBox box)
	{
		box.addMouseMotionListener(new MouseMotionAdapter() {

			public void mouseDragged(MouseEvent event)
			{
				Component comp = event.getComponent();
				Container parent = comp.getParent();
				
				int zOrder = parent.getComponentZOrder(comp);
				
				if(
					// si no soy el elemento de mas arriba y estoy mas al norte que el proximo elemento arriba mio 
					(zOrder > 0 &&  comp.getY() < getComponent(zOrder-1).getY()) ||
					// si no soy el de mas abajo y estoy mas al sur que el proximo elemento arriba mio
					(zOrder < parent.getComponentCount()-1 &&  comp.getY() > getComponent(zOrder+1).getY())
				)
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
	
	public void boxAdded(Box box)
	{
		if (panel.accepts(box))
		{
			JBox boxUi = new JBox(box, panel, program);
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