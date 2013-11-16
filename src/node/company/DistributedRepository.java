package node.company;

import java.util.*;

import transaction.*;
import transaction.Lock.Token;

import communication.CommunicationException;
import communication.messages.DataRequestMessage;
import communication.messages.Message;
import communication.protocols.Protocol;
import communication.unicast.UnicastSocketClient;
import data.request.BookableSectionDataRequest;
import data.trainnetwork.*;
import node.data.DataRepository; 

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
	}
	
	public static final int PORT = 8001;
	
	protected static final String DATA_STORE_LOCATION = "192.168.1.7";
	protected static final int DATA_STORE_PORT = 8005;

	protected static Vault<Map<String, Vault<BookableSection>>> sections;
	protected static TransactionManager<String, Vault<BookableSection>> transactions;
	protected static TransactionCoordinatorManager<String, Vault<BookableSection>> transactionCoordinators;
	
	protected static WriteOnlyLock<Integer> communicationLock;
	
	public DistributedRepository() {
		super(PORT);
	}

	@Override
	protected void initialize() {
		sections = new Vault<Map<String, Vault<BookableSection>>>(new HashMap<String, Vault<BookableSection>>());
		transactions = new TransactionManager<String, Vault<BookableSection>>(sections);
		transactionCoordinators = new TransactionCoordinatorManager<String, Vault<BookableSection>>();
		communicationLock = new WriteOnlyLock<Integer>(new Integer(PORT));
		
		int count = 0;
		while (count++ < 3) {
			try {
				restoreFromStore();
				count = Integer.MAX_VALUE;
			} catch (DataLoadException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected abstract Set<Protocol> getProtocols();
	@SuppressWarnings("unchecked")
	private void restoreFromStore() throws DataLoadException {
		UnicastSocketClient client = new UnicastSocketClient(DATA_STORE_LOCATION, DATA_STORE_PORT);
		
		BookableSectionDataRequest request = new BookableSectionDataRequest();
		DataRequestMessage<BookableSection> requestMessage = new DataRequestMessage<BookableSection>(request, "BookableSection");
		
		Set<BookableSection> data = null;
		
		Message msg;
		try {
			msg = UnicastSocketClient.sendOneMessage(client, requestMessage, true);
			data = (Set<BookableSection>) msg.getContents();
		} catch (CommunicationException e) {
			// Failed to contact DataStore
			return;
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
	}
}
