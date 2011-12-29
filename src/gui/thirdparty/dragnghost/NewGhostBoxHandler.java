package thirdparty.dragnghost;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.SwingUtilities;

/*
 * Esta clase debe escuchar al boton de NewBox de las herramientas para generar el comportamiento.
 * Se ocupa de crear el 'ghostBox' arrastrable y de destruirlo al soltar el mouse,
 * asi como habilitar/deshabilitar el 'glassPane' de la ventana.
 * 
 * Ademas, al soltarse la 'ghostBox' dispara un evento para que escuche un 'GhostDropListener'
 * avisando que se produjo el 'drop'.
 * */

public class NewGhostBoxHandler extends GhostDropAdapter
{
    public NewGhostBoxHandler(GhostGlassPane glassPane, String action)
    {
        super(glassPane, action);
    }

    public void mousePressed(MouseEvent e)
    {
        Component c = e.getComponent();

        BufferedImage image = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        c.paint(g);

        glassPane.setVisible(true);

        Point p = (Point) e.getPoint().clone();
        SwingUtilities.convertPointToScreen(p, c);
        SwingUtilities.convertPointFromScreen(p, glassPane);
        
        glassPane.setExtension(new GhostDragObjectExtension(glassPane, image, p));
        //glassPane.setPoint(p);
        //glassPane.setImage(image);
        glassPane.repaint();
    }

    public void mouseReleased(MouseEvent e)
    {
        Component c = e.getComponent();

        Point p = (Point) e.getPoint().clone();
        SwingUtilities.convertPointToScreen(p, c);

        Point eventPoint = (Point) p.clone();
        SwingUtilities.convertPointFromScreen(p, glassPane);

        //glassPane.setPoint(p);
        glassPane.setVisible(false);
        //glassPane.setImage(null);

        fireGhostDropEvent(new GhostDropEvent(action, eventPoint));
    }
}