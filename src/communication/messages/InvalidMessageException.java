package communication.messages;

/**
 * An exception to be thrown when a protocol does not support the input 
 * @author Balazs Pete
 *
 */
public class InvalidMessageException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4750296226455787348L;

	/**
	 * Create a new InvalidMessageException
	 * @param message The message describing the exception
	 */
	public InvalidMessageException(String message) {
		super(message);
	}
}
