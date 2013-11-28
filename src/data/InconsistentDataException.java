package data;

/**
 * An exception thrown when a data error cannot be handled
 * @author Balazs Pete
 *
 */
public class InconsistentDataException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3819549999386879849L;

	public InconsistentDataException(String message) {
		super(message);
	}
	
}
