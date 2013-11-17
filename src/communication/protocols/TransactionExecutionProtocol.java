package communication.protocols;

import transaction.FailedTransactionException;
import transaction.Lock.Token;
import transaction.LockException;
import transaction.TransactionContent;
import transaction.TransactionManager;
import transaction.WriteOnlyLock;
import communication.CommunicationException;
import communication.messages.Message;
import communication.messages.TransactionExecutionReplyMessage;
import communication.unicast.UnicastSocketClient;

/**
 * A protocol to handle incoming {@link Transaction} requests and execute them
 * @author Balazs Pete
 *
 */
public class TransactionExecutionProtocol<KEY, VALUE> implements Protocol {

	private TransactionManager<KEY, VALUE> manager;
	private WriteOnlyLock<Integer> monitor;
	
	public TransactionExecutionProtocol(TransactionManager<KEY, VALUE> manager, WriteOnlyLock<Integer> monitor) {
		this.manager = manager;
		this.monitor = monitor;
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
		
		TransactionExecutionReplyMessage reply = null;
		try {
			manager.execute(content);
			reply = TransactionExecutionReplyMessage.readyToCommitMessage(content.getId());
		} catch (FailedTransactionException e) {
			reply = TransactionExecutionReplyMessage.failedMessage(content.getId());
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
		return false;
	}
}
