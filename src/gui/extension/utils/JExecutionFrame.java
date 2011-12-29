package extension.utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.swing.JButton;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

public class JExecutionFrame extends JConsole implements ActionListener
{
	private static final long serialVersionUID = 1L;

	private JButton stopButton;
	
	// Executor for the program
	private DefaultExecutor executor;
	
	// Watchdog used to kill the program manually if needed
	private ExecuteWatchdog processWatchdog;
	
	public JExecutionFrame(String command)
	{
		super();
		
		addStopButton();
		
		this.pack();
		
		startExecution(command);
	}
	
	private void startExecution(String command)
	{
		CommandLine commandLine = CommandLine.parse(command);
		
		executor = new DefaultExecutor();
		
		processWatchdog = new ExecuteWatchdog(ExecuteWatchdog.INFINITE_TIMEOUT);
		executor.setWatchdog(processWatchdog);
		
		// provide a pipe end for the processStreamHandler to write on
		PipedOutputStream pipeInput = new PipedOutputStream();
		PumpStreamHandler processStreamHandler = new PumpStreamHandler(pipeInput);
		executor.setStreamHandler(processStreamHandler);
		
		// Stream where the process writes its out/err streams
		PipedInputStream processOutputStream = null;
		
		try {
			processOutputStream = new PipedInputStream(pipeInput);
		} catch (IOException e) {
			System.err.println("JExecutionFrame - IOException creating pipe from program output stream."+"\n"+e.getMessage());
			e.printStackTrace();
			return;
		}
		
		DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
		
		try {
			executor.execute(commandLine, resultHandler);
		} catch (ExecuteException e) {
			System.err.println("JExecutionFrame - ExecuteException trying to execute $ "+command+"\n"+e.getMessage());
			e.printStackTrace();
			return;
		} catch (IOException e) {
			System.err.println("JExecutionFrame - ExecuteException trying to execute $ "+command+"\n"+e.getMessage());
			e.printStackTrace();
			return;
		}
		
		start(processOutputStream);
	}
	
	@Override
	public void close()
	{
		super.close();
		
		processWatchdog.destroyProcess();
		 
		stopButton.setEnabled(false);
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
			close();
		}
		else {
			System.err.println("JExecutionFrame::actionPerformed - unhandled action "+e.getActionCommand());
		}
	}
}
