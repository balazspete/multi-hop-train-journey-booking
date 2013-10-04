package communication;

/**
 * An exception describing a communication error
 * @author Balazs Pete
 *
 */
public class CommunicationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1153395142101460769L;

	/**
	 * Create a new CommunicationException
	 * @param message The message to be contained by the exception
	 */
	public CommunicationException(String message) {
		super(message);
	}
	
}
