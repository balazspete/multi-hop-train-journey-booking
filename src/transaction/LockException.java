package transaction;

/**
 * An exception thrown when an error occurs in a lock
 * @author Balazs Pete
 *
 */
public class LockException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1644213440133595230L;

	public LockException(String message) {
		super(message);
	}
	
}
