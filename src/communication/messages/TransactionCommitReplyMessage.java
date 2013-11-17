package communication.messages;

import java.io.Serializable;

/**
 * A message used to tell origin confirming final action on a {@link Transaction}
 * @author Balazs Pete
 *
 */
public class TransactionCommitReplyMessage extends TransactionMessage {

	/**
	 * Represents the final status of a {@link Transaction}
	 * @author Balazs Pete
	 *
	 */
	public enum Status implements Serializable {
		COMMITTED, ABORTED
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7607849192706448985L;

	private Status status;
	
	public TransactionCommitReplyMessage(String transactionId) {
		super(transactionId);
	}
	
	@Override
	public String getType() {
		return "TransactionCommitReplyMessage";
	}

	@Override
	public Serializable getContents() {
		return status;
	}

	@Override
	public void setContents(Serializable content) {
		status = (Status) content;
	}

	/**
	 * Get a committed message
	 * @param transactionId The id of the corresponding message
	 * @return The message
	 */
	public static TransactionCommitReplyMessage committedMessage(String transactionId) {
		TransactionCommitReplyMessage msg = new TransactionCommitReplyMessage(transactionId);
		msg.setContents(Status.COMMITTED);
		return msg;
	}
	
	/**
	 * Get an aborted message
	 * @param transactionId The id of the corresponding message
	 * @return The message
	 */
	public static TransactionCommitReplyMessage abortedMessage(String transactionId) {
		TransactionCommitReplyMessage msg = new TransactionCommitReplyMessage(transactionId);
		msg.setContents(Status.ABORTED);
		return msg;
	}
}
