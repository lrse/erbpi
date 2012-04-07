package extension.utils.execution;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

public class JExecutionFrame extends JConsole
{
	private static final long serialVersionUID = 1L;
	
	// Executor for the program
	private DefaultExecutor executor;
	
	// Watchdog used to kill the program manually if needed
	private ExecuteWatchdog processWatchdog;
	
	public JExecutionFrame(String command)
	{
		super();
		
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
/*
 * @author tfischer
 * Descomentar si queremos que el output del subproceso que llamamos 
 * sea pipeado a nuestra consola de la GUI.
 * 		
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
*/		
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
		
		start(/*processOutputStream*/);
	}
	
	@Override
	protected void stop()
	{
		super.stop();
		
		processWatchdog.destroyProcess();
		
		DisableStopButton();
	}
}
