package extension.gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;

import thirdparty.dragnghost.AbstractGhostDropManager;
import thirdparty.dragnghost.GhostDropEvent;

import extension.model.Box;
import extension.model.FunctionBox;
import extension.model.FunctionTemplate;
import extension.model.Program;

public class JBoxTemplate extends JButton {
	private FunctionTemplate functionTemplate;

	JBoxTemplate(FunctionTemplate functionTemplate) {
		this.functionTemplate = functionTemplate;
		this.setPreferredSize(new Dimension(40,40));
		setActionCommand("newBox");
    	
    	setOpaque(true);
	}

	public void paint(Graphics g) {
    	super.paint(g);
    	Icon icon = functionTemplate.getIcon();
    	if( icon != null ) {
    		int w = (int)(getWidth()/2 - icon.getIconWidth()/2);
    		int h = (int)(getHeight()/2 - icon.getIconHeight()/2);
    		icon.paintIcon(this, g, w, h);
    	}
	}		
	public FunctionTemplate getFunctionTemplate() {
		return functionTemplate;
	}
}

class BoxTemplateDropManager extends AbstractGhostDropManager {

	private JBoxTemplate boxTemplate;
	
	public BoxTemplateDropManager(JBoxTemplate boxTemplate, JPanel programPanel) {
		super(programPanel);
		this.boxTemplate = boxTemplate;
	}
	public void ghostDropped(GhostDropEvent e) {
		Point p = e.getDropLocation();
		if( !isInTarget(p) )
			return;
		
		JProgramPanel diagramPanel = (JProgramPanel)component.getComponent(0);
		
		JBoxPanel boxPanel = diagramPanel.getPanelAt(p);
		Box box = new FunctionBox(boxTemplate.getFunctionTemplate()); 
		if( boxPanel.getPanel().accepts(box, Program.getCurrentProgram(), true) ) {
			JBox boxUi = new JBox(box, boxPanel.getPanel());
			boxPanel.addBox(boxUi);
		}
	}		
}
