package transaction;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Map;

/**
 * An object containing logic to execute within a transaction
 * @author Balazs Pete
 *
 */
public abstract class TransactionContent<KEY, VALUE> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4571149016161551365L;
	
	private String id;
	protected Map<KEY, VALUE> data;
	
	/**
	 * Create a new TransactionContent
	 */
	public TransactionContent() {
		id = new BigInteger(130, new SecureRandom()).toString(32).intern();
	}
	
	/**
	 * Contains the logic to execute by a transaction
	 * @throws FailedTransactionException Thrown if execution failed
	 */
	public abstract void run() throws FailedTransactionException;

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
	public void setData(Map<KEY, VALUE> data) {
		this.data = data;
	}
	
	/**
	 * Determine whether this TransactionContent is the same as the input
	 * @param content The other object
	 * @return True if the two objects are the same
	 */
	public boolean equals(TransactionContent<KEY, VALUE> content) {
		return id.equals(content.getId());
	}
}
