package transaction;

public class WriteOnlyLock<T> extends Lock<T> {

	public WriteOnlyLock(T data) {
		super(data);
	}

	/**
	 * Unsupported operation
	 * @return 
	 */
	public Token readLock() {
		throw new UnsupportedOperationException();
	}
	
	
	/**
	 * Unsupported operation
	 */
	public void readUnlock() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Unsupported operation
	 */
	public T getReadable() {
		throw new UnsupportedOperationException();
	}
}
