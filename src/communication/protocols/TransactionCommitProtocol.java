package communication.protocols;

import transaction.TransactionManager;
import communication.messages.Message;
import communication.messages.TransactionCommitMessage;
import communication.messages.TransactionCommitMessage.CommitAction;
import communication.messages.TransactionCommitReplyMessage;
import data.InconsistentDataException;

/**
 * A protocol used to instruct a node to either commit or abort a {@link Transaction}
 * @author Balazs Pete
 *
 */
public class TransactionCommitProtocol<KEY, VALUE> implements Protocol {

	private TransactionManager<KEY, VALUE> manager;
	
	public TransactionCommitProtocol(TransactionManager<KEY, VALUE> manager) {
		this.manager = manager;
	}
	
	@Override
	public String getAcceptedMessageType() {
		return "TransactionCommitMessage";
	}

	@Override
	public Message processMessage(Message message) {
		TransactionCommitMessage msg = (TransactionCommitMessage) message;
		CommitAction action = (CommitAction) msg.getContents();
		
		if (action == CommitAction.COMMIT) {
			try {
				manager.commit(msg.getTransactionId());
			} catch (InconsistentDataException e) {
				// TODO Re-Initialize node
				e.printStackTrace();
			}
			return TransactionCommitReplyMessage.committedMessage(msg.getTransactionId());
		} else {
			manager.abort(msg.getTransactionId());
			return TransactionCommitReplyMessage.abortedMessage(msg.getTransactionId());
		}
	}

}
