package extension.gui.program;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

class JInlineDialog extends JLayeredPane
{
	private static final long serialVersionUID = 1L;
	protected JPanel rootPane;	
	protected JPanel glassPane;
	
	public JInlineDialog() {

		glassPane = new JPanel();
		glassPane.setBackground(new Color(255,255,255,160));
		glassPane.setOpaque(true);
		glassPane.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				setVisible(false);
			}
		});
		add(glassPane, new Integer(1));
		
		rootPane = new JPanel();
		rootPane.setBackground(Color.GRAY);
		rootPane.setBorder(new LineBorder(Color.WHITE, 8));
		add(rootPane, new Integer(2));
	}
	
	void run(JLayeredPane owner, Point pScreen) {
		Point p = pScreen;
		SwingUtilities.convertPointFromScreen(p, rootPane);
		System.out.println(p);
		rootPane.setLocation(p);
		setBounds(owner.getBounds());
		glassPane.setBounds(getBounds());
		owner.add(this,new Integer(5));
	}	
}
