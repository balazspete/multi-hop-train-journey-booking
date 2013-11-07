package communication.protocols;

import transaction.FailedTransactionException;
import transaction.TransactionContent;
import transaction.TransactionManager;
import communication.messages.Message;
import communication.messages.TransactionExecutionReplyMessage;

/**
 * A protocol to handle incoming {@link Transaction} requests and execute them
 * @author Balazs Pete
 *
 */
public class TransactionExecutionProtocol<KEY, VALUE> implements Protocol {

	private TransactionManager<KEY, VALUE> manager;
	
	public TransactionExecutionProtocol(TransactionManager<KEY, VALUE> manager) {
		this.manager = manager;
	}
	
	@Override
	public String getAcceptedMessageType() {
		return "TransactionContentMessage";
	}

	@SuppressWarnings("unchecked")
	@Override
	public Message processMessage(Message message) {
		TransactionContent<KEY, VALUE> content = 
				(TransactionContent<KEY, VALUE>) message.getContents();
		try {
			manager.execute(content);
			return TransactionExecutionReplyMessage.readyToCommitMessage(content.getId());
		} catch (FailedTransactionException e) {
			return TransactionExecutionReplyMessage.failedMessage(content.getId());
		}
	}
}
