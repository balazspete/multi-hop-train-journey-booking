package communication.unicast;

import java.io.*;
import java.net.*;

import communication.CommunicationException;
import communication.messages.*;

/**
 * A client-to-server networking client implemented using sockets
 * @author Balazs Pete
 *
 */
public class UnicastSocketClient extends Thread implements
		UnicastClient {

	private String host;
	private int port;
	
	private Socket socket;
	
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;
	
    /**
     * Create an instance of {@link UnicastSocketNetworkingClient}
     * @param host The host to connect to 
     * @param port The port to bind to
     */
	public UnicastSocketClient(String host, int port) {
		this.host = host.intern();
		this.port = port;
	}
	
	@Override
	public boolean createConnection() throws CommunicationException {
		try {
			socket = new Socket(host, port);
		} catch (UnknownHostException e1) {
			throw CommunicationException.UNREACHABLE_HOST;
		} catch (IOException e1) {
			throw CommunicationException.CANNOT_OPEN_CONNECTION;
		}
		
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
        	throw CommunicationException.CANNOT_USE_PORT;
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
			throw CommunicationException.CANNOT_CLOSE_CONNECTION;
		}
		
		return true;
	}
	
	@Override
	public void sendMessage(Message message) throws CommunicationException {
		try {
			out.writeObject(message);
		} catch (IOException e) {
			throw CommunicationException.CANNOT_SERIALIZE_MESSAGE;
		}

		try {
			out.flush();
		} catch (IOException e) {
			throw CommunicationException.CANNOT_SEND_MESSAGE;
		}
	}

	@Override
	public Message getMessage() throws CommunicationException, InvalidMessageException {
		Object object;
		try {
			// try to get message and check if input stream has ended
			if ((object = in.readObject()) == null) {
				throw CommunicationException.CONNECTION_TERMINATED;
			}
		} catch (CommunicationException e) {
			throw e; 
		} catch (EOFException e) {
			throw CommunicationException.CONNECTION_TERMINATED;
		} catch (IOException e) {
			throw CommunicationException.CANNOT_READ_MESSAGE;
		} catch (ClassNotFoundException e) {
			throw new InvalidMessageException("Received message is invalid");
		}
		
		return (Message) object;
	}

	public static void main(String[] args) {
		UnicastSocketClient i=new UnicastSocketClient("localhost", 8000);
		try {
			i.createConnection();
			i.sendMessage(new TextMessage("hello"));
			
			Message m;
			try {
				m = i.getMessage();
				System.out.println(m.getType() +"\n"+ m.getContents());
			} catch (InvalidMessageException e) {
				e.printStackTrace();
			}
			i.endConnection();
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
		
	}
	
}
