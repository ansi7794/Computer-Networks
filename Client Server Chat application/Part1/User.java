//package basic;

import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;



public class User {

	// The user socket
	private static Socket userSocket = null;
	// The output stream
	private static PrintStream output_stream = null;
	// The input stream
	private static BufferedReader input_stream = null;

	private static BufferedReader inputLine = null;

	public static void main(String[] args) {

		// The default port.
		int portNumber = 8000;
		// The default host.
		String host = "localhost";
		String responseLine;

		if (args.length < 2) {
			System.out.println("Usage: java User <host> <portNumber>"
					+ "Now using host=" + host + ", portNumber=" + portNumber);
		} else {
			host = args[0];
			portNumber = Integer.valueOf(args[1]).intValue();
			System.out.println("Usage: java User <host> <portNumber>"
					+ "Now using host=" + host + ", portNumber=" + portNumber);
		}

		/*
		 * If everything has been initialized then we want to send user status 
		 * message to the socket we have opened a connection to on port portNumber.
		 * When we receive the acknowledgment, print out success.
		 */
		try { 
			// COMPLETE MISSING CODE HERE
			userSocket = new Socket(host, portNumber);
			output_stream = new PrintStream(userSocket.getOutputStream());
			inputLine = new BufferedReader(new InputStreamReader(System.in));
			input_stream = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));
			System.out.println("Enter your status (#status <status>)");
			//take the status as input
			String message = inputLine.readLine();
			output_stream.println(message);
			String temp = input_stream.readLine();
			if(temp.contains("#statusPosted")==true)
			{
				System.out.println(temp);
			}
			/*
			 * Close the output stream, close the input stream, close the socket.
			 */
			output_stream.close();
			userSocket.close();
		} catch (IOException e) {
			System.err.println("IOException:  " + e);
		}

	}
}