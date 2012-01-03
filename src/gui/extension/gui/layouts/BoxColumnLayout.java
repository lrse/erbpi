package extension.gui.layouts;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;

import extension.gui.program.JBox;
import extension.gui.program.JBoxPanel;
import extension.model.BehaviorProgram;
import extension.model.Panel;
import extension.model.elements.Box;

public class BoxColumnLayout implements LayoutManager
{
    private int vgap;
    private int minWidth = 0, minHeight = 0;
    private int preferredWidth = 0, preferredHeight = 0;
    
    private Panel panel;

    public BoxColumnLayout(Panel panel)
    {
    	this.panel=panel;
        vgap = 5;
    }

    /* Required by LayoutManager. */
    public void addLayoutComponent(String name, Component comp) {
    }

    /* Required by LayoutManager. */
    public void removeLayoutComponent(Component comp) {
    }

    private void setSizes(Container parent) {
        int nComps = parent.getComponentCount();
        Dimension d = null;

        //Reset preferred/minimum width and height.
        preferredWidth = 0;
        preferredHeight = 0;
        minWidth = 0;
        minHeight = 0;

        for (int i = 0; i < nComps; i++) {
            Component c = parent.getComponent(i);
            if (c.isVisible()) {
                d = c.getPreferredSize();

                if (i > 0) {
                    preferredWidth += d.width/2;
                    preferredHeight += vgap;
                } else {
                    preferredWidth = d.width;
                }
                preferredHeight += d.height;

                minWidth = Math.max(c.getMinimumSize().width,
                                    minWidth);
                minHeight = preferredHeight;
            }
        }   
    }

	public void reorderComponents(Container target) {
		ArrayList<Component> sortedComponents = new ArrayList<Component>();
		for( Component c : target.getComponents() ) sortedComponents.add(c);
		Collections.sort(sortedComponents, new Comparator<Component>(){
			public int compare(Component arg0, Component arg1) {
				double y0 = arg0.getLocation().getY();
				double y1 = arg1.getLocation().getY();
				if( y0 < y1 ) return -1;
				else if( y0 == y1 ) return 0;
				else return 1;
			}			
		});
		int ndx = 0;
		for( Component c : sortedComponents ){
			target.setComponentZOrder(c, ndx);
			ndx++;
		}
	}

    /* Required by LayoutManager. */
    public Dimension preferredLayoutSize(Container parent) {
        Dimension dim = new Dimension(0, 0);
        //int nComps = parent.getComponentCount();

        setSizes(parent);

        //Always add the container's insets!
        Insets insets = parent.getInsets();
        dim.width = preferredWidth
                    + insets.left + insets.right;
        dim.height = preferredHeight
                     + insets.top + insets.bottom;

        return dim;
    }

    /* Required by LayoutManager. */
    public Dimension minimumLayoutSize(Container parent) {
        Dimension dim = new Dimension(0, 0);
        //int nComps = parent.getComponentCount();

        //Always add the container's insets!
        Insets insets = parent.getInsets();
        dim.width = minWidth
                    + insets.left + insets.right;
        dim.height = minHeight
                     + insets.top + insets.bottom;

        return dim;
    }

    /* Required by LayoutManager. */
    /*
     * This is called when the panel is first displayed,
     * and every time its size changes.
     * Note: You CAN'T assume preferredLayoutSize or
     * minimumLayoutSize will be called -- in the case
     * of applets, at least, they probably won't be.
     */
    public void layoutContainer(Container parent) {

    	reorderComponents(parent);
    	
        Insets insets = parent.getInsets();
        int maxHeight = parent.getHeight()
                        - (insets.top + insets.bottom);
        int maxWidth = parent.getWidth() - (insets.left + insets.right);
        Component[][] columns = generateColumns((JBoxPanel)parent);
        int xStep = maxWidth / (columns.length+1);
        int x = xStep;
        for( Component[] column: columns ) {
        	layoutColumn(maxHeight, x, column);
        	x += xStep;
        }
    }
    
    private void layoutColumn(int maxHeight, int x, Component[] components) {
        int ySep = maxHeight / (components.length+1);
        int yPos = ySep;
        int xPos = x;
        for( Component c: components ){
        	Dimension dim = c.getPreferredSize();
        	if( ! (c instanceof JBox) || ! ((JBox)c).isDragging() )
        		c.setBounds(xPos-dim.width/2, yPos-dim.height/2, dim.width, dim.height);
        	yPos += ySep;
        }
    	
    }
    
    private Component[][] generateColumns(JBoxPanel boxPanel) {
    	HashMap<Component,Integer> map = new HashMap<Component,Integer>();
    	for( Component c: boxPanel.getComponents() )
    		map.put(c, 1);

    	boolean changed = true;
    	while( changed ) {
    		changed = false;
    		for( Box b: panel.getProgram().getBoxes() ) {
    			if( map.containsKey(b.getUi()) ) {
					for( Box src: panel.getProgram().getConnectionsTo(b) )    					
						if( map.containsKey(src.getUi()) && map.get(src.getUi()) >= map.get(b.getUi()) ) {
							map.put(b.getUi(), map.get(b.getUi())+1);
							changed = true;
							break;
						}
    			}
    		}
    	}
    	
    	Vector<Vector<Component>> columns = new Vector<Vector<Component>>();
    	for( Component c: boxPanel.getComponents() ) {
    		int i = map.get(c);
    		while( i > columns.size() )
    			columns.add(new Vector<Component>());
    		columns.get(i-1).add(c);
    	}
    	
    	Component[][] answer = new Component[columns.size()][];
    	 for( int i=0; i<columns.size(); i++ ) {
    		answer[i] = new Component[columns.get(i).size()];
    		columns.get(i).copyInto(answer[i]);
    	 }
    	return answer;
    }

    public String toString() {
        String str = "";
        return getClass().getName() + "[vgap=" + vgap + str + "]";
    }
	
}

