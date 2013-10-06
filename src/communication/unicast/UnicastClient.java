package communication.unicast;

import communication.CommunicationException;
import communication.messages.InvalidMessageException;
import communication.messages.Message;

/**
 * A generic client-to-server communication client interface (to be paired with a {@link UnicastServer}
 * @author Balazs Pete
 *
 */
public interface UnicastClient {

	/**
	 * Establish a connection to the server
	 * @return True of the communication was established successfully
	 * @throws CommunicationException Thrown if an error occurred while establishing the connection 
	 */
	public boolean createConnection() throws CommunicationException;
	
	/**
	 * Terminate the connection to the server
	 * @return true if the connection was terminated successfully
	 * @throws CommunicationException Thrown if an error occurred while terminating the connection
	 */
	public boolean endConnection() throws CommunicationException;
	
	/**
	 * Send a {@link Message} to the {@link UnicastServer}
	 * @param message The {@link Message} to be sent
	 * @throws CommunicationException Thrown if an error occurred during transmission
	 */
	public void sendMessage(Message message) throws CommunicationException;
	
	/**
	 * Receive a {@link Message} from a {@link UnicastServer} (will block until message has been received)
	 * @return The {@link Message} received
	 * @throws CommunicationException Thrown if an error occurred while receiving the {@link Message}
	 * @throws InvalidMessageException Thrown if the received {@link Message} cannot be interpreted
	 */
	public Message getMessage() throws CommunicationException, InvalidMessageException;
	
}
