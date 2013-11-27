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

	private Token dataVaultToken; 
	
	@Override
	public void run() throws FailedTransactionException {
		FailedTransactionException ex = null;
		
		
		dataVaultToken = dataVault.writeLock();
		try {
			script(dataVaultToken);
		} catch (LockException e) {
			ex = new FailedTransactionException(e.getMessage());
		}
		
		if (ex != null) {
			throw ex;
		}
	}
	
	@Override
	public void commit() {
		manager.commit();
		manager.unlock();
		dataVault.commit(dataVaultToken);
		dataVault.writeUnlock(dataVaultToken);
	}
	
	@Override
	public void abort() {
		manager.abort();
		manager.unlock();
		dataVault.abort(dataVaultToken);
		dataVault.writeUnlock(dataVaultToken);
	}
	
	@Override
	public abstract void script(Token t) throws FailedTransactionException, LockException;

}
