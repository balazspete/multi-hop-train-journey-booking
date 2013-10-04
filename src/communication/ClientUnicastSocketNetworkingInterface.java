package communication;

import java.io.*;
import java.net.*;

import communication.messages.*;

public class ClientUnicastSocketNetworkingInterface extends Thread implements
		ClientNetworkingInterface {

	private String host;
	private int port;
	
	private Socket socket;
	
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;
	
	public ClientUnicastSocketNetworkingInterface(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	@Override
	public boolean createConnection() throws CommunicationException {
		try {
			socket = new Socket(host, port);
		} catch (UnknownHostException e1) {
			throw new CommunicationException("Specified host `" + host + "` is unreachable (or does not exist)");
		} catch (IOException e1) {
			throw new CommunicationException("Cannot connect to `" + host + "` on port " + port);
		}
		
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
        	throw new CommunicationException("Could not listen on port " + port);
        }

        return true;
	}

	@Override
	public boolean endConnection() throws CommunicationException {
		try {
			out.close();
			in.close();
			socket.close();
		} catch (IOException e) {
			throw new CommunicationException("Could not close connection");
		}
		
		return true;
	}
	
	@Override
	public void sendMessage(Message message) throws CommunicationException {
		try {
			out.writeObject(message);
		} catch (IOException e) {
			throw new CommunicationException("Failed to serialize message"); 
		}

		try {
			out.flush();
		} catch (IOException e) {
			throw new CommunicationException("Failed to send message to server"); 
		}
	}

	@Override
	public Message getMessage() throws CommunicationException {
		Object object;
		try {
			if ((object = in.readObject()) == null) {
				throw new CommunicationException("Connection with server terminated");
			}
		} catch (CommunicationException e) {
			throw e;
		} catch (IOException e) {
			throw new CommunicationException("Error while reading message");
		} catch (ClassNotFoundException e) {
			throw new CommunicationException("Received message is invalid");
		}
		
		return (Message) object;
	}

	public static void main(String[] args) {
		ClientUnicastSocketNetworkingInterface i=new ClientUnicastSocketNetworkingInterface("localhost", 8000);
		try {
			i.createConnection();
			i.sendMessage(new TextMessage("hello"));
			
			Message m = i.getMessage();
			i.sendMessage(new TextMessage("hello"));
			i.endConnection();
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
		
	}
	
}
