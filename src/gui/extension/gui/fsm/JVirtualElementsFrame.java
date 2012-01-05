package extension.gui.fsm;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import extension.gui.elements.VirtualElement;
import extension.model.FSM;
import extension.utils.IconBank;

abstract class JVirtualElementsFrame extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private FSM fsm;
	
	JVirtualElementsFrame(FSM fsm)
	{
		this.fsm=fsm;
		
		this.setVisible(true);
		this.setLayout(new GridLayout(0,1));
		this.setTitle(getFrameTitle());
		
		JButton addElementButton = new JButton(getButtonText());
		addElementButton.setToolTipText(getButtonTooltipText());
		addElementButton.setActionCommand("addElement");
		addElementButton.addActionListener(this);
		this.add(addElementButton);
		
		for (Object element : getExistingElements())
			addRow((VirtualElement)element);
		
		this.pack();
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if ( e.getActionCommand() == "addElement" )
			addRow();
	}
	
	private void addRow()
	{
		VirtualElement element = addElement();
		addRow(element);
	}
	
	private void addRow(VirtualElement element)
	{
		this.add(new JElementRow(this,element));
		this.pack();
	}
	
	private void removeRow(JElementRow row)
	{
		removeElement(row.getElement());
		this.remove(row);
		this.pack();
	}
	
	protected FSM getFSM() { return fsm; }
	
	protected abstract String getFrameTitle();
	protected abstract String getButtonText();
	protected abstract String getButtonTooltipText();
	protected abstract Iterable<?> getExistingElements();
	protected abstract VirtualElement addElement();
	protected abstract void removeElement(VirtualElement element);
	
	private class JElementRow extends JPanel implements ActionListener
	{
		private static final long serialVersionUID = 1L;
		private JVirtualElementsFrame frame;
		private VirtualElement element;
		private JFormattedTextField thresholdField;
		
		private JElementRow(JVirtualElementsFrame frame, VirtualElement element)
		{
			this.frame=frame;
			this.element=element;
			
			addIdField();
			addCancelButton();
		}
		
		private void addIdField()
		{
			thresholdField = new JFormattedTextField();
			thresholdField.setValue(this.element.getDescription());
			thresholdField.setColumns(10);
			thresholdField.getDocument().addDocumentListener(
				new DocumentListener()
				{
					
					@Override
					public void removeUpdate(DocumentEvent e) {
						element.setDescription((String)thresholdField.getValue());
					}
					
					@Override
					public void insertUpdate(DocumentEvent e) {
						element.setDescription((String)thresholdField.getValue());
					}
					
					@Override
					public void changedUpdate(DocumentEvent e) {
						element.setDescription((String)thresholdField.getValue());
					}
				}
			);
			this.add(thresholdField);
		}
		
		private void addCancelButton()
		{
			Icon icon = IconBank.getByUrl("images/close.png", 20, 20);
			JButton closeButton = new JButton(icon);
			closeButton.setToolTipText("eliminar");
			closeButton.setActionCommand("delete");
			closeButton.addActionListener(this);
			this.add(closeButton);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if ( e.getActionCommand() == "delete" )
			{
				frame.removeRow(this);
			}
			else
			{
				System.out.println("new action "+e.getActionCommand());
			}
		}
		
		private VirtualElement getElement() { return element; }
	}
}
