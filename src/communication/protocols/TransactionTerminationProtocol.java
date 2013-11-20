package communication.protocols;

import transaction.Lock.Token;
import transaction.LockException;
import transaction.TransactionManager;
import transaction.WriteOnlyLock;
import communication.CommunicationException;
import communication.messages.Message;
import communication.messages.TransactionTerminationMessage;
import communication.messages.TransactionTerminationMessage.CommitAction;
import communication.messages.TransactionTerminationReplyMessage;
import communication.unicast.UnicastSocketClient;
import data.InconsistentDataException;

/**
 * A protocol used to instruct a node to either commit or abort a {@link Transaction}
 * @author Balazs Pete
 *
 */
public class TransactionTerminationProtocol<KEY, VALUE, RETURN> implements Protocol {

	private TransactionManager<KEY, VALUE, RETURN> manager;
	private WriteOnlyLock<Integer> monitor;
	
	public TransactionTerminationProtocol(TransactionManager<KEY, VALUE, RETURN> manager, WriteOnlyLock<Integer> monitor) {
		this.manager = manager;
		this.monitor = monitor;
	}
	
	@Override
	public String getAcceptedMessageType() {
		return "TransactionCommitMessage";
	}

	@Override
	public Message processMessage(Message message) {
		TransactionTerminationMessage msg = (TransactionTerminationMessage) message;
		CommitAction action = (CommitAction) msg.getContents();
		
		TransactionTerminationReplyMessage reply = null;
		if (action == CommitAction.COMMIT) {
			try {
				manager.commit(msg.getTransactionId());
			} catch (InconsistentDataException e) {
				// TODO Re-Initialize node
				e.printStackTrace();
				return null;
			}
			reply = TransactionTerminationReplyMessage.committedMessage(msg.getTransactionId());
		} else {
			manager.abort(msg.getTransactionId());
			reply = TransactionTerminationReplyMessage.abortedMessage(msg.getTransactionId());
		}
		
		boolean success = false;
		while (!success) {
			Token token = monitor.writeLock();
			try {
				UnicastSocketClient.sendOneMessage(message.getSender().getLocation(), monitor.getWriteable(token), reply, false);
				success = true;
			} catch (CommunicationException e) {
				e.printStackTrace();
			} catch (LockException e) {
				e.printStackTrace();
			} finally {
				monitor.writeUnlock(token);
			}
		}
		
		return null;
	}

	@Override
	public boolean hasReply() {
		return true;
	}

}
