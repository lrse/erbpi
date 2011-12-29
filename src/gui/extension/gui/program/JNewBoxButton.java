package extension.gui.program;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.JButton;

import extension.model.FunctionTemplate;

/*
 * Botones del menu de herramientas para armar un programa.
 * Al apretarlo/arrastrarlo sirve para poner poner un box de algun tipo en el programa.
 */

public class JNewBoxButton extends JButton
{
	private static final long serialVersionUID = 1L;
	private FunctionTemplate functionTemplate;

	public JNewBoxButton(FunctionTemplate functionTemplate)
	{
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
	
	public FunctionTemplate getFunctionTemplate()
	{
		return functionTemplate;
	}
}
