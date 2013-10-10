package data;

/**
 * Exception thrown when a required argument is not defined
 * @author Balazs Pete
 *
 */
public class MissingParameterException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8012154776652920764L;

	/**
	 * Create a new instance of {@link MissingParameterException} with a specified message
	 * @param message The message of the exception
	 */
	public MissingParameterException(String message) {
		super(message);
	}
	
}
