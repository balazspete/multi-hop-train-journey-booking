package node.company;

import java.util.*;

import transaction.TransactionManager;
import transaction.Vault;
import transaction.WriteOnlyLock;

import communication.CommunicationException;
import communication.messages.DataRequestMessage;
import communication.messages.Message;
import communication.protocols.Protocol;
import communication.unicast.UnicastSocketClient;
import data.request.BookableSectionDataRequest;
import data.trainnetwork.*;
import node.data.DataRepository; 

public abstract class DistributedRepository extends DataRepository {
	
	public class DataLoadException extends Exception {
		private static final long serialVersionUID = -6869722814419718639L;
	}
	
	public static final int PORT = 8001;
	
	protected static final String DATA_STORE_LOCATION = "localhost";
	protected static final int DATA_STORE_PORT = 8005;

	protected volatile Map<String, Vault<BookableSection>> sections;
	protected volatile TransactionManager<String, Vault<BookableSection>> transactions;
	
	protected static WriteOnlyLock<Integer> communicationLock = new WriteOnlyLock<Integer>(new Integer(PORT));
	
	public DistributedRepository() {
		super(PORT);
	}

	@Override
	protected void initialize() {
		sections = new HashMap<String, Vault<BookableSection>>();
		transactions = new TransactionManager<String, Vault<BookableSection>>(sections);
		
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
		
		if (data != null) {
			for (BookableSection entry : data) {
				Vault<BookableSection> vault = new Vault<BookableSection>(entry);
				sections.put(entry.getID().intern(), vault);
			}
		}
	}
}
