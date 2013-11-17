package communication.messages;

import java.io.Serializable;

/**
 * A message used to rely the result of the execution stage of a transaction to the origin node
 * @author Balazs Pete
 *
 */
public class TransactionExecutionReplyMessage extends TransactionMessage {

	/**
	 * An data type describing the status of a transaction post execution
	 * It can be either `ready to commit` (execution succeeded) or `failed` (execution failed)
	 * @author Balazs Pete
	 *
	 */
	public enum Reply implements Serializable {
		READY_TO_COMMIT, FAILED
	}
	
	private Reply reply;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6604653713509310206L;
	
	/**
	 * Create a new {@link TransactionExecutionReplyMessage}, specifying the ID of the corresponding {@link Transaction}
	 * @param transactionId The ID of a Transaction
	 */
	public TransactionExecutionReplyMessage(String transactionId) {
		super(transactionId);
	}
	
	@Override
	public String getType() {
		return "TransactionExecutionReplyMessage";
	}

	@Override
	public Serializable getContents() {
		return reply;
	}

	@Override
	public void setContents(Serializable content) {
		reply = (Reply) content;
	}
	
	/**
	 * Get a `ready to commit` (success) message
	 * @param id The id of the corresponding {@link Transaction}
	 * @return The message
	 */
	public static TransactionExecutionReplyMessage readyToCommitMessage(String id) {
		TransactionExecutionReplyMessage msg = new TransactionExecutionReplyMessage(id);
		msg.setContents(Reply.READY_TO_COMMIT);
		return msg;
	}

	/**
	 * Get a `failed` message
	 * @param id The id of the corresponding {@link Transaction}
	 * @return The message
	 */
	public static TransactionExecutionReplyMessage failedMessage(String id) {
		TransactionExecutionReplyMessage msg = new TransactionExecutionReplyMessage(id);
		msg.setContents(Reply.FAILED);
		return msg;
	}
}
