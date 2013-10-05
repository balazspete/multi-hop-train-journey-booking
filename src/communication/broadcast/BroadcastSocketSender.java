package communication.broadcast;

import java.io.*;
import java.net.*;

import communication.CommunicationException;
import communication.messages.TextMessage;

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
            	BroadcastSocketClientHandler h = new BroadcastSocketClientHandler(socket.accept());
            	addConnectionHandler(h);
            	h.start();
            }
            
			socket.close();
        } catch (Exception e) {
        	e.printStackTrace();
        	throw CommunicationException.CANNOT_USE_PORT;
        }
	}
	
	public static void main(String[] args) {
		BroadcastSocketSender s = new BroadcastSocketSender(8000);
		s.start();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		String input;
		try {
			while((input = br.readLine()) != null) {
				s.broadcastMessage(new TextMessage(input));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
