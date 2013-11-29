package node.client;

/**
 * Exception thrown when an error occurring in the booking process cannot be handled
 * @author Balazs Pete
 *
 */
public class BookingException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1376663148018629479L;

	public BookingException(String message) {
		super(message);
	}
}
