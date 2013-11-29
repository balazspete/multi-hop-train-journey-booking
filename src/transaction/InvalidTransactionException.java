package transaction;

/**
 * An exception thrown when a transaction is invalid for some reason
 * @author Balazs Pete
 *
 */
public class InvalidTransactionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7009216477791861740L;

	public InvalidTransactionException(String message) {
		super(message);
	}
	
}
