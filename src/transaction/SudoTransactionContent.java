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
		FailedTransactionException ex = null;
		
		Token t = dataVault.writeLock();
		try {
			script(t);
			dataVault.commit(t);
		} catch (LockException e) {
			dataVault.abort(t);
			ex = new FailedTransactionException(e.getMessage());
		} finally {
			dataVault.writeUnlock(t);
		}
		
		if (ex != null) {
			throw ex;
		}
	}
	
	@Override
	public abstract void script(Token t) throws FailedTransactionException, LockException;

}
