package transaction;

/**
 * An object representing a transaction
 * @author Balazs Pete
 *
 */
@SuppressWarnings("rawtypes")
public class Transaction {

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
		content.run();
	}
	
	/**
	 * Commit the transaction by applying all changes
	 */
	public void commit() {
		content.commit();
		System.out.println("Transaction COMMITTED - ID: " + getId());
	}
	
	/**
	 * Abort the current transaction and discard all changes
	 */
	public void abort() {
		content.abort();
		System.out.println("Transaction ABORTED - ID: " + getId());
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
	
	/**
	 * Get the returned data by the transaction
	 * @return The data returned by the transaction
	 */
	public Object getReturnedContent() {
		return content.getReturnedData();
	}
}
