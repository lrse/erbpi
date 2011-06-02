package extension;

import javax.swing.SwingUtilities;

import extension.gui.JRoboticaFrame;

public class ExtensionApp {

	public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI(); 
            }
        });
	}

	private static void createAndShowGUI() {
        SwingUtilities.isEventDispatchThread();
        JRoboticaFrame robotica = new JRoboticaFrame();
        robotica.run();
	}	
}
