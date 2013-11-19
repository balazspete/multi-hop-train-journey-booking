package transaction;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import transaction.Lock.Token;

import com.rits.cloning.Cloner;

import communication.CommunicationException;
import communication.messages.Message;
import communication.messages.TransactionCommitMessage;
import communication.messages.TransactionExecutionMessage;
import communication.messages.TransactionExecutionReplyMessage.Reply;
import communication.unicast.UnicastSocketClient;

import data.system.NodeInfo;

/**
 * An object used to coordinate a transaction within multiple nodes on the origin
 * @author Balazs Pete
 *
 * @param <KEY>
 * @param <VALUE>
 */
public class TransactionCoordinator<KEY, VALUE, RETURN> extends Thread {
	
	public enum TransactionStage {
		INITIAL, COMMIT, ABORT
	}
	
	/**
	 * Used to state the status of a transaction
	 * @author Balazs Pete
	 *
	 */
	public enum TransactionStatus {
		ALIVE, DEAD, SLEEPING, DONE
	}
	
	private TransactionContent<KEY, VALUE, RETURN> content;
	private Vault<Map<KEY, VALUE>> dataVault;
	private Collection<NodeInfo> nodes;
	private WriteOnlyLock<Integer> monitor;
	
	private Transaction transaction;
	
	private TransactionStage stage = TransactionStage.INITIAL;
	private TransactionStatus status = TransactionStatus.SLEEPING;
	
	private Collection<NodeInfo> replies = null;
	
	
	/**
	 * Create a new {@link TransactionCoordinator}
	 * @param content The {@link TransactionContent} to forward to other nodes
	 * @param data The {@link Map<KEY, VALUE>} to work on 
	 * @param nodes The collection of nodes to correspond with
	 * @param monitor The monitor used to control communication, its value is the port number used fro communication
	 */
	public TransactionCoordinator(
		TransactionContent<KEY, VALUE, RETURN> content, 
		Vault<Map<KEY, VALUE>> data, 
		Collection<NodeInfo> nodes, 
		WriteOnlyLock<Integer> monitor
	) {
		this.content = content;
		this.dataVault = data;
		this.nodes = nodes;
		this.monitor = monitor;
	}
	
	/**
	 * Get the status of the wrapped {@link Transaction}
	 * @return The status of the transaction
	 */
	public TransactionStatus getStatus() {
		return status;
	}
	
	/**
	 * Get the stage of the wrapper {@link Transaction}
	 * @return The stage the transaction is in
	 */
	public TransactionStage getStage() {
		return stage;
	}
	
	/**
	 * Get the ID of the wrapped transaction
	 * @return The ID of the corresponding transaction
	 */
	public String getTransactionId() {
		return content.getId();
	}
	
	@Override
	public void run() {
		replies = null;
		status = TransactionStatus.ALIVE; 
		
		if (stage == TransactionStage.INITIAL) {
			try {
				doLocalTransaction();
				doRemoteTransaction();
				stage = TransactionStage.COMMIT;
			} catch (FailedTransactionException e) {
				status = TransactionStatus.DEAD;
				notifyAll();
				return;
			}
		} else if (status != TransactionStatus.DONE){ 
			if (stage == TransactionStage.COMMIT) {
				transaction.commit();
				System.out.println("Transaction COMMITTED - ID: " + transaction.getId());
				doRemoteCommit();
			} else {
				transaction.abort();
				System.out.println("Transaction ABORTED - ID: " + transaction.getId());
				doRemoteAbort();
			}
			
			status = TransactionStatus.DONE;
			notifyAll();
			return;
		} else {
			status = TransactionStatus.DEAD;
			notifyAll();
			return;
		}
		
		status = TransactionStatus.SLEEPING;
	}
	
	/**
	 * Log a reply from a remote & continue transaction if all remote nodes have answered
	 * @param location The location of the remote
	 * @param reply The reply received from the remote
	 * @return True if the answer has been acknowledged
	 */
	public synchronized boolean logReply(String location, Reply reply) {
		if (replies == null) {
			replies = new HashSet<NodeInfo>();
		}

		NodeInfo node = null;
		for (NodeInfo _node : nodes) {
			if (location.equals(_node.getLocation())) {
				node = _node;
				break;
			}
		}

		if (node == null) return false;
		
		replies.add(node);
		
		if (status != TransactionStatus.DONE) {
			if (reply == null || reply == Reply.FAILED) {
				stage = TransactionStage.ABORT;
			}
			
			if (replies.size() == nodes.size()) {
				new Thread(this).start();
			}
		}
		
		return true;
	}
	
	private void doLocalTransaction() throws FailedTransactionException {
		TransactionContent<KEY, VALUE, RETURN> _content = new Cloner().deepClone(content);
		_content.setData(dataVault);
		
		transaction = new Transaction(_content);
		transaction.execute();
	}
	
	private void doRemoteTransaction() {
		Token token = monitor.writeLock();
		try {
			TransactionExecutionMessage<KEY, VALUE, RETURN> message = new TransactionExecutionMessage<KEY, VALUE, RETURN>(content.getId());
			message.setContents(content);
			sendMessageToAllNodes(message, token);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			monitor.writeUnlock(token);
		}
	}
	
	private void doRemoteCommit() {
		TransactionCommitMessage message = new TransactionCommitMessage(content.getId());
		message.setContents(TransactionCommitMessage.CommitAction.COMMIT);
		Token token = monitor.writeLock();
		try {
			sendMessageToAllNodes(message, token);
		} catch (LockException e) {
			e.printStackTrace();
		} finally {
			monitor.writeUnlock(token);
		}
	}
	
	private void doRemoteAbort() {
		TransactionCommitMessage message = new TransactionCommitMessage(content.getId());
		message.setContents(TransactionCommitMessage.CommitAction.ABORT);
		Token token = monitor.writeLock();
		try {
			sendMessageToAllNodes(message, token);
		} catch (LockException e) {
			e.printStackTrace();
		} finally {
			monitor.writeUnlock(token);
		}
	}
	
	private void sendMessageToAllNodes(Message message, Token token) throws LockException {
		Queue<NodeInfo> messageQueue = new ConcurrentLinkedQueue<NodeInfo>(nodes);
		
		int count = 0;
		while (count++ < nodes.size() * 3 && messageQueue.size() != 0) {
			NodeInfo node = messageQueue.remove();
			try {
				UnicastSocketClient.sendOneMessage(node.getLocation(), monitor.getWriteable(token), message, false);
			} catch (CommunicationException e) {
				messageQueue.add(node);
			}
		}
		
		// If there are any remaining nodes after 3 tries, we assume they are unreachable, hence removing them from the list of nodes
		for (NodeInfo node : messageQueue) {
			nodes.remove(node);
		}
		
	}
}
