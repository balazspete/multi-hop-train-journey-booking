package communication.protocols;

import transaction.TransactionCoordinator;
import transaction.TransactionCoordinatorManager;
import communication.messages.Message;
import communication.messages.TransactionCommitReplyMessage;

public class TransactionCommitReplyProtocol<KEY, VALUE> implements Protocol {

	private TransactionCoordinatorManager<KEY, VALUE> manager;

	public TransactionCommitReplyProtocol(TransactionCoordinatorManager<KEY, VALUE> manager) {
		this.manager = manager;
	}
	
	@Override
	public String getAcceptedMessageType() {
		return "TransactionCommitReplyMessage";
	}

	@Override
	public Message processMessage(Message message) {
		TransactionCommitReplyMessage msg = (TransactionCommitReplyMessage) message;
		TransactionCoordinator<KEY, VALUE> tc = manager.get(msg.getTransactionId()); 
		
		tc.logReply(msg.getSender().getLocation(), null);
		
		return null;
	}

	@Override
	public boolean hasReply() {
		return false;
	}
}
