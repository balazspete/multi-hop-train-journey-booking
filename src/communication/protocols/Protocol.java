package communication.protocols;

import communication.messages.Message;

/**
 * Skeleton of a communication protocol
 * @author Balazs Pete
 *
 */
public interface Protocol {

	/**
	 * Get the type of the accepted {@link Message}
	 * @return The class name of the {@link Message}
	 */
	public String getAcceptedMessageType();
	
	/**
	 * Handle an input {@link Message} and return an answer
	 * @param message The {@link Message} to process
	 * @return The reply
	 */
	public Message processMessage(Message message);
	
}
