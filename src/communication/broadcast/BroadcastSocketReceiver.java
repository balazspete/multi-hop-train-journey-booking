package communication.broadcast;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;

import communication.CommunicationException;
import communication.messages.*;
import communication.protocols.MessagePrinterProtocol;

public class BroadcastSocketReceiver extends BroadcastReceiver {

	private Socket socket = null;
	
	private String host;
	private int port;
	
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;
	
	public BroadcastSocketReceiver(String host, int port) {
		this.host = host.intern();
		this.port = port;
	}
	
	@Override
	public boolean createConnection() throws CommunicationException {
		try {
			socket = new Socket(host, port);
		} catch (UnknownHostException e) {
			throw CommunicationException.UNREACHABLE_HOST;
		} catch (IOException e) {
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
	public  boolean endConnection() throws CommunicationException {
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
	protected Message getBroadcastMessage() throws CommunicationException, InvalidMessageException {
		Object object = null;
		try {
			// try to get message and check if input stream has ended
			if ((object = in.readObject()) == null) {
				throw CommunicationException.CANNOT_READ_MESSAGE;
			}
		} catch (CommunicationException e) {
			throw e;
		} catch (EOFException e) {
			throw CommunicationException.CONNECTION_TERMINATED;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			throw new InvalidMessageException("");
		}
		
		return (Message) object;
	}
	
	public static void main(String[] args) {
		BroadcastReceiver r = new BroadcastSocketReceiver("localhost", 8000);
		r.putProtocol(new MessagePrinterProtocol());
		r.start();
	}

}
