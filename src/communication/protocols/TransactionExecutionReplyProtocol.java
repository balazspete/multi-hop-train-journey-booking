package communication.protocols;

import transaction.TransactionCoordinator;
import transaction.TransactionCoordinatorManager;
import communication.messages.Message;
import communication.messages.TransactionExecutionReplyMessage;
import communication.messages.TransactionExecutionReplyMessage.Reply;

/**
 * A protocol handling messages detailing the result of a transaction on the remote node
 * @author Balazs Pete
 *
 * @param <KEY>
 * @param <VALUE>
 * @param <RETURN>
 */
public class TransactionExecutionReplyProtocol<KEY, VALUE, RETURN> implements Protocol {
	
	private TransactionCoordinatorManager<KEY, VALUE, RETURN> manager;

	public TransactionExecutionReplyProtocol(TransactionCoordinatorManager<KEY, VALUE, RETURN> manager) {
		this.manager = manager;
	}
	
	@Override
	public String getAcceptedMessageType() {
		return "TransactionExecutionReplyMessage";
	}

	@Override
	public Message processMessage(Message message) {
		TransactionExecutionReplyMessage msg = (TransactionExecutionReplyMessage) message;
		TransactionCoordinator<KEY, VALUE, RETURN> tc = manager.get(msg.getTransactionId()); 
		
		tc.logReply(msg.getSender().getLocation(), (Reply) msg.getContents());
		
		return null;
	}

	@Override
	public boolean hasReply() {
		return false;
	}

}
