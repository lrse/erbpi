package extension.utils.execution;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.UnknownHostException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;


import javax.swing.JOptionPane;

public class JRemoteExecutionFrame extends JConsole
{
	private static final long serialVersionUID = 1L;
	
	Socket socket;
	String ip;
	int port;
	
	public JRemoteExecutionFrame(File programFile, String ip, int port)
	{
		super();

		this.ip = ip;
		this.port = port;

		try{
			socket = new Socket(ip, port);
		
			sendProgram(socket, programFile);
/*			
			//InputStream program_output = socket.getInputStream();
			// no anda bien, lo cambio por un cartelito!
	        String texto = "\n >> Ejecutando el CORE localmente en el Robot EXABOT...\n";

	        try{
	        	InputStream program_output = new ByteArrayInputStream(texto.getBytes("UTF-8"));
	        	start(program_output);
	        }
	        catch( UnsupportedEncodingException e ){
	        	e.printStackTrace();
	        }
*/
			start();
		}
		catch (ConnectException e)
		{
			JOptionPane.showMessageDialog(this, "No se pudo establecer una coneccion con "+ip+":"+port,"establishing remote connection",JOptionPane.WARNING_MESSAGE);
		}
		catch (UnknownHostException e) {
			JOptionPane.showMessageDialog(this, "No se pudo establecer una coneccion con "+ip+":"+port,"establishing remote connection",JOptionPane.WARNING_MESSAGE);
		}
		catch (NoRouteToHostException e) {
			JOptionPane.showMessageDialog(this, "No se pudo establecer una coneccion con "+ip+":"+port,"establishing remote connection",JOptionPane.WARNING_MESSAGE);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void sendProgram(Socket socket, File programFile)
	{
		try
		{
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new FileReader(programFile));
			
			String text = new String(); 
		 	while (in.ready())
		 	{
		 		text += in.readLine() + "\n";
		 	}
		 	
		 	out.print(text);
		 	
		 	in.close(); 
		 	out.close();
		 	this.socket.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
	@Override
	protected void stop()
	{
		super.stop();
		
		try{
			socket = new Socket(ip, port);
			
			PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true); 
		  	out.print("STOP\n"); 
		  	out.close();
		  	
			this.socket.close();
		}

		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
