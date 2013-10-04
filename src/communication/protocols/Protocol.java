package communication.protocols;

import communication.messages.Message;

/**
 * Skeleton of a communication protocol
 * @author Balazs Pete
 *
 */
public interface Protocol {

	/**
	 * Get the type of the accepted message
	 * @return The class name of the message
	 */
	public String getAcceptedMessageType();
	
	/**
	 * Handle an input message and return an answer
	 * @param message The message to process
	 * @return The reply
	 */
	public Message processMessage(Object message);
	
}
