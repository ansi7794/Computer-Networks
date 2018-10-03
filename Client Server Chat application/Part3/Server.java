//package broadcast;

import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.*;



/*
 * A server that delivers status messages to other users.
 */
public class Server {

	// Create a socket for the server 
	private static ServerSocket serverSocket = null;
	// Create a socket for the server 
	private static Socket userSocket = null;
	// Maximum number of users 
	private static int maxUsersCount = 5;
	// An array of threads for users
	private static userThread[] threads = null;

	private static String userName = null;
	private static ArrayList<String> friends = new ArrayList<String>();

	public static void main(String args[]) {

		// The default port number.
		int portNumber = 8000;
		if (args.length < 2) {
			System.out.println("Usage: java Server <portNumber>\n"
					+ "Now using port number=" + portNumber + "\n" +
					"Maximum user count=" + maxUsersCount);
		} else {
			portNumber = Integer.valueOf(args[0]).intValue();
			maxUsersCount = Integer.valueOf(args[1]).intValue();
		}

		System.out.println("Server now using port number=" + portNumber + "\n" + "Maximum user count=" + maxUsersCount);
		
		
		userThread[] threads = new userThread[maxUsersCount];


		/*
		 * Open a server socket on the portNumber (default 8000). 
		 */
		try {
			serverSocket = new ServerSocket(portNumber);
		} catch (IOException e) {
			System.out.println(e);
		}

		/*
		 * Create a user socket for each connection and pass it to a new user
		 * thread.
		 */
		while (true) {
			try {
				userSocket = serverSocket.accept();
				int i = 0;
				for (i = 0; i < maxUsersCount; i++) {
					if (threads[i] == null) {
						threads[i] = new userThread(userSocket, threads, userName, friends);
						threads[i].start();
						break;
					}
				}
				if (i == maxUsersCount) {
					PrintStream output_stream = new PrintStream(userSocket.getOutputStream());
					output_stream.println("#busy");
					output_stream.close();
					userSocket.close();
				}
			} catch (IOException e) {
				System.out.println(e);
			}
		}
	}
}

/*
 * Threads
 */

class userThread extends Thread {

	private String userName = null;
	private BufferedReader input_stream = null;
	private PrintStream output_stream = null;
	private Socket userSocket = null;
	private final userThread[] threads;
	private static ArrayList<String> friends = new ArrayList<String>();
	private int maxUsersCount;

	public userThread(Socket userSocket, userThread[] threads, String userName, ArrayList<String> friends) {
		this.userSocket = userSocket;
		this.threads = threads;
		maxUsersCount = threads.length;
		this.userName = userName;
		this.friends = friends;
	}

