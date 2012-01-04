package extension.gui.program;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import extension.gui.layouts.BoxColumnLayout;
import extension.model.Panel;
import extension.model.elements.Box;
import extension.model.elements.Box.BoxType;
import extension.model.program.PanelListener;

public class JBoxPanel extends JPanel implements PanelListener
{
	private static final long serialVersionUID = 1L;
	private Panel panel;
	
	JBoxPanel(Panel panel)
	{
		super();
		this.panel = panel;
		
		setBackground(Color.LIGHT_GRAY);
		setBorder(new LineBorder(Color.DARK_GRAY));
		
		setLayout(new BoxColumnLayout(panel));
		
		panel.addListener(this);
    	for ( Box box : panel.getBoxes() )
    		boxAdded(box);
	}
	
	public Dimension getPreferredSize() {
    	return new Dimension(100,200);
    }
	
    public Dimension getMaximumSize() {
    	if( panel.getType() == BoxType.FUNCTION )
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
    
	@Override
	public void boxRemoved(Box box)
	{
		remove(box.getUi());
		doLayout();
	}
	
	@Override
	public void boxAdded(Box box)
	{
		JBox boxUi = new JBox(box, panel);
		
		boxUi.addMouseMotionListener(new BoxMotionAdapter());
		
		super.add(boxUi);
		
		doLayout();
		
		boxUi.updateUI();
	}
	
	private class BoxMotionAdapter extends MouseMotionAdapter
	{
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
	}
	
}