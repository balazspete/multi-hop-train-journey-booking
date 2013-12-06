package transaction;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.joda.time.DateTime;

import transaction.Lock.Token.LockType;

import com.rits.cloning.Cloner;

/**
 * An object used to lock a resource
 * @author Balazs Pete
 *
 * @param <T> The type of the locked resource
 */
public class Lock<T> {
	
	/**
	 * An object returned by a {@link Lock}, used to determine locking ownerships
	 * @author Balazs Pete
	 *
	 */
	public static class Token {
		
		/**
		 * Type of a lock (READ or WRITE)
		 * @author Balazs Pete
		 *
		 */
		public enum LockType {
			READ, WRITE
		}
		
		private String id;
		private LockType type;
		private DateTime time;
		
		/**
		 * Create a new lock with
		 * @param type The type of the lock
		 */
		public Token(LockType type) {
			id = new BigInteger(130, new SecureRandom()).toString(32);
			this.type = type;
			time = DateTime.now();
		}
		
		/**
		 * Get the type of the lock
		 * @return The {@link LockType}
		 */
		public LockType getLockType() {
			return type;
		}
		
		/**
		 * Determine if the input Token is identical to this one
		 * @param other The other token
		 * @return True if equal, false otherwise
		 */
		public boolean equals(Token other) {
			return id.equals(other.getId());
		}
		
		/**
		 * Get the ID of the token
		 * @return The ID
		 */
		protected String getId() {
			return id;
		}
		
		public boolean isOld(){
			return time.plusSeconds(60).isBefore(DateTime.now());
		}
		
		@Override
		public String toString() {
			return id;
		}
	}
	
	private boolean writeMode = false; 
	
	private volatile Set<Token> currentLocks = new HashSet<Token>();
	private volatile ConcurrentLinkedQueue<Token> pendingQueue = new ConcurrentLinkedQueue<Token>();
	
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
	 * @throws LockException 
	 */
	public T getReadable(Token token) throws LockException {
		if (!canRead(token)) {
			throw new LockException("You do not have a read lock on the item");
		}
		
		return cloner.deepClone(lockedData);
	}
	
	/**
	 * Determine whether the holder of the token can read the object
	 * @param token The token
	 * @return True if the token is currently read locking the object, false otherwise
	 */
	public boolean canRead(Token token) {
		return currentLocks.contains(token);
	}
	
	/**
	 * Get the stored resource
	 * @return The resource stored in the lock
	 * @throws LockException Thrown if the thread has not secured a write lock 
	 */
	public T getWriteable(Token token) throws LockException {
		if (!canWrite(token)) {
			throw new LockException("You do not have a write lock on the item");
		}
		
		return lockedData;
	}
	
	/**
	 * Determine whether the holder of the token has write lock
	 * @param token The token
	 * @return true If the input token is currently used to write lock
	 */
	public boolean canWrite(Token token) {
		return writeMode && currentLocks.contains(token);
	}
	
	/**
	 * Get a read lock
	 */
	public synchronized Token readLock() {
		Token t = new Token(LockType.READ);
		pendingQueue.add(t);
		
		while (writeMode || !pendingQueue.peek().equals(t)) {
			cleanTokens();
			try {
				wait();
			} catch (InterruptedException e) {
				System.err.println(e.getMessage());
				// Not that big of a problem, loop around and try again
			}
		}
		
		currentLocks.add(pendingQueue.remove());
		notifyAll();
		return t;
	}
	
	/**
	 * Discard the read lock
	 * @throws LockException 
	 */
	public boolean readUnlock(Token token) {
		return removeLock(token);
	}
	
	/**
	 * Get a write lock
	 */
	public synchronized Token writeLock() {
		Token t = new Token(LockType.WRITE);
		pendingQueue.add(t);
		
		while (currentLocks.size() > 0 || !pendingQueue.peek().equals(t)) {
			cleanTokens();
			try {
				wait();
			} catch (InterruptedException e) {
				System.err.println(e.getMessage());
				// Not that big of a problem, loop around and try again
			}
		}
		
		writeMode = true;
		currentLocks.add(pendingQueue.remove());
		
		notifyAll();
		
		return t;
	}
	
	/**
	 * Discard the write lock
	 * @return True if the lock has been removed
	 */
	public boolean writeUnlock(Token token) {
		return removeLock(token);
	}
	
	private synchronized boolean removeLock(Token t) {
		if (!currentLocks.contains(t)) {
			return false;
		}
		
		if (writeMode) {
			writeMode = false;
		}
		
		boolean result = currentLocks.remove(t);
		notifyAll();
		return result;
	}
	
	/**
	 * Determine if the lock is write locked by someone
	 * @return True if the lock is write locked, false otherwise
	 */
	public boolean isWriteLocked() {
		System.out.println(currentLocks);
		return writeMode && currentLocks.size() == 1;
	}
	
	/**
	 * Determine if the lock is read locked by someone
	 * @return True if the lock is read locked, false otherwise
	 */
	public boolean isReadLocked() {
		return !writeMode && currentLocks.size() > 0;
	}
	
	protected void cleanTokens() {
		Set<Token> toRemove = new HashSet<Token>();
		for(Token token : currentLocks) {
			if (token.isOld()) {
				toRemove.add(token);
			}
		}
		for(Token token : currentLocks) {
			currentLocks.remove(token);
			writeMode = false;
		}
	}
}
