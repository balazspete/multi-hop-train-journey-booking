package transaction;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.rits.cloning.Cloner;

/**
 * An object used to lock a resource
 * @author Balazs Pete
 *
 * @param <T> The type of the locked resource
 */
public class Lock<T> {
	ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	
	protected T lockedData;
	protected Cloner cloner = new Cloner();
	
	/**
	 * Create a new lock with its stored data
	 * @param data The resource to be made lockable
	 */
	public Lock(T data) {
		lockedData = cloner.deepClone(data);
	}
	
	/**
	 * Get a copy of the stored resource
	 * @return A deep copy of the resource within the lock
	 */
	public T getReadable() {
		return cloner.deepClone(lockedData);
	}
	
	/**
	 * Get the stored resource
	 * @return The resource stored in the lock
	 * @throws LockException Thrown if the thread has not secured a write lock 
	 */
	public T getWriteable() throws LockException {
		if (rwl.isWriteLockedByCurrentThread()) {
			return lockedData;
		}
		
		throw new LockException("You do not have a write lock on the item");
	}
	
	/**
	 * Get a read lock
	 */
	public void readLock() {
		rwl.readLock().lock();
	}
	
	/**
	 * Discard the read lock
	 */
	public void readUnlock() {
		rwl.readLock().unlock();
	}
	
	/**
	 * Get a write lock
	 */
	public void writeLock(){
		rwl.writeLock().lock();
	}
	
	/**
	 * Discard the write lock
	 */
	public void writeUnlock() {
		rwl.writeLock().unlock();
	}
}
