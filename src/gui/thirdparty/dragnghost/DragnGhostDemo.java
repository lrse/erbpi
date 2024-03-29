package thirdparty.dragnghost;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

public class DragnGhostDemo extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	private GhostGlassPane glassPane;

    public DragnGhostDemo()
    {
		super("Drag n' Ghost Demo");
		setDefaultCloseOperation(EXIT_ON_CLOSE);

        glassPane = new GhostGlassPane();
        setGlassPane(glassPane);

        TableModel dataModel = new AbstractTableModel()
        {
			private static final long serialVersionUID = 1L;
			public int getColumnCount() { return 10; }
            public int getRowCount() { return 10;}
            public Object getValueAt(int row, int col) { return new Integer((row + 1) * (col + 1)); }
        };
        JTable table = new JTable(dataModel);

        GhostDropListener listener = new GhostDropManagerDemo(table);
        GhostPictureAdapter pictureAdapter;
        NewGhostBoxHandler componentAdapter;

        JLabel label;
        Box box = Box.createVerticalBox();
        box.setBorder(new EmptyBorder(0, 0, 0, 20));

        box.add(label = UIHelper.createLabel("New Sale", "new_sale"));
        label.addMouseListener(pictureAdapter = new GhostPictureAdapter(glassPane, "new_sale", "images/new_sale.png"));
        pictureAdapter.addGhostDropListener(listener);
        label.addMouseMotionListener(new GhostBoxMotionHandler(glassPane));

        box.add(label = UIHelper.createLabel("View Sale", "view_sale"));
        label.addMouseListener(pictureAdapter = new GhostPictureAdapter(glassPane, "view_sale", "images/view_sale.png"));
        pictureAdapter.addGhostDropListener(listener);
        label.addMouseMotionListener(new GhostBoxMotionHandler(glassPane));

        box.add(label = UIHelper.createLabel("Quit", "quit"));
        label.addMouseListener(pictureAdapter = new GhostPictureAdapter(glassPane, "quit", "images/quit.png"));
        pictureAdapter.addGhostDropListener(listener);
        label.addMouseMotionListener(new GhostBoxMotionHandler(glassPane));

        Container c = getContentPane();
		c.setLayout(new BorderLayout());
		c.add(BorderLayout.WEST, box);

        JPanel buttons = new JPanel();
        JButton button;

        buttons.add(button = new JButton("Drag n' Drop"));
        button.addMouseListener(componentAdapter = new NewGhostBoxHandler(glassPane, "button1"));
        componentAdapter.addGhostDropListener(listener);
        button.addMouseMotionListener(new GhostBoxMotionHandler(glassPane));

        buttons.add(button = new JButton("Ghost Alike"));
        button.addMouseListener(componentAdapter = new NewGhostBoxHandler(glassPane, "button2"));
        componentAdapter.addGhostDropListener(listener);
        button.addMouseMotionListener(new GhostBoxMotionHandler(glassPane));

        c.add(BorderLayout.SOUTH, buttons);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.addMouseListener(new NewGhostBoxHandler(glassPane, "table"));
        scrollPane.addMouseMotionListener(new GhostBoxMotionHandler(glassPane));

        c.add(BorderLayout.CENTER, scrollPane);

        JPanel headerPanel = new JPanel(new BorderLayout());
        HeaderPanel header = new HeaderPanel();
        headerPanel.add(BorderLayout.NORTH, header);
        headerPanel.add(BorderLayout.SOUTH, new JSeparator(JSeparator.HORIZONTAL));
        headerPanel.setBorder(new EmptyBorder(0, 0, 6, 0));
        c.add(BorderLayout.NORTH, headerPanel);

        pack();
        setResizable(false);
		setLocationRelativeTo(null);
    }

 	public static void main(String[] args)
	{
		DragnGhostDemo demo = new DragnGhostDemo();
		demo.setVisible(true);
	}
}