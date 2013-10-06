package communication.broadcast;

import java.io.*;
import java.net.*;

import communication.messages.Message;

/**
 * A sockets based implementation of {@link BroadcastReceiverHandler}
 * @author Balazs
 *
 */
public class BroadcastSocketReceiverHandler extends BroadcastReceiverHandler {

	ObjectOutputStream out;
	
	/**
	 * Create a new instance of {@link BroadcastSocketReceiverHandler}
	 * @param socket The {@link Socket} associated with the receiver
	 * @throws IOException Exception thrown in case connection could not be established
	 */
	public BroadcastSocketReceiverHandler(Socket socket) throws IOException {
		this.out = new ObjectOutputStream(socket.getOutputStream());
	}
	
	@Override
	protected boolean sendMessage(Message message) {
		try {
			out.writeObject(message);
			out.flush();
		} catch (SocketException e) {
			endBroadcasts();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
}
