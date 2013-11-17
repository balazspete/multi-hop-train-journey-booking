package communication.messages;

import java.io.Serializable;

public class TransactionCommitMessage extends TransactionMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2143310657255500308L;
	
	public enum CommitAction implements Serializable {
		COMMIT, ABORT
	}
	
	private CommitAction action;
	
	public TransactionCommitMessage(String transactionId) {
		super(transactionId);
	}
	
	@Override
	public String getType() {
		return "TransactionCommitMessage";
	}

	@Override
	public Serializable getContents() {
		return action;
	}

	@Override
	public void setContents(Serializable content) {
		action = (CommitAction) content;
	}
}
