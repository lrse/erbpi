package extension.gui.fsm;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import extension.gui.JRoboticaFrame;
import extension.model.BehaviorNode;
import extension.utils.IconBank;

public class JBehaviorNode extends JNode implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	private JPanel closeButton = null;
	
	public JBehaviorNode( BehaviorNode behavior, JFSMPanel fsmPanel )
	{
		super(behavior, fsmPanel);
		
		addMouseListener(new OpenNodeHandler());
		
		addCloseButton();
		
		closeButton.addMouseListener(new RemoveNodeHandler());
		
		// le agrego el listener para el menú de cambiar el nombre-descripción...		
		this.addMouseListener(new PopupMenuMouseAdapter(getPopupMenu()));
	}
	
	private void addCloseButton()
	{
		closeButton = new JCloseButton(this);
		// agrego el boton de cerrar en la 2a capa
		closeButton.setLocation( 38,2);
		//closeButton.setVisible(false);
		this.add(closeButton,JLayeredPane.PALETTE_LAYER);
	}
	
	private JPopupMenu getPopupMenu()
	{
    	JPopupMenu popup = new JPopupMenu();
    	JMenuItem item;
    	item = new JMenuItem("Cambiar nombre...");
    	item.setActionCommand("setDescriptionNode");
    	item.addActionListener(this);
    	popup.add(item);
		return popup;
    }
	
	@Override
	protected void createIconLabel()
	{
		super.createIconLabel();
		
		iconLabel.setText( getBehavior().getDescripcion() );
		iconLabel.setHorizontalTextPosition(JLabel.CENTER);
		iconLabel.setVerticalTextPosition(JLabel.BOTTOM);
		iconLabel.setBorder(null);
//		iconLabel.setBorder(LineBorder.createBlackLineBorder());
		iconLabel.setBackground(Color.GRAY);
		iconLabel.setForeground(Color.WHITE);
		Font curFont = iconLabel.getFont();
		iconLabel.setFont(new Font(curFont.getFontName(), curFont.getStyle(), 10));
	}
	
	public void actionPerformed(ActionEvent e){
		if( e.getActionCommand() == "setDescriptionNode" )
			new JCambiarDescripcionNodo( this, iconLabel, fsmPanel );
	}
	
	@Override
	protected Icon getNewIcon() { return IconBank.getByUrl("images/nodo2.png", 60, 73); }
	
	private class OpenNodeHandler extends MouseAdapter
	{
		@Override
		public void mouseClicked( MouseEvent event )
		{
			if ( event.getClickCount() > 1 )
			{
				JNode node = (JNode) event.getComponent();
				JRoboticaFrame.getInstance().OpenProgramFrame( node.getBehavior() );
			}
		}
	}
	
	private class RemoveNodeHandler extends MouseAdapter
	{
		 @Override
		public void mouseClicked(MouseEvent e)
		{
			 JCloseButton closeButton = (JCloseButton) e.getSource();
			 BehaviorNode behavior = closeButton.getNode().getBehavior();
			 
			 fsmPanel.getFSM().removeBehaviorNode(behavior);
         }
	}
	
	private class PopupMenuMouseAdapter extends MouseAdapter{
	    private JPopupMenu popup;

	    public PopupMenuMouseAdapter( JPopupMenu popupMenu ){ popup = popupMenu; }

	    public void mousePressed( MouseEvent e ){ maybeShowPopup(e); }

	    public void mouseReleased( MouseEvent e ){ maybeShowPopup(e); }
	    
	    private void maybeShowPopup(MouseEvent e){
	        if( e.isPopupTrigger() ){
	        	popup.show(e.getComponent(),
	                       e.getX(), e.getY());
	        }
	    }
	}
	
	private class JCloseButton extends JPanel{
		private static final long serialVersionUID = 1L;
		private JNode node;
		
		public JCloseButton( JNode node ){
			super(new GridLayout(1, 1));
			this.node=node;
			this.setOpaque(false);
			Icon icon = IconBank.getByUrl("images/close.png", 20, 20);
			JLabel iconLabel = new JLabel(icon);
			this.add(iconLabel);
			this.setBounds(0, 0, icon.getIconWidth(), icon.getIconHeight());
		}
		
		public JNode getNode(){
			return node;
		}
	}
	
//	private class contenidoFormulario extends JPanel{
//		private static final long serialVersionUID = 1L;
//		final JTextField resultado = new JTextField();
//		
//		public contenidoFormulario(){
//			JPanel componentsPanel = new JPanel( new GridLayout( 2,2 ) );
//			add(componentsPanel, BorderLayout.CENTER);
//			
//			resultado.setColumns(15);
//			resultado.setText( getBehavior().getDescripcion() );
//			
//			JLabel lab2 = new JLabel( "Nombre actual: " , JLabel.RIGHT );
//			componentsPanel.add(lab2);
//			lab2 = new JLabel( getBehavior().getDescripcion() , JLabel.LEFT );
//			componentsPanel.add(lab2);
//			
//			JLabel lab = new JLabel( "Cambiar por: " , JLabel.RIGHT );
//			lab.setLabelFor( resultado );
//			componentsPanel.add(lab);
//			JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
//			p.add( resultado );
//			componentsPanel.add(p);
//		}
//		public String getText( int i ){ return resultado.getText(); }
//	}
}
