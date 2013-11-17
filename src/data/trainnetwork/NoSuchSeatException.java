package data.trainnetwork;

/**
 * An exception thrown when trying to access a non-existing Seat
 * @author Balazs Pete
 *
 */
public class NoSuchSeatException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6915548273517756283L;

	/**
	 * Create a new instance of the exception with a specific message
	 * @param message The message of the exception
	 */
	public NoSuchSeatException(String message) {
		super(message);
	}
	
}
