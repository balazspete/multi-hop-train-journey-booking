package transaction;

public class FailedTransactionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 432626507979971564L;

	public FailedTransactionException() {
		super();
	}
	
	public FailedTransactionException(String message) {
		super(message);
	}
}
