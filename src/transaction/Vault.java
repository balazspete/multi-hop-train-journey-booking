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
	public T getReadable(Token token) throws LockException {
		if (!canRead(token)) {
			throw new LockException("You do not have a read lock on the item");
		}
		
		return cloner.deepClone(base);
	}
	
	@Override
	public T getWriteable(Token token) throws LockException {
		if (lockedData == null) {
			lockedData = cloner.deepClone(base);
		}
		
		return super.getWriteable(token);
	}
	
	/**
	 * Commit the changes 
	 * @param token The lock token
	 */
	public synchronized void commit(Token token) {
		if (canWrite(token)) {
			base = cloner.deepClone(lockedData);
			lockedData = null;
		}
	}
	
	/**
	 * Discard the changes
	 * @param token The lock token
	 */
	public synchronized void abort(Token token) {
		if (canWrite(token)) {
			lockedData = null;
		}
	}
	
	@Override
	public String toString() {
		return "vault["+base.toString()+"]";
	}
}
