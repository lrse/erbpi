package extension.gui;

import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import thirdparty.dragnghost.AbstractGhostDropManager;
import thirdparty.dragnghost.GhostDropEvent;
import extension.gui.program.JBoxPanel;
import extension.gui.program.JNewBoxButton;
import extension.gui.program.JProgramPanel;
import extension.model.Panel;
import extension.model.elements.Box;
import extension.model.elements.FunctionBox;

/*
 * Esta clase es un 'GhostDropListener' que se encarga de manejar el drop de una 'ghostBox'
 */

class GhostBoxDropHandler extends AbstractGhostDropManager
{
	private JNewBoxButton boxTemplate;
	
	public GhostBoxDropHandler(JNewBoxButton boxTemplate, JComponent programPanel)
	{
		super(programPanel);
		this.boxTemplate = boxTemplate;
	}
	
	public void ghostDropped(GhostDropEvent e)
	{
		Point p = (Point)e.getDropLocation().clone();
		
		// el panel que tiene como hijos las columnas
		// con los diferentes boxes del comportamiento
		JProgramPanel programPanel = (JProgramPanel)component;
		
		// el evento me devuelve un punto en coordenadas de pantalla,
		// asi que lo hago relativo al 'programPanel'
		SwingUtilities.convertPointFromScreen(p, programPanel);
		
		// si hice el drop en un lugar distinto al programPanel
		// no me interesa y descarto el drop
		if ( p.getX() < 0 || component.getWidth() < p.getX() || p.getY() < 0 || component.getHeight() < p.getY() )
			return;
		
		// busco la columna correspondiente del 'boxesPanel' donde hice el drop del elemento
		JBoxPanel boxPanelGui = programPanel.getPanelAt(p);
		
		// A veces la funcion devuelve 'null' y no se porque, asi que chequeo por las dudas.
		if (boxPanelGui!=null)
		{
			Panel boxPanel = boxPanelGui.getPanel();
			
			// creo una nueva caja de tipo 'funcion' (porque son las unicas que estan en la barra de herramientas)
			Box box = new FunctionBox(boxTemplate.getFunctionTemplate(),programPanel.getProgram(),boxPanel.getLocation());
			
			if( boxPanel.accepts(box) )
				programPanel.getProgram().addBox(box);
		}
	}		
}