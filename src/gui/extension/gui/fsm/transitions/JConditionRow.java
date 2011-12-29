package extension.gui.fsm.transitions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import extension.model.elements.VirtualElement;
import extension.model.fsm.transitions.Condition;
import extension.utils.IconBank;

public class JConditionRow extends JPanel implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private JComboBox triggerElementsBox = null;
	private JComboBox operatorBox = null;
	private JFormattedTextField thresholdField = null;
	private JEditTransitionFrame frame = null;
	private Condition condition = null;
	
	public JConditionRow(JEditTransitionFrame frame, Condition condition)
	{
		this.frame=frame;
		this.condition=condition;
		
		addTriggerElementsBox();
		addOperatorBox();
		addThresholdField();
		addCancelButton();
		
		this.triggerElementsBox.setSelectedItem(condition.getTriggerElement());
		this.operatorBox.setSelectedItem(condition.getOperator()); 
		this.thresholdField.setValue(condition.getThreshold());
	}
	
	private void addTriggerElementsBox()
	{
		Vector<VirtualElement> triggerElements = Condition.getTriggerElements();
		
		triggerElementsBox = new JComboBox(triggerElements);
		triggerElementsBox.addActionListener(this);
		
		this.add(triggerElementsBox);
	}
	
	private void addOperatorBox()
	{
		String[] operators = Condition.getOperators();
		
		operatorBox = new JComboBox(operators);
		operatorBox.addActionListener(this);
		
		this.add(operatorBox);
	}
	
	private void addThresholdField()
	{
		thresholdField = new JFormattedTextField(NumberFormat.getNumberInstance());
		thresholdField.setValue(condition.getDefaultThreshold());
		thresholdField.setColumns(10);
		
		thresholdField.getDocument().addDocumentListener(
			new DocumentListener()
			{
				
				@Override
				public void removeUpdate(DocumentEvent e) {
					condition.setThreshold((Number)thresholdField.getValue());
				}
				
				@Override
				public void insertUpdate(DocumentEvent e) {
					condition.setThreshold((Number)thresholdField.getValue());
				}
				
				@Override
				public void changedUpdate(DocumentEvent e) {
					condition.setThreshold((Number)thresholdField.getValue());
				}
			}
		);
		
		this.add(thresholdField);
	}
	
	private void addCancelButton()
	{
		Icon icon = IconBank.getByUrl("images/close.png", 20, 20);
		JButton closeButton = new JButton(icon);
		closeButton.setToolTipText("eliminar condicion");
		closeButton.setActionCommand("removeCondition");
		closeButton.addActionListener(this);
		this.add(closeButton);
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if ( e.getActionCommand() == "comboBoxChanged" )
		{
			JComboBox box = (JComboBox)e.getSource();
			
			if (box==this.triggerElementsBox)
			{
				VirtualElement elem = (VirtualElement) this.triggerElementsBox.getSelectedItem();
				this.condition.setTriggerElement(elem); 
			}
			else if (box==this.operatorBox)
			{
				String elem = (String) this.operatorBox.getSelectedItem();
				this.condition.setOperator(elem);
			}
		}
		else if ( e.getActionCommand() == "removeCondition" )
		{
			this.frame.removeCondition(this);
		}
		else
		{
			System.out.println(e.getActionCommand().toString());
		}
	}
	
	public Condition getCondition() { return condition; }
}
