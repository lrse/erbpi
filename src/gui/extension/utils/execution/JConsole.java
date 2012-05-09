package extension.utils.execution;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import extension.gui.layouts.VerticalFlowLayout;

class JConsole extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 1L;
	private static final String NEWLINE = "\n";
	
	//private TextArea console;
	private JTextArea console;
	private JScrollPane scrollPane;
//	private Thread monitorThread;
	
	private JButton stopButton;
	private boolean running = false;
	
	JConsole()
	{
		this.setLayout(new VerticalFlowLayout());
		
		addConsole();
		
		addStopButton();
		
		this.pack();
		
		// este listener se encarga de hacer el cleanup de las
		// cosas que se estan ejecutando si alguien cierra
		// la ventana de ejecucion antes de parar el prgrama. 
		this.addWindowListener( new WindowAdapter() {
			public void windowClosing(WindowEvent e) { if (running) stop(); }
		});
	}
	
	protected void start(/*InputStream stream*/)
	{
//		monitorThread = new ReaderThread(stream);
//		monitorThread.start();
		
		this.console.append("Iniciando ejecución.\n");
		
		this.setVisible(true);
		
		running = true;
	}
	
	protected void stop()
	{
		running = false;
		
		// TODO ver si esto mata el thread
		System.out.println("waiting for monitorThread to finish");
/*
		monitorThread.interrupt();
		try {
			monitorThread.join();
		} catch (InterruptedException e) {
			System.err.println("JExecutionFrame - InterruptedException waiting for monitorThread to finish.");
			e.printStackTrace();
			return;
		}
*/
		System.out.println("monitorThread finished!");
		
		// show termination message
		console.append("> Process terminated by user." + NEWLINE);
		this.dispose();
	}
	
	private void addConsole()
	{
		//console = new TextArea(20,50);
		console = new JTextArea();
		console.setEditable(false);
		
		scrollPane = new JScrollPane(console);
		scrollPane.setPreferredSize(new Dimension(200,100));
		
		this.add(scrollPane);
	}
	
	private class ReaderThread extends Thread
	{
		private InputStream processOutputReader;
		
		public ReaderThread(InputStream processOutputStream)
		{
			// leo el output del core
			processOutputReader = processOutputStream;
		}
		
		public void run()
		{
            final byte[] buf = new byte[1024];
            try {
                while (!isInterrupted())
                	if (processOutputReader.available() > 0)
                	{
                		final int len = processOutputReader.read(buf);
                        
                        if (len != -1)
    		                SwingUtilities.invokeLater(new Runnable() {
    		                    public void run() {
    		                        console.append(new String(buf, 0, len));
    		
    		                        // Make sure the last line is always visible
    		                        console.setCaretPosition(console.getDocument().getLength());
    		
    		                        // Keep the text area down to a certain character size
    		                        int idealSize = 1000;
    		                        int maxExcess = 500;
    		                        int excess = console.getDocument().getLength() - idealSize;
    		                        if (excess >= maxExcess) {
    		                            console.replaceRange("", 0, excess);
    		                        }
    		                    }
    		                });
                	}
            } catch (IOException e) {
            	// this means the input stream was closed.
            	// Its ok, so we handle the cleanup
            }
        }
		
		@Override
		public void interrupt()
		{
			try {
				System.out.println("closing reader");
				processOutputReader.close();
				System.out.println("reader closed");
			} catch (IOException e) {
				System.err.println("JExecutionFrame::iOBlockedThread::interrupt() - IOException closing stream on interrupt.");
				e.printStackTrace();
			}
			
			super.interrupt();
		}
	}
	
	private void addStopButton()
	{
		stopButton = new JButton("Parar");
		stopButton.setToolTipText("Terminar la ejecucion actual");
		stopButton.setActionCommand("stop");
		stopButton.addActionListener(this);
		this.add(stopButton);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if ( e.getActionCommand() == "stop" ) {
			stop();
		}
		else {
			System.err.println("JExecutionFrame::actionPerformed - unhandled action "+e.getActionCommand());
		}
	}
	
	protected void DisableStopButton()
	{
		stopButton.setEnabled(false);
	}
}
