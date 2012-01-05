package extension.gui.fsm;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;

import extension.gui.elements.Timer;
import extension.gui.elements.VirtualElement;
import extension.model.FSM;

class JTimerFrame extends JVirtualElementsFrame
{
	private static final long serialVersionUID = 1L;

	JTimerFrame(FSM fsm) {
		super(fsm);
		FSM.timersFrameOpen = true;
		this.addWindowListener( new java.awt.event.WindowAdapter() {
			public void windowClosing( WindowEvent winEvt ){
				FSM.timersFrameOpen = false;
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
		return "Nuevo temporizador";
	}

	@Override
	protected String getButtonTooltipText() {
		return "Haga click para agregar un nuevo temporizador";
	}
	
	@Override
	protected VirtualElement addElement() {
		return getFSM().addNewTimer();
	}

	@Override
	protected void removeElement(VirtualElement element) {
		getFSM().removeTimer((Timer)element);
	}

	@Override
	protected String getFrameTitle() {
		return "Temporizadores";
	}

	@Override
	protected Iterable<?> getExistingElements() {
		return getFSM().getTimers();
	}
}
