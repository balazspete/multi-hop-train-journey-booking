package communication;

import communication.messages.Message;

/**
 * A generic client-to-server communication client interface
 * @author Balazs Pete
 *
 */
public interface UnicastCommunicationClient {

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
	 * Send a message to the server
	 * @param message The message to be sent
	 * @throws CommunicationException Thrown if an error occurred during transmission
	 */
	public void sendMessage(Message message) throws CommunicationException;
	
	/**
	 * Receive a message from the server (will block until message has been received)
	 * @return The message received
	 * @throws CommunicationException Thrown if an error occurred while receiving the message
	 */
	public Message getMessage() throws CommunicationException;
	
}
