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
		Token t = dataVault.writeLock();
		try {
			script(t);
		} catch (LockException e) {
			throw new FailedTransactionException(e.getMessage());
		} finally {
			dataVault.writeUnlock(t);
		}
	}
	
	@Override
	public abstract void script(Token t) throws FailedTransactionException, LockException;

}
