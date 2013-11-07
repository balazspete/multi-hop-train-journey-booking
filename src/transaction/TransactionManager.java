package transaction;

import java.util.HashMap;
import java.util.Map;

import data.InconsistentDataException;

/**
 * An object used to easily instruct {@link Transaction}s
 * @author Balazs Pete
 *
 */
public class TransactionManager<KEY, VALUE> {

	private volatile Map<String, Transaction> transactionsTable;
	private volatile Map<KEY, VALUE> data;
	
	/**
	 * Create a new TransactionManager
	 */
	public TransactionManager(Map<KEY, VALUE> data) {
		this.data = data;
		transactionsTable = new HashMap<String, Transaction>();
	}
	
	/**
	 * Create and execute the input {@link TransactionContent}
	 * @param content The content to execute
	 * @throws FailedTransactionException Thrown if transaction creation or execution failed
	 */
	public void execute(TransactionContent<KEY, VALUE> content) throws FailedTransactionException {
		content.setData(data);
		try {
			Transaction t = initiateTransaction(content);
			t.execute();
		} catch (InvalidTransactionException e) {
			throw new FailedTransactionException(e.getMessage());
		}
	}
	
	/**
	 * Commit the {@link Transaction} corresponding to the specified id
	 * @param transactionId The id of the transaction to commit
	 * @throws InconsistentDataException Thrown if there is no transaction with the input id
	 */
	public void commit(String transactionId) throws InconsistentDataException {
		try {
			Transaction t = getTransaction(transactionId);
			t.commit();
			transactionsTable.remove(transactionId);
		} catch (InvalidTransactionException e) {
			throw new InconsistentDataException("Transaction with id {" + transactionId + "} does not exist while it should");
		}
	}
	
	/**
	 * Abort the specified {@link Transaction}
	 * @param transactionId The id of the transaction
	 */
	public void abort(String transactionId) {
		try {
			Transaction t = getTransaction(transactionId);
			t.abort();
			transactionsTable.remove(transactionId);
		} catch (InvalidTransactionException e) {
			// Do nothing, no harm will be caused
		}
	}
	
	private Transaction initiateTransaction(TransactionContent<KEY, VALUE> content) throws InvalidTransactionException {
		if (transactionsTable.containsKey(content.getId()))
			throw new InvalidTransactionException("Transaction ID already exists");
		
		Transaction t = new Transaction(content);
		transactionsTable.put(t.getId(), t);
		
		return t;
	}
	
	private Transaction getTransaction(String id) throws InvalidTransactionException {
		Transaction t = transactionsTable.get(id);
		if(t == null || !t.getId().equals(id)) {
			throw new InvalidTransactionException("Invalid transaction ID");
		}
		
		return null;
	}
}
