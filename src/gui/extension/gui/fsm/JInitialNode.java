package extension.gui.fsm;

import javax.swing.Icon;

import extension.model.BehaviorNode;
import extension.utils.IconBank;

class JInitialNode extends JNode
{
	private static final long serialVersionUID = 1L;

	JInitialNode(BehaviorNode behavior, JFSMPanel fsmPanel)
	{
		super(behavior, fsmPanel);
	}

	@Override
	protected Icon getNewIcon() { return IconBank.getByUrl("images/nodo_inicial.png", 60, 60); }
}
