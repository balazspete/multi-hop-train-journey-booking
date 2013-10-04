package communication;

import java.io.*;
import java.net.*;

import communication.protocols.*;
import communication.messages.*;

/**
 * A client-to-server networking server implemented using sockets
 * @author Balazs Pete
 *
 */
public class UnicastSocketNetworkingServer extends ServerCommunicationInterface {

	private Protocol protocol = null;
	private int port;
	
	/**
	 * Create an instance of UnicastSocketNetworkingServer
	 * @param protocol The protocol to used to handle messages
	 * @param port The port to bind the interface to
	 */
	public UnicastSocketNetworkingServer(Protocol protocol, int port) {
		this.protocol = protocol;
		this.port = port;
	}

	@Override
	public void acceptConnections() throws CommunicationException {
		ServerSocket serverSocket = null;
        boolean listening = true;

        try {
            serverSocket = new ServerSocket(port);
            
            while (listening) {
            	new MessageHandler(protocol, serverSocket.accept()).start();
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
		private Protocol protocol = null;
		
		/**
		 * Create a new instance of MessageHandler
		 * @param protocol The protocol to use
		 * @param socket The socket to use to send reply
		 */
		public MessageHandler(Protocol protocol, Socket socket) {
			this.socket = socket;
			this.protocol = protocol;
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
			    	
					Message message = protocol.processMessage(object);
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
		UnicastSocketNetworkingServer i = new UnicastSocketNetworkingServer(new LoopbackProtocol(), 8000);
		try {
			i.acceptConnections();
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
	}
}
