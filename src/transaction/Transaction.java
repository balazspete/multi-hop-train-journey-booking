package transaction;

/**
 * An object representing a transaction
 * @author Balazs Pete
 *
 */
@SuppressWarnings("rawtypes")
public class Transaction {

	private VaultManager lockManager = new VaultManager();
	private TransactionContent content;
	
	/**
	 * Create a new Transaction with the specified {@link TransactionContent}
	 * @param runnable The executable content of the transaction
	 */
	public Transaction(TransactionContent runnable) {
		content = runnable;
	}

	/**
	 * Execute the transaction
	 * @throws FailedTransactionException Throws if the execution failed
	 */
	public void execute() throws FailedTransactionException {
		content.setVaultManager(lockManager);
		content.run();
	}
	
	/**
	 * Commit the transaction by applying all changes
	 */
	public void commit() {
		lockManager.commit();
		lockManager.unlock();
	}
	
	/**
	 * Abort the current transaction and discard all changes
	 */
	public void abort() {
		lockManager.abort();
		lockManager.unlock();
	}
	
	/**
	 * Get the ID of the transaction
	 * @return The ID
	 */
	public String getId() {
		return content.getId();
	}
	
	/**
	 * Determine if the current and the input transaction are the same 
	 * @param other The other transaction to compare with
	 * @return True if the transactions are identical, false otherwise
	 */
	public boolean equals(Transaction other) {
		return getId().equals(other.getId());
	}
}
