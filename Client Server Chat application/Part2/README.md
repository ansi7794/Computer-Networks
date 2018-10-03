* The User sends a message to join the server:
	-- #join <username>
* If server can entertain the user, the server will reply back:
	-- #welcome
* The server will also send a broadcast protocol message to all other users 
informing them that a new user has joined
	-- #newuser <username> 
* If the server is busy (i.e., number of users reaches a threshold, say 5), the 
server replies back to the client:
	-- #busy
* The User sends a message to the server for updating status as:
	-- #status <status>
* The server then sends the user:
	-- #statusUpdated
* The server also sends to other clients:
	-- #newStatus <username> <status>
* The other clients will post:
	-- <username> updated status to <status>
* When the user enters Exit on the console, The client will send to the server:
	-- #Bye
* The server will send back to achknowledge the user:
	-- #Bye
* The server will also send the other clients:
	-- #Leave <username>
* The other clients will post:
	-- <username> has left
-----------------------------------------------------------------------------

* To run the program open 2 terminals and enter the following commands:
	-- First Terminal(Server):
		-- javac Server.java
		-- java Server <port number>
	-- Second Terminal(User):
		-- javac User.java
		-- java User <server address> <port number>
	-- Use more terminals like the second terminal to determine results.
	-- Now run the rest as per the aforementioned protocol.
-----------------------------------------------------------------------------

* The server remains open. The client closes after recieving #statusUpdated.

-----------------------------------------------------------------------------