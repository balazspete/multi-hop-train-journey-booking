package node.company;

import java.util.*;

import transaction.TransactionManager;
import transaction.Vault;

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
	
	protected static final String DATA_STORE_LOCATION = "localhost";
	protected static final int DATA_STORE_PORT = 8005;

	protected volatile Map<String, Vault<BookableSection>> sections;
	protected volatile TransactionManager<String, Vault<BookableSection>> transactions;
	
	public DistributedRepository() {
		// TODO load from config
		super(8001);
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
		
		System.out.println("requestmessage: " +requestMessage);
		try {
			client.createConnection();
			client.sendMessage(requestMessage);
		} catch (CommunicationException e) {
			e.printStackTrace();
			throw new DataLoadException();
		}
		
		Set<BookableSection> data = null;
		try {
			Message msg = client.getMessage();
			data = (Set<BookableSection>) msg.getContents();
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataLoadException();
		}

		try {
			client.endConnection();
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (BookableSection entry : data) {
			Vault<BookableSection> vault = new Vault<BookableSection>(entry);
			sections.put(entry.getID().intern(), vault);
		}
	}
}
