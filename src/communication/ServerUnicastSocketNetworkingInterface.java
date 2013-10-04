package communication;

import java.io.*;
import java.net.*;

import communication.protocols.*;
import communication.messages.*;

public class ServerUnicastSocketNetworkingInterface extends ServerNetworkingInterface {

	private Protocol protocol = null;
	private int port;
	
	public ServerUnicastSocketNetworkingInterface(Protocol protocol, int port) {
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
	
	protected class MessageHandler extends Thread {
		
		private Socket socket = null;
		private Protocol protocol = null;
		
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
		ServerUnicastSocketNetworkingInterface i = new ServerUnicastSocketNetworkingInterface(new LoopbackProtocol(), 8000);
		try {
			i.acceptConnections();
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
	}
}
