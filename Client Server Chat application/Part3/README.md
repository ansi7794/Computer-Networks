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
* The user enters @connect <username> to connect to the user, it send to the server:
	-- #friendme <username>
* The server then to the <username> client sends:
	-- #friendme <requesterusername>
* The <username> can then choose to @friend <requesterusername> or @deny <requesterussername>,
the client then sends to the server:
	-- #friends <requesterusername>
	OR
	-- #DenyFriendRequest <requesterusername>
* the server then adds them to each other's list if accepted and sends to both:
	-- #OKfriends <username> <requesterusername>
* Else if the request is denies it sends to the requester:
	-- #FriendRequestDenied <username>
* The user enters @disconnect <username> to disconnect to the user, it send to the server:
	-- #unfriend <username>
* The server then sends to the <username> client:
	-- NotFriends <username> <requesterusername>
* The User sends a message to the server for updating status as:
	-- #status <status>
* The server then sends the user:
	-- #statusUpdated
* The server also sends to the client's friends:
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