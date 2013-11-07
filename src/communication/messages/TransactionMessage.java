package communication.messages;

import java.io.Serializable;

/**
 * A generic message used for {@link Transaction} control
 * @author Balazs Pete
 *
 */
public abstract class TransactionMessage extends Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4296254777937095508L;

	private String transactionId;
	
	public TransactionMessage(String transactionId) {
		this.transactionId = transactionId;
	}
	
	/**
	 * Get the ID of the corresponding {@link Transaction}
	 * @return
	 */
	public String getTransactionId() {
		return transactionId;
	}
	
	@Override
	public abstract String getType();

	@Override
	public abstract Serializable getContents();

	@Override
	public abstract void setContents(Serializable content);
}
