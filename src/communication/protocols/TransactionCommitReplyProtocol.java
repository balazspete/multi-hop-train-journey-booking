package communication.protocols;

import transaction.TransactionCoordinator;
import transaction.TransactionCoordinatorManager;
import communication.messages.Message;
import communication.messages.TransactionExecutionReplyMessage;
import communication.messages.TransactionExecutionReplyMessage.Reply;

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
		System.out.println("Received commit reply");
		TransactionExecutionReplyMessage msg = (TransactionExecutionReplyMessage) message;
		TransactionCoordinator<KEY, VALUE> tc = manager.get(msg.getTransactionId()); 
		
		tc.logReply(msg.getSender().getLocation(), (Reply) msg.getContents());
		
		return null;
	}

	@Override
	public boolean hasReply() {
		return false;
	}

}
