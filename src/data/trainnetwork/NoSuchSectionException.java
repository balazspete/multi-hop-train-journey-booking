package data.trainnetwork;

/**
 * Exception thrown when trying to access a {@link Seat} in a {@link BookableSection} that does not exist
 * @author Balazs Pete
 *
 */
public class NoSuchSectionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2369240250732675474L;

	public NoSuchSectionException(String message) {
		super(message);
	}
	
}
