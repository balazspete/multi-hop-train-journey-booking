package communication.broadcast;

import communication.messages.*;
import communication.protocols.ProtocolControlledMessenger;

public abstract class BroadcastReceiver extends ProtocolControlledMessenger {

	private boolean listening = true;
	
	/**
	 * Establish a connection to the server
	 * @return True of the communication was established successfully
	 */
	protected abstract boolean createConnection();
	
	/**
	 * Terminate the connection to the server
	 * @return true if the connection was terminated successfully
	 */
	protected abstract boolean endConnection();
	
	/**
	 * Listen to and retrieve a broadcasted message
	 * @return The received Message
	 */
	protected abstract Message getBroadcastMessage();
	
	/**
	 * Stop listening to broadcasts
	 */
	protected void stopListening() {
		listening = false;
	}
	
	private void listenToBroadcasts() {
		while (listening) {
			Message msg = getBroadcastMessage();
			try {
				processMessage(msg);
			} catch (InvalidMessageException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void run() {
		createConnection();
		listenToBroadcasts();
		endConnection();
	}
}
