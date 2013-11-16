package transaction;

import java.util.*;

import transaction.Lock.Token;
import transaction.Lock.Token.LockType;

/**
 * An object to manage multiple vault in bulk
 * @author Balazs Pete
 *
 */
@SuppressWarnings("rawtypes")
public class VaultManager {

	private Map<Vault, Token> locks = new HashMap<Vault, Token>();
	
	/**
	 * Request a write lock on the object contained in the input vault
	 * @param lock The vault to add to the manager
	 * @return The value on which the lock was requested
	 */
	public Object writeLock(Vault lock) {
		Object result = null;
		
		Token token = lock.writeLock();
		locks.put(lock, token);
		
		try {
			result = lock.getWriteable(token);
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
		Object result = null;
		
		Token token = lock.readLock();
		locks.put(lock, token);
		
		try {
			result = lock.getReadable(token);
		} catch (LockException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * Unlock all locks
	 */
	public void unlock() {
		for (Vault lock : locks.keySet()) {
			Token token = locks.get(lock);
			if (token.getLockType() == LockType.WRITE) {
				lock.writeUnlock(token);
			} else {
				lock.readUnlock(token);
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
	
	/**
	 * Return the token corresponding to the input vault
	 * @param vault The vault which's token is requested
	 * @return The token of the input vault
	 */
	public Token getToken(Vault vault) {
		return locks.get(vault);
	}
}
