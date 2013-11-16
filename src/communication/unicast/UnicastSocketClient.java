package communication.unicast;

import java.io.*;
import java.net.*;
import java.util.Set;

import node.company.DistributedRepository;
import node.company.DistributedRepository.DataLoadException;
import communication.CommunicationException;
import communication.messages.*;
import data.system.NodeInfo;
import data.trainnetwork.BookableSection;

/**
 * A client-to-server networking client implemented using sockets
 * @author Balazs Pete
 *
 */
public class UnicastSocketClient extends Thread implements
		UnicastClient {

	public static final int MAX_TRIES = 3;
	
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
			System.out.println("UnicastSocketClient: Sent a " + message.getType() + " to " + socket.getInetAddress().toString() + " | " + message.getContents());
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
		
		Message message = (Message) object;
		NodeInfo node = new NodeInfo(null);
		node.addLocation(host);
		message.setSender(node);
		
		System.out.println("UnicastSocketServer: Received a " + message.getType() + " from " + message.getSender().getLocation() + " | " + message.getContents());
		
		return message;
	}
	
	/**
	 * Send one message to a {@link UnicastSocketServer}
	 * @param location The address of the server
	 * @param port The port the server is running on
	 * @param message The message to send
	 * @throws CommunicationException Thrown if an error occurred while sending the message
	 */
	public static Message sendOneMessage(String location, int port, Message message, boolean receiveReply) throws CommunicationException {
		UnicastSocketClient client = new UnicastSocketClient(location, DistributedRepository.PORT);
		return sendOneMessage(client, message, receiveReply);
	}
	
	/**
	 * Send one message to a {@link UnicastSocketServer}
	 * @param client The {@link UnicastSocketClient} to use
	 * @param message The message to send
	 * @throws CommunicationException Thrown if an error occurred while sending the message
	 */
	public static Message sendOneMessage(UnicastSocketClient client, Message message, boolean receiveReply) throws CommunicationException {
		int count = 0;
		while (true) {
			try {
				client.createConnection();
				break;
			} catch (CommunicationException e) {
				if (count++ > MAX_TRIES) {
					throw CommunicationException.CANNOT_OPEN_CONNECTION;
				} else {
					System.err.println(e.getMessage() + "; Retrying...");
				}
			}
		}
		
		count = 0;
		while (true) {
			try {
				client.sendMessage(message);
				break;
			} catch (CommunicationException e) {
				if (count++ > MAX_TRIES) {
					throw CommunicationException.CANNOT_SEND_MESSAGE;
				} else {
					System.err.println(e.getMessage() + "; Retrying...");
				}
			}
		}
		
		Message msg = null;
		if (receiveReply) {
			try {
				msg = client.getMessage();
			} catch (InvalidMessageException e) {
				System.err.println(e.getMessage());
			}
		}
		
		count = 0;
		while (true) {
			try {
				client.endConnection();
				break;
			} catch (CommunicationException e) {
				if (count++ > MAX_TRIES) {
					throw CommunicationException.CANNOT_CLOSE_CONNECTION;
				} else {
					System.err.println(e.getMessage() + "; Retrying...");
				}
			}
		}
		
		return msg;
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
