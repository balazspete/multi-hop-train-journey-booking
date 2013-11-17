package node.data;

/**
 * Exception thrown when a major error occurs in a {@link DataRepository}
 * @author Balazs Pete
 *
 */
public class RepositoryException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4322288404260989789L;

	public RepositoryException() {
		super();
	}
	
	public RepositoryException(String message) {
		super(message);
	}
}
