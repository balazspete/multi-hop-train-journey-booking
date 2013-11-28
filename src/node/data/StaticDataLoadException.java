package node.data;

/**
 * An exception thrown when an error occurs during static data load
 * @author Balazs Pete
 *
 */
public class StaticDataLoadException extends Exception {
	
	private static final long serialVersionUID = 5894980678111092409L;
	
	public StaticDataLoadException(String message) {
		super(message);
	}
}
