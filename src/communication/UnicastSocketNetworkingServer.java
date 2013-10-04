package communication;

import java.io.*;
import java.net.*;
import java.util.Map;

import communication.protocols.*;
import communication.messages.*;

/**
 * A client-to-server networking server implemented using sockets
 * @author Balazs Pete
 *
 */
public class UnicastSocketNetworkingServer extends UnicastCommunicationServer {

	private int port;
	
	/**
	 * Create an instance of UnicastSocketNetworkingServer
	 * @param port The port to bind the interface to
	 */
	public UnicastSocketNetworkingServer(int port) {
		this.port = port;
	}

	@Override
	public void acceptConnections() throws CommunicationException {
		ServerSocket serverSocket = null;
        boolean listening = true;

        try {
            serverSocket = new ServerSocket(port);
            
            while (listening) {
            	new MessageHandler(protocolMap, serverSocket.accept()).start();
            }
            
			serverSocket.close();
        } catch (Exception e) {
        	throw new CommunicationException("Could not listen on port " + port);
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
			try {
				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				
			    Object object = null;
			    while (true) {
			    	try {
			    		object = in.readObject();
			    	} catch (ClassNotFoundException e) {
						e.printStackTrace();
						continue;
					} catch (EOFException e) {
						continue;
					}
			    	
			    	if (object == null) break;
			    	
			    	Message input = (Message) object;
			    	
			    	System.out.println(input.getType());
			    	Protocol protocol = protocolMap.get(input.getType().intern());
			    	
					Message message;
					if(protocol != null) {
						message = protocol.processMessage(object);
					} else {
						message = new ErrorMessage("Message type not supported");
					}
					
					out.writeObject(message);
			    }
			    	
			    out.close();
			    in.close();
			    socket.close();
			} catch (IOException e) {
			    e.printStackTrace();
			}
		}	
	}
	
	public static void main(String[] args) {
		UnicastSocketNetworkingServer i = new UnicastSocketNetworkingServer(8000);
		i.putProtocol(new LoopbackProtocol());
		
		try {
			i.acceptConnections();
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
	}
}
