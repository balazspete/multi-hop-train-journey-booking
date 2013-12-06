package communication.protocols;

import transaction.TransactionCoordinator;
import transaction.TransactionCoordinatorManager;
import communication.messages.Message;
import communication.messages.TransactionTerminationReplyMessage;

/**
 * A protocol handling the final transaction message from a remote node
 * @author Balazs Pete
 *
 * @param <KEY>
 * @param <VALUE>
 * @param <RETURN>
 */
public class TransactionTerminationReplyProtocol<KEY, VALUE, RETURN> implements Protocol {

	private TransactionCoordinatorManager<KEY, VALUE, RETURN> manager;

	public TransactionTerminationReplyProtocol(TransactionCoordinatorManager<KEY, VALUE, RETURN> manager) {
		this.manager = manager;
	}
	
	@Override
	public String getAcceptedMessageType() {
		return "TransactionCommitReplyMessage";
	}

	@Override
	public Message processMessage(Message message) {
		TransactionTerminationReplyMessage msg = (TransactionTerminationReplyMessage) message;
		TransactionCoordinator<KEY, VALUE, RETURN> tc = manager.get(msg.getTransactionId()); 
		
		tc.logReply(msg.getSender().getLocation(), null);
		
		return null;
	}

	@Override
	public boolean hasReply() {
		return false;
	}
}
