package communication.broadcast;

import java.net.*;

import communication.CommunicationException;

public class BroadcastSocketSender extends BroadcastSender {

	private int port;
	
	private ServerSocket socket = null;
	
	public BroadcastSocketSender(int port) {
		this.port = port;
	}
	
	@Override
	public void acceptConnections() throws CommunicationException {
        boolean listening = true;

        try {
            socket = new ServerSocket(port);
            
            while (listening) {
            	
            }
            
			socket.close();
        } catch (Exception e) {
        	throw CommunicationException.CANNOT_USE_PORT;
        }
	}
}
