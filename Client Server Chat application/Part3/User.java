//package broadcast;

import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.Socket;
import java.net.UnknownHostException;


public class User extends Thread {

	// The user socket
	private static Socket userSocket = null;
	// The output stream
	private static PrintStream output_stream = null;
	// The input stream
	private static BufferedReader input_stream = null;

	private static BufferedReader inputLine = null;
	private static boolean closed = false;

	//username
	private static String username = null;

	private static String newsta = null;

	public static void main(String[] args) {

		// The default port.
		int portNumber = 8000;
		// The default host.
		String host = "localhost";

		if (args.length < 2) {
			System.out.println("Usage: java User <host> <portNumber>\n"
					+ "Now using host=" + host + ", portNumber=" + portNumber);
		} else {
			host = args[0];
			portNumber = Integer.valueOf(args[1]).intValue();
		}

		/*
		 * Open a socket on a given host and port. Open input and output streams.
		 */
		try {
			userSocket = new Socket(host, portNumber);
			inputLine = new BufferedReader(new InputStreamReader(System.in));
			output_stream = new PrintStream(userSocket.getOutputStream());
			input_stream = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + host);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to the host "
					+ host);
		}

		/*
		 * If everything has been initialized then we want to write some data to the
		 * socket we have opened a connection to on port portNumber.
		 */
		if (userSocket != null && output_stream != null && input_stream != null) {
			try {                
				/* Create a thread to read from the server. */
				new Thread(new User()).start();

				// Get user name and join the social net
				System.out.println("-->");

				while (!closed) {
					String userInput = inputLine.readLine().trim();
					
					// Read user input and send protocol message to server
					if(userInput.contains("@connect")){
						userInput = "#friendme " + userInput.replace("@connect ", "");
					}
					if(userInput.contains("@deny"))
					{
						userInput = "#DenyFriendRequest " + userInput.replace("@deny ", "");
					}
					if(userInput.contains("@friend"))
					{
						userInput = "#friends " + userInput.replace("@friend ", "");
					}
					if(userInput.contains("@disconnect")){
						userInput = "#unfriend " + userInput.replace("@disconnect ", "");
					}
					if(userInput.contains("Exit")){
						userInput = "#Bye";
						closed = true;
					}
					output_stream.println(userInput);
				}
				/*
				 * Close the output stream, close the input stream, close the socket.
				 */
			} catch (IOException e) {
				System.err.println("IOException:  " + e);
			}
		}
	}

	/*
	 * Create a thread to read from the server.
	 */
	public void run() {
		/*
		 * Keep on reading from the socket till we receive a Bye from the
		 * server. Once we received that then we want to break.
		 */
		String responseLine, answer;
		
		try {
			while ((responseLine = input_stream.readLine()) != null) {

				// Display on console based on what protocol message we get from server.
				System.out.println(responseLine);
				if(responseLine.contains("#busy"))
				{
					break;
				}
				if(responseLine.contains("#Bye"))
				{
					break;
				}
				if(responseLine.contains("#newStatus"))
				{
					String responses[] = responseLine.split(" ");
					newsta = responseLine.replace("#newStatus ", "");
					newsta = newsta.replace(responses[1], "");
					System.out.println("The user " + responses[1] +" updated status to" + newsta);
				}
				if(responseLine.contains("#Leave"))
				{
					String responses[] = responseLine.split(" ");
					System.out.println("The user " + responses[1] +" left");
				}
				if(responseLine.contains("#newUser"))
				{
					String responses[] = responseLine.split(" ");
					System.out.println("A new user : " + responses[1] +" joined");
				}
				if(responseLine.contains("#friendme")){
					System.out.println("do you want to be friends with "+responseLine.replace("#friendme ", "").replace("\n","")+"? (@friend <username> or @deny <username>) -->");	
				}
			}
			closed = true;
			output_stream.close();
			input_stream.close();
			userSocket.close();
		} catch (IOException e) {
			System.err.println("IOException:  " + e);
		}
	}
}