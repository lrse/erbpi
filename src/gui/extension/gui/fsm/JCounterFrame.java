package extension.gui.fsm;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;

import extension.model.FSM;
import extension.model.elements.Counter;
import extension.model.elements.VirtualElement;

class JCounterFrame extends JVirtualElementsFrame
{
	private static final long serialVersionUID = 1L;

	JCounterFrame(FSM fsm) {
		super(fsm);
		FSM.countersFrameOpen = true;
		this.addWindowListener( new java.awt.event.WindowAdapter() {
			public void windowClosing( WindowEvent winEvt ){
				FSM.countersFrameOpen = false;
			}
		});
		// variables para centrar la ventana en la pantalla y definir tamaño ventana...
		Dimension tamanioPantalla = Toolkit.getDefaultToolkit().getScreenSize();
//		Dimension tamanioVentana = new Dimension( 185,70 );
		Dimension tamanioVentana = this.getSize();
		// ubico la ventana en el centro de la pantalla...
		this.setSize( tamanioVentana );
		this.setResizable( true );
		this.setLocation(((tamanioPantalla.width - tamanioVentana.width)/2), (tamanioPantalla.height - tamanioVentana.height)/2);
	}

	@Override
	protected String getButtonText() {
		return "Nuevo contador";
	}

	@Override
	protected String getButtonTooltipText() {
		return "Haga click para agregar un nuevo contador";
	}

	@Override
	protected VirtualElement addElement() {
		return getFSM().addNewCounter();
	}

	@Override
	protected void removeElement(VirtualElement element) {
		getFSM().removeCounter((Counter)element);
	}

	@Override
	protected String getFrameTitle() {
		return "Contadores";
	}

	@Override
	protected Iterable<?> getExistingElements() {
		return getFSM().getCounters();
	}

}
