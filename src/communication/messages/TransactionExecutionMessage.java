package communication.messages;

import java.io.Serializable;

import transaction.TransactionContent;

/**
 * A {@link Message} used to transfer a {@link TransactionContent} and initiate a {@link Transaction} on the remote
 * @author Balazs Pete
 *
 */
public class TransactionExecutionMessage<KEY, VALUE> extends TransactionMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7804040847207632803L;
	
	private TransactionContent<KEY, VALUE> content;
	
	public TransactionExecutionMessage(String transactionId) {
		super(transactionId);
	}
	
	@Override
	public String getType() {
		return "TransactionContentMessage";
	}

	@Override
	public Serializable getContents() {
		return content;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setContents(Serializable content) {
		this.content = (TransactionContent<KEY, VALUE>) content;
	}
}
