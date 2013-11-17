package data.trainnetwork;

/**
 * An exception thrown when trying to access {@link Section} functionalities requiring free {@link Seat}s, however no more are available
 * @author Balazs Pete
 *
 */
public class SectionFullException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8266743542579692956L;

	/**
	 * Create a new instance of the exception
	 */
	public SectionFullException() {
		super();
	}
	
	/**
	 * Create a new instance with a specific message
	 * @param message The message of the exception
	 */
	public SectionFullException(String message) {
		super(message);
	}
	
}
