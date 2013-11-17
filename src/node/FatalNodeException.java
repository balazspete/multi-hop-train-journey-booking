package node;

/**
 * Thrown in case a node encounters a condition that it cannot recover from
 * @author Balazs Pete
 *
 */
public class FatalNodeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -677870089470777098L;

	public FatalNodeException() {
		this(null);
	}
	
	public FatalNodeException(String message) {
		super(message);
	}
}
