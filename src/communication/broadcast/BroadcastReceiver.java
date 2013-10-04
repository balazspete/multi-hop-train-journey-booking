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
	 * Handle the input broadcast message
	 * @param message The broadcast message
	 */
	protected abstract void handleBroadcastMessage(Message message);
	
	private void listenToBroadcasts() {
		while (listening)
			return;//getBroadcastMessage();
	}
	
	@Override
	public void run() {
		createConnection();
		listenToBroadcasts();
		endConnection();
	}
}
