package transaction;

import transaction.Lock.Token;

/**
 * A {@link TransactionContent} that gives write permissions on the dataVault
 * @author Balazs Pete
 *
 * @param <KEY>
 * @param <VALUE>
 */
public abstract class SudoTransactionContent<KEY, VALUE, RETURN> extends TransactionContent<KEY, VALUE, RETURN> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3125515031068969990L;
	
	@Override
	public void run() throws FailedTransactionException {
		manager = new VaultManager();
		
		dataVaultToken = dataLock.writeLock();
		try {
			script(dataVaultToken);
		} catch (LockException e) {
			throw new FailedTransactionException(e.getMessage());
		}
	}
	
	@Override
	public void commit() {
		manager.commit();
		manager.unlock();
		dataLock.writeUnlock(dataVaultToken);
	}
	
	@Override
	public void abort() {
		manager.abort();
		manager.unlock();
		dataLock.writeUnlock(dataVaultToken);
	}
	
	@Override
	public abstract void script(Token t) throws FailedTransactionException, LockException;

}
