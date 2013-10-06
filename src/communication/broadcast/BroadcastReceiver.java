package communication.broadcast;

import communication.CommunicationException;
import communication.messages.*;
import communication.protocols.ProtocolControlledMessenger;

/**
 * A generic description of a broadcast receiver node
 * @author Balazs Pete
 *
 */
public abstract class BroadcastReceiver extends ProtocolControlledMessenger {

	private boolean listening = true;
	
	/**
	 * Establish a connection to the {@link BroadcastSender}
	 * @return True if the communication was established successfully
	 * @throws CommunicationException Thrown in case an error occurred while creating the connection
	 */
	protected abstract boolean createConnection() throws CommunicationException;
	
	/**
	 * Terminate the connection to the {@link BroadcastServer}
	 * @return true if the connection was terminated successfully
	 * @throws CommunicationException Thrown in case the connection could not be closed properly
	 */
	protected abstract boolean endConnection() throws CommunicationException;
	
	/**
	 * Listen to and retrieve a broadcasted message
	 * @return The received {@link Message}
	 * @throws ComunicationException Thrown in case an error occurred while receiving the {@link Message}
	 * @throws InvalidMessageException Thrown if the received {@link Message} could not be processed
	 */
	protected abstract Message getBroadcastMessage() throws CommunicationException, InvalidMessageException;
	
	/**
	 * Stop listening to broadcasts
	 */
	protected void stopListening() {
		listening = false;
	}
	
	private void listenToBroadcasts() throws CommunicationException {
		while (listening) {
			Message msg;
			try {
				msg = getBroadcastMessage();
				processMessage(msg);
			} catch (InvalidMessageException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void run() {
		try {
			createConnection();
		} catch (CommunicationException e) {
			e.printStackTrace();
			return;
		}
		
		try {
			listenToBroadcasts();
		} catch (CommunicationException e) {
			e.printStackTrace();
			return;
		}
		
		try {
			endConnection();
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
	}
}
