package extension.utils;

import java.io.IOException;
import java.io.InputStream;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import extension.gui.layouts.VerticalFlowLayout;

public class JConsole extends JFrame
{
	private static final long serialVersionUID = 1L;
	private static final String NEWLINE = "\n";
	
	//private TextArea console;
	private JTextArea console;
	private Thread monitorThread;
	
	public JConsole()
	{
		this.setLayout(new VerticalFlowLayout());
		
		addConsole();
	}
	
	public void start(InputStream stream)
	{
		monitorThread = new ReaderThread(stream);
		monitorThread.start();
		
		this.setVisible(true);
	}
	
	public void close()
	{
		// TODO ver si esto mata el thread
		System.out.println("waiting for monitorThread to finish");
		monitorThread.interrupt();
		try {
			monitorThread.join();
		} catch (InterruptedException e) {
			System.err.println("JExecutionFrame - InterruptedException waiting for monitorThread to finish.");
			e.printStackTrace();
			return;
		}
		System.out.println("monitorThread finished!");
		
		// show termination message
		console.append("> Process terminated by user." + NEWLINE);
	}
	
	private void addConsole()
	{
		//console = new TextArea(20,50);
		console = new JTextArea(20,50);
		console.setEditable(false);
		this.add(console);
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
}
