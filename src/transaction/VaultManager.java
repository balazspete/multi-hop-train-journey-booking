package transaction;

import java.util.*;

/**
 * An object to manage multiple vault in bulk
 * @author Balazs Pete
 *
 */
@SuppressWarnings("rawtypes")
public class VaultManager {

	public enum LockType {
		READ, WRITE
	}
	
	private Map<Vault, LockType> locks = new HashMap<Vault, LockType>();
	
	/**
	 * Request a write lock on the object contained in the input vault
	 * @param lock The vault to add to the manager
	 * @return The value on which the lock was requested
	 */
	public Object writeLock(Vault lock) {
		Object result = null;
		
		lock.writeLock();
		locks.put(lock, LockType.WRITE);
		
		try {
			result = lock.getWriteable();
		} catch (LockException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * Request a read lock on the object contained in the input vault
	 * @param lock The vault to add to the manager
	 * @return The value on which the lock was requested
	 */
	public Object readLock(Vault lock) {
		lock.writeLock();
		locks.put(lock, LockType.READ);
		
		return lock.getReadable();
	}
	
	/**
	 * Unlock all locks
	 */
	public void unlock() {
		for (Vault lock : locks.keySet()) {
			if (locks.get(lock) == LockType.WRITE) {
				lock.writeUnlock();
			} else {
				lock.readUnlock();
			}
			locks.remove(lock);
		}
	}
	
	/**
	 * Commit all locks
	 */
	public void commit() {
		for (Vault lock : locks.keySet()) {
			lock.commit();
		}
	}
	
	/**
	 * Discard changes in all locks
	 */
	public void abort() {
		for (Vault lock : locks.keySet()) {
			lock.abort();
		}
	}
}
