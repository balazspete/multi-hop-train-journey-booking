package communication.unicast;

import java.io.*;
import java.net.*;
import java.util.Map;

import communication.CommunicationException;
import communication.protocols.*;
import communication.messages.*;
import data.system.NodeInfo;

/**
 * A client-to-server networking server implemented using sockets
 * @author Balazs Pete
 *
 */
public class UnicastSocketServer extends UnicastServer {

	private int port;
	
	/**
	 * Create an instance of UnicastSocketNetworkingServer
	 * @param port The port to bind the interface to
	 */
	public UnicastSocketServer(int port) {
		this.port = port;
	}

	@Override
	public void acceptConnections() throws CommunicationException {
		ServerSocket serverSocket = null;
        boolean listening = true;
        
        String location = null;
        try {
			location = Inet4Address.getLocalHost().getHostAddress();
		} catch (UnknownHostException e1) {
			// We can't get our IP address, oh well displaying less info...
		}
        
        System.out.println("*******************************************************************************\n" + 
        		"* UnicastSocketServer: Listening to connections" +
        		(location == null ? "" : " at " + location) + " on port "+port + 
        		"\n*******************************************************************************");

        try {
            serverSocket = new ServerSocket(port);
            
            while (listening) {
            	new MessageHandler(protocolMap, serverSocket.accept()).start();
            }
            
			serverSocket.close();
        } catch (Exception e) {
        	throw CommunicationException.CANNOT_USE_PORT;
        }
	}
	
	public void run() {
		try {
			acceptConnections();
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * A message handling wrapper to process messages in a separate thread
	 * @author Balazs Pete
	 *
	 */
	protected class MessageHandler extends Thread {
		
		private Socket socket = null;
		private Map<String, Protocol> protocolMap = null;
		
		/**
		 * Create a new instance of MessageHandler
		 * @param protocol The protocol to use
		 * @param socket The socket to use to send reply
		 */
		public MessageHandler(Map<String, Protocol> protocolMap, Socket socket) {
			this.socket = socket;
			this.protocolMap = protocolMap;
		}
		
		public void run() {
			tryGetSendMessage();
		}	
		
		private void tryGetSendMessage() {
			try {
				// Pass the socket streams to writable object streams
				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				
			    Object object = null;
			    while (true) {
			    	try {
			    		object = in.readObject();
			    	} catch (ClassNotFoundException e) {
			    		// Sent object does not exist 
						e.printStackTrace();
						break;
					} catch (SocketException e) {
						// Connection has been closed
						break;
					} catch (EOFException e) {
						// End of transferred content, just break
						break;
					}
			    	
			    	// If stream has ended, end listening and close communication
			    	if (object == null) break;
			    	
			    	// Determine protocol to be used
			    	Message input = (Message) object;
					NodeInfo node = new NodeInfo(socket.getInetAddress().getHostName());
					node.addLocation(socket.getInetAddress().getHostAddress());
					input.setSender(node);
					
					System.out.println("UnicastSocketServer: Received a " + input.getType() + " from " + input.getSender().getLocation() + " | " + input.getContents());
					
			    	Protocol protocol = protocolMap.get(input.getType().intern());
			    	
			    	// Process message and get response
					Message message;
					if(protocol != null) {
						message = protocol.processMessage(input);
						
						if (!protocol.hasReply() || message == null) {
							break;
						}
					} else {
						System.err.println("UnicastSocketServer: Received an incompatible message. Replying with an ErrorMessage...");
						message = new ErrorMessage("Message type not supported");
					}
					
					// Send message
					out.writeObject(message);
					System.out.println("UnicastSocketServer: Sent a " + message.getType() + " to " + input.getSender().getLocation());// + " | " + message.getContents());
					out.flush();
			    }
			    	
			    out.close();
			    in.close();
			    socket.close();
			} catch (IOException e) {
			    e.printStackTrace();
			}
		}
	}
	
	@Deprecated
	public static void main(String[] args) {
		UnicastSocketServer i = new UnicastSocketServer(8000);
		i.putProtocol(new LoopbackProtocol());
		
		try {
			i.acceptConnections();
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
	}
}
