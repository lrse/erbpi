package extension.gui.fsm.transitions;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import extension.gui.layouts.VerticalFlowLayout;
import extension.model.Transition;

class JEditTransitionFrame extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	private Transition transition = null;
	private JPanel conditionsPanel = null;
	private JPanel actionsPanel = null;
	
	JEditTransitionFrame(Transition transition)
	{
		this.setLayout(new VerticalFlowLayout());
		
		this.transition=transition;
		
		this.conditionsPanel = new JPanel(new GridLayout(0,1));
		this.add(this.conditionsPanel);
		
		this.actionsPanel = new JPanel(new GridLayout(0,1));
		this.add(this.actionsPanel);
		
		addConditionButton();
		addActionButton();
		
		loadExistingConditions();
		loadExistingActions();
		
		// actualiza el tamaño del frame acorde al contenido
		this.pack();
		
		// variables para centrar la ventana en la pantalla y definir tamaño ventana...
		Dimension tamanioPantalla = Toolkit.getDefaultToolkit().getScreenSize();
//		Dimension tamanioVentana = new Dimension( 300,100 );
		Dimension tamanioVentana = this.getSize();
		// ubico la ventana en el centro de la pantalla...
		this.setSize( tamanioVentana );
		this.setResizable( true );
		this.setLocation(((tamanioPantalla.width - tamanioVentana.width)/2), (tamanioPantalla.height - tamanioVentana.height)/2);

		this.setVisible(true);
	}
	
	private void addConditionButton()
	{
		JButton addConditionButton = new JButton("Agregar Condición sobre nodo origien");
		addConditionButton.setToolTipText("Agregar una condicion de transicion");
		addConditionButton.setActionCommand("addCondition");
		addConditionButton.addActionListener(this);
		this.conditionsPanel.add(addConditionButton);
	}
	
	private void addActionButton()
	{
		JButton addActionButton = new JButton("Agregar Actualización sobre nodo destino");
		addActionButton.setToolTipText("Agregar una actualizacion a la transicion");
		addActionButton.setActionCommand("addAction");
		addActionButton.addActionListener(this);
		actionsPanel.add(addActionButton);
	}
	
	private void loadExistingConditions()
	{
		for ( Condition condition : this.transition.conditions )
			this.conditionsPanel.add(new JConditionRow(this,condition));
	}
	
	private void loadExistingActions()
	{
		for ( Action action : this.transition.actions )
			this.actionsPanel.add(new JActionRow(this,action));
	}
	
	private void addCondition()
	{
		Condition newCondition = transition.addNewCondition();
		conditionsPanel.add(new JConditionRow(this,newCondition));
		this.pack();
	}
	
	void removeCondition(JConditionRow conditionRow)
	{
		conditionsPanel.remove(conditionRow);
		transition.removeCondition(conditionRow.getCondition());
		this.pack();
	}
	
	private void addAction()
	{
		Action newAction = transition.addNewAction();
		actionsPanel.add(new JActionRow(this,newAction));
		this.pack();
	}
	
	void removeAction(JActionRow actionRow)
	{
		actionsPanel.remove(actionRow);
		transition.removeAction(actionRow.getAction());
		this.pack();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if ( e.getActionCommand() == "addCondition" )
		{
			this.addCondition();
		}
		else if ( e.getActionCommand() == "addAction" )
		{
			if (Action.getElements().size() > 0)
				this.addAction();
			else
				JOptionPane.showMessageDialog(this,"Tiene que haber al menos un timer o contador definido para poder agregar actualizaciones");
		}
	}
	
	void updateLayout()
	{
		pack();
	}
}
