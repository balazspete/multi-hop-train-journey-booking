package transaction;

/**
 * A lock used in {@link Transaction}s in order to lock resources and modify a temporary value before committing or discarding changes
 * @author Balazs Pete
 *
 * @param <T> The class of the stored object
 */
public class Vault<T> extends Lock<T> {

	private T base = null;
	
	/**
	 * Create a new {@link Value} with its value
	 * @param data The value to store in the vault
	 */
	public Vault(T data) {
		super(null);
		base = data;
	}

	@Override
	public T getReadable() {
		if (lockedData == null) {
			lockedData = cloner.deepClone(base);
		}
		
		return super.getReadable();
	}
	
	/**
	 * Commit the changes 
	 */
	public void commit() {
		if (rwl.isWriteLockedByCurrentThread()) {
			base = cloner.deepClone(lockedData);
		}
		
		lockedData = null;
	}
	
	/**
	 * Discard the changes
	 */
	public void abort() {
		lockedData = null;
	}
}
