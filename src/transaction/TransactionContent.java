package transaction;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;

public abstract class TransactionContent implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4571149016161551365L;
	
	private String id, coordinator;
	
	public TransactionContent(String coordinatorAddress) {
		id = new BigInteger(130, new SecureRandom()).toString(32).intern();
		coordinator = coordinatorAddress;
	}
	
	public abstract void run() throws FailedTransactionException;

	public String getId() {
		return id;
	}
	
	public String getCoordinatorAddress() {
		return coordinator;
	}
	
	public boolean equals(TransactionContent content) {
		return id.equals(content.getId());
	}
}