	public void run() {
		int maxUsersCount = this.maxUsersCount;
		userThread[] threads = this.threads;

		try {
			/*
			 * Create input and output streams for this client.
			 * Read user name.
			 */
			input_stream = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));
			output_stream = new PrintStream(userSocket.getOutputStream());
			String userinput = input_stream.readLine();
			/* Welcome the new user. */
			if(userinput.contains("#join"))
			{
				userName = userinput.replace("#join ","");
				output_stream.println("#welcome");
				synchronized (userThread.class) {
					for (int i = 0; i < maxUsersCount; i++) {
						if (threads[i] != null && threads[i]== this) {
							threads[i].userName = userinput.replace("#join ","");
						}
						if (threads[i] != null && threads[i]!= this) {
							threads[i].output_stream.println("#newUser "+ userinput.replace("#join ",""));
						}
					}
				}
			}
			/* Start the conversation. */
			while (true) {
				String responseLine = input_stream.readLine();

				if(responseLine.contains("#status"))
				{
					String newsta = responseLine.replace("#status ", "");
					output_stream.println("#statusUpdated");

					synchronized (userThread.class) {
						boolean once = false;
						for (int i = 0; i < maxUsersCount; i++) {
							for(int j = 0; j < this.friends.size(); j++) {
								if (threads[i] != null && threads[i]!= this && !once && this.friends.get(j) != null && threads[i].userName.contains(this.friends.get(j))){
									System.out.println("2"+threads[i].userName);
									threads[i].output_stream.println("#newStatus " + this.userName +" "+ newsta);
									once = true;
								}
							}
						}
						once = false;
					}
				}
				if(responseLine.contains("#Bye"))
				{
					output_stream.println("#Bye "+ this.userName);
					synchronized (userThread.class) {
						for (int i = 0; i < maxUsersCount; i++) {
							if (threads[i] != null && threads[i]!= this) {
								threads[i].output_stream.println("#Leave "+ this.userName);
							}
						}
					}
					break;
				}
				if(responseLine.contains("#friendme"))
				{
					synchronized (userThread.class) {
						for (int i = 0; i < maxUsersCount; i++) {
							if (threads[i] != null && threads[i]!= this && threads[i].userName.contains(responseLine.replace("#friendme ", ""))) {
								threads[i].output_stream.println("#friendme "+ this.userName);
							}
						}
					}
				}
				if(responseLine.contains("#friends"))
				{
					boolean an1 = false;
					boolean an2 = false;
					synchronized (userThread.class) {
						for (int i = 0; i < maxUsersCount; i++) {
							if (threads[i] != null && threads[i]!= this && responseLine.replace("#friends ", "").contains(threads[i].userName)) {
								if (threads[i].friends != null && !threads[i].friends.contains(this.userName) && !an1) {
									threads[i].friends.add(this.userName);
									threads[i].output_stream.println("#OKFriends "+ this.userName +" " + responseLine.replace("#friends ", ""));
									an1 = true;
								}
							}
							if (threads[i] != null && threads[i] == this) {
								if (threads[i].friends != null && !threads[i].friends.contains(responseLine.replace("#friends ", "")) && !an2) {
									threads[i].output_stream.println("#OKFriends "+ this.userName +" " + responseLine.replace("#friends ", ""));
									threads[i].friends.add(responseLine.replace("#friends ", ""));
									an2 = true;
								}
							}
						}
					}
					an1 = false;
					an2 = false;
				}
				if(responseLine.contains("#DenyFriendRequest"))
				{
					synchronized (userThread.class) {
						for (int i = 0; i < maxUsersCount; i++) {
							if (threads[i] != null && threads[i]!= this && threads[i].userName.contains(responseLine.replace("#DenyFriendRequest ", ""))) {
								threads[i].output_stream.println("#FriendRequestDenied " + this.userName);
							}
						}
					}
				}
				if(responseLine.contains("#unfriend"))
				{
					boolean an1 = false;
					boolean an2 = false;
					synchronized (userThread.class) {
						for (int i = 0; i < maxUsersCount; i++) {
							if (threads[i] != null && threads[i]!= this && threads[i].userName.contains(responseLine.replace("#unfriend ", ""))) {
								if (threads[i].friends != null && threads[i].friends.contains(this.userName) && !an1) {
									threads[i].friends.remove(this.userName);
									threads[i].output_stream.println("#NotFriends "+ this.userName +" " + responseLine.replace("#unfriend ", ""));
									an1 = true;
								}
							}
							if (threads[i] != null && threads[i] == this) {
								if (threads[i].friends != null && threads[i].friends.contains(responseLine.replace("#unfriend ", "")) && !an2) {
									threads[i].output_stream.println("#NotFriends "+ this.userName +" " + responseLine.replace("#unfriend ", ""));
									threads[i].friends.remove(responseLine.replace("#unfriend ", ""));
									an2 = true;
								}
							}
						}
					}
					an1 = false;
					an2 = false;
				}
			}

			// conversation ended.

			/*
			 * Clean up. Set the current thread variable to null so that a new user
			 * could be accepted by the server.
			 */
			synchronized (userThread.class) {
				for (int i = 0; i < maxUsersCount; i++) {
					if (threads[i] == this) {
						threads[i] = null;
					}
				}
			}
			/*
			 * Close the output stream, close the input stream, close the socket.
			 */
			input_stream.close();
			output_stream.close();
			userSocket.close();
		} catch (IOException e) {
		}
	}
}