package transaction;

public class ShallowLock<T> extends Lock<T> {

	public ShallowLock(T data) {
		super(data);
	}
	
	@Override
	public T getReadable(Token token) throws LockException {
		if (!canRead(token)) {
			throw new LockException("You do not have a read lock on the item");
		}
		
		return cloner.shallowClone(lockedData);
	}
}
