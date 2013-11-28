package transaction;

/**
 * A lock that only copies the main data structure and keeps the references to the underlying objects
 * @author Balazs Pete
 *
 * @param <T>
 */
public class ShallowLock<T> extends Lock<T> {

	public ShallowLock(T data) {
		super(data);
	}
	
	@Override
	public T getReadable(Token token) throws LockException {
		if (!canRead(token)) {
			throw new LockException("You do not have a read lock on the item");
		}
		
		// TODO only copy map structure, keep references to vaults
		// WARNING this is unsafe
		return lockedData;
	}
}
