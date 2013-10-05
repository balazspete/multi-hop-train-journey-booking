package communication.broadcast;

import java.io.*;
import java.net.*;

import communication.messages.Message;

public class BroadcastSocketClientHandler extends BroadcastClientHandler {

	ObjectOutputStream out;
	
	public BroadcastSocketClientHandler(Socket socket) throws IOException {
		this.out = new ObjectOutputStream(socket.getOutputStream());
	}
	
	@Override
	protected boolean sendMessage(Message message) {
		try {
			out.writeObject(message);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
}
