package node.company;

import java.util.*;

import transaction.*;
import transaction.Lock.Token;
import communication.CommunicationException;
import communication.messages.DataRequestMessage;
import communication.messages.DataRequestReplyMessage;
import communication.messages.HelloMessage;
import communication.messages.HelloReplyMessage;
import communication.messages.Message;
import communication.protocols.Protocol;
import communication.unicast.UnicastSocketClient;
import data.request.BookableSectionDataRequest;
import data.request.NodeInfoRequest;
import data.system.NodeInfo;
import data.trainnetwork.*;
import node.NodeConstants;
import node.data.DataRepository; 
import node.data.RepositoryException;

/**
 * The core module of a distributed {@link DataRepository} 
 * @author Balazs Pete
 *
 */
public abstract class DistributedRepository extends DataRepository {
	
	/**
	 * An exception thrown when loading data from the {@link DistributedRepositoryDataStore} fails
	 * @author Balazs Pete
	 *
	 */
	public class DataLoadException extends Exception {
		private static final long serialVersionUID = -6869722814419718639L;
		public DataLoadException(String message) {
			super(message);
		}
	}
	
	protected static String REPOSITORY_NAME;
	public static final int PORT = NodeConstants.DYNAMIC_CLUSTER_PORT;
	
	protected static String DATA_STORE_LOCATION;
	protected static final int DATA_STORE_PORT = NodeConstants.DYNAMIC_CLUSTER_STORE_PORT;

	protected static Vault<Map<String, Vault<BookableSection>>> sections;
	protected static TransactionManager<String, Vault<BookableSection>, Set<Seat>> transactions;
	protected static TransactionCoordinatorManager<String, Vault<BookableSection>, Set<Seat>> transactionCoordinators;
	
	protected static WriteOnlyLock<Integer> communicationLock;
	
	protected static Set<NodeInfo> nodes;
	
	public DistributedRepository() throws RepositoryException {
		super(NodeConstants.DYNAMIC_CLUSTER_PORT);
		sayHello();
	}

	@Override
	protected void initialize() throws DistributedRepositoryException {
		sections = new Vault<Map<String, Vault<BookableSection>>>(new HashMap<String, Vault<BookableSection>>());
		transactions = new TransactionManager<String, Vault<BookableSection>, Set<Seat>>(sections);
		transactionCoordinators = new TransactionCoordinatorManager<String, Vault<BookableSection>, Set<Seat>>();
		communicationLock = new WriteOnlyLock<Integer>(new Integer(NodeConstants.DYNAMIC_CLUSTER_PORT));
		nodes = new HashSet<NodeInfo>();
		
		try {
			restoreFromStore();
		} catch (DataLoadException e) {
			System.err.println(e.getMessage());
			throw DistributedRepositoryException.FAILED_TO_LOAD_DATA;
		}
	}

	@Override
	protected abstract Set<Protocol> getProtocols();

	private void sayHello() {
		HelloMessage message = HelloMessage.getHi();
		
		Iterator<NodeInfo> it = nodes.iterator();
		while (it.hasNext()) {
			NodeInfo node = it.next();
			Token token = communicationLock.writeLock();
			try {
				UnicastSocketClient.sendOneMessage(node.getLocation(), PORT, message, true);
			} catch (CommunicationException e) {
				System.err.println(e.getMessage());
				
				// Node is unreachable, remove it...
				nodes.remove(node);
			} finally {
				communicationLock.writeUnlock(token);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void restoreFromStore() throws DataLoadException {
		UnicastSocketClient client = new UnicastSocketClient(DATA_STORE_LOCATION, DATA_STORE_PORT);
		
		BookableSectionDataRequest request = new BookableSectionDataRequest();
		DataRequestMessage<BookableSection> requestMessage = new DataRequestMessage<BookableSection>(request, "BookableSection");
		
		Set<BookableSection> data = null;
		
		boolean error = false;
		
		Message msg;
		Token token = communicationLock.writeLock();
		try {
			msg = UnicastSocketClient.sendOneMessage(client, requestMessage, true);
			data = (Set<BookableSection>) msg.getContents();
		} catch (CommunicationException e) {
			// Failed to contact DataStore
			error = true;
		} finally {
			communicationLock.writeUnlock(token);
		}
		
		if (error) {
			throw new DataLoadException("Failed to load data from `store`");
		}
		
		boolean committed = false;
		while (data != null && !committed) {
			Token t = sections.writeLock();
			Map<String, Vault<BookableSection>> _sections;
			try {
				_sections = sections.getWriteable(t);
				for (BookableSection entry : data) {
					Vault<BookableSection> vault = new Vault<BookableSection>(entry);
					_sections.put(entry.getID().intern(), vault);
				}
				sections.commit(t);
				committed = true;
			} catch (LockException e) {
				System.err.println(e.getMessage());
				// Loop until all sections are saved
			} finally {
				sections.writeUnlock(t);
			}
		}
		
		token = communicationLock.writeLock();
		try {
			//--- begin HELLO 
			msg = HelloMessage.getHi();
			HelloReplyMessage reply = (HelloReplyMessage) UnicastSocketClient.sendOneMessage(client, msg, true);
			NodeInfo myOwn = reply.getHelloer();
			//--- end HELLO
			
			//--- begin GetNodes 
			NodeInfoRequest _request = new NodeInfoRequest(myOwn);
			msg = new DataRequestMessage<NodeInfo>(_request, "NodeInfo");
			
			DataRequestReplyMessage<NodeInfo> _reply = (DataRequestReplyMessage<NodeInfo>) UnicastSocketClient.sendOneMessage(client, msg, true);
			nodes = (Set<NodeInfo>) _reply.getContents();
			//--- end GetNodes
		} catch (CommunicationException e) {
			// Failed to contact DataStore
			error = true;
		} finally {
			communicationLock.writeUnlock(token);
		}
		
		if (error) {
			throw new DataLoadException("Failed to load data from `store`");
		}
	}
}
