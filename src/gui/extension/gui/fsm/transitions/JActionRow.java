package extension.gui.fsm.transitions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import extension.gui.elements.VirtualElement;
import extension.utils.IconBank;

class JActionRow extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	private Action action;
	private JComboBox elementsBox = null;
	private JComboBox operationsBox = null;
	private JEditTransitionFrame frame = null;

	JActionRow(JEditTransitionFrame frame, Action action)
	{
		this.frame = frame;
		this.action = action;
		
		addElementsBox();
		addOperationBox();
		addCancelButton();
		
		this.elementsBox.setSelectedItem(action.getElement());
		this.operationsBox.setSelectedItem(action.getOperation());
	}
	
	private void addElementsBox()
	{
		Vector<VirtualElement> elements = Action.getElements();
		
		elementsBox = new JComboBox(elements);
		elementsBox.addActionListener(this);
		
		this.add(elementsBox);
	}
	
	private void updateOperationsBox()
	{
		this.remove(operationsBox);
		
		String[] operations = action.getElement().getOperations();
		
		operationsBox = new JComboBox(operations);
		operationsBox.addActionListener(this);
		
		this.add(operationsBox,1);
		
		frame.updateLayout();
	}
	
	private void addOperationBox()
	{
		String[] operations = action.getElement().getOperations();
		
		operationsBox = new JComboBox(operations);
		operationsBox.addActionListener(this);
		
		this.add(operationsBox);
	}
	
	private void addCancelButton()
	{
		Icon icon = IconBank.getByUrl("images/close.png", 20, 20);
		JButton closeButton = new JButton(icon);
		closeButton.setToolTipText("eliminar actualizacion");
		closeButton.setActionCommand("delete");
		closeButton.addActionListener(this);
		this.add(closeButton);
	}
	
	public Action getAction() { return action; }
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if ( e.getActionCommand() == "comboBoxChanged" )
		{
			JComboBox box = (JComboBox)e.getSource();
			
			if (box==this.elementsBox)
			{
				VirtualElement elem = (VirtualElement) this.elementsBox.getSelectedItem();
				action.setElement(elem);
				
				updateOperationsBox();
			}
			else if (box==this.operationsBox)
			{
				String elem = (String) this.operationsBox.getSelectedItem();
				action.setOperation(elem);
			}
		}
		else if ( e.getActionCommand() == "delete" )
		{
			frame.removeAction(this);
		}
	}
}
