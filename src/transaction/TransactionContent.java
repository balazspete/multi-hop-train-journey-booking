package transaction;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Map;

import transaction.Lock.Token;

/**
 * An object containing logic to execute within a transaction
 * @author Balazs Pete
 *
 */
public abstract class TransactionContent<KEY, VALUE, RETURN> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4571149016161551365L;
	
	private String id = new BigInteger(130, new SecureRandom()).toString(32).intern();
	protected volatile VaultManager manager;
	protected volatile ShallowLock<Map<KEY, VALUE>> dataLock;
	protected Token dataVaultToken; 
	
	protected volatile RETURN dataToReturn = null;
	
	/**
	 * Contains the logic to execute by a transaction
	 * @throws FailedTransactionException Thrown if execution failed
	 */
	public void run() throws FailedTransactionException {
		manager = new VaultManager();
		
		dataVaultToken = dataLock.readLock();
		try {
			script(dataVaultToken);
		} catch (LockException e) {
			throw new FailedTransactionException(e.getMessage());
		}
	}

	public abstract void script(Token t) throws FailedTransactionException, LockException;
	
	/**
	 * Get the ID of the corresponding transaction
	 * @return The ID
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Set the map to work with
	 * @param data The data map
	 */
	public void setData(ShallowLock<Map<KEY, VALUE>> data) {
		this.dataLock = data;
	}
	
	public RETURN getReturnedData() {
		return dataToReturn;
	}
	
	public void commit() {
		manager.commit();
		manager.unlock();
		dataLock.readUnlock(dataVaultToken);
	}
	
	public void abort() {
		manager.abort();
		manager.unlock();
		dataLock.readUnlock(dataVaultToken);
	}
	
	/**
	 * Determine whether this TransactionContent is the same as the input
	 * @param content The other object
	 * @return True if the two objects are the same
	 */
	public boolean equals(TransactionContent<KEY, VALUE, RETURN> content) {
		return id.equals(content.getId());
	}
}
