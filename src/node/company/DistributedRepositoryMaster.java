package node.company;

import java.util.*;

import node.data.RepositoryException;
import transaction.*;
import transaction.Lock.Token;
import communication.CommunicationException;
import communication.messages.DataTransferMessage;
import communication.messages.DataTransferReplyMessage;
import communication.protocols.*;
import communication.unicast.UnicastSocketClient;
import data.request.DataTransfer;
import data.trainnetwork.BookableSection;
import data.trainnetwork.Seat;

/**
 * The master node for the distributed repository (aka Dynamic Data Cluster Master)
 * @author Balazs Pete
 *
 */
public class DistributedRepositoryMaster extends DistributedRepository {

	// Time between backups (in seconds)
	private final int TIME_BETWEEN_BACKUPS = 100;
	
	public static String CLUSTER_NAME;
	
	public DistributedRepositoryMaster() throws RepositoryException {
		super();
	}

	@Override
	protected Set<Protocol> getProtocols() {
		Set<Protocol> protocols = new HashSet<Protocol>();
		
		// Accept and handle `Hello` requests from other nodes
		protocols.add(new HelloProtocol(nodes));
		
		// Accept and handle `ClusterHello` requests
		protocols.add(new ClusterHelloProtocol(CLUSTER_NAME, nodes));
		
		// Accept and handle distributed transactions
		protocols.add(new TransactionExecutionProtocol<String, Vault<BookableSection>, Set<Seat>>(transactions, communicationLock));
		protocols.add(new TransactionExecutionReplyProtocol<String, Vault<BookableSection>, Set<Seat>>(transactionCoordinators));
		protocols.add(new TransactionTerminationProtocol<String, Vault<BookableSection>, Set<Seat>>(transactions, communicationLock));
		protocols.add(new TransactionTerminationReplyProtocol<String, Vault<BookableSection>, Set<Seat>>(transactionCoordinators));
		
		return protocols;
	}
	
	/**
	 * Initiate scheduled backups
	 */
	public void scheduledBackup() {
		while (true) {
			try {
				sleep(1000 * TIME_BETWEEN_BACKUPS);
			} catch (InterruptedException e) {
				System.err.println("Failed to wait for backup: " + e.getMessage());
			}
			
			backup();
		}
	}
	
	// This will discard data if cannot send it, it is assumed for now that the node will not fail
	private void backup() {
		Collection<Vault<BookableSection>> toGet = null;
		
		Token sectionsToken = sections.readLock();
		try {
			 toGet = sections.getReadable(sectionsToken).values();
		} catch (LockException e) {
			// Failed to acquire read lock, oh well... will try next time...
			System.err.println("DistributedRepositoryMaster|backup: " + e.getMessage());
			return;
		} finally {
			sections.writeUnlock(sectionsToken);
		}
		
		Set<BookableSection> toSend = new HashSet<BookableSection>();
		while (toGet != null && toGet.size() > 0) {
			Collection<Vault<BookableSection>> failed = new HashSet<Vault<BookableSection>>();
			
			Iterator<Vault<BookableSection>> iterator = toGet.iterator();
			while (iterator.hasNext()) {
				Vault<BookableSection> vault = iterator.next();
				Token token = vault.readLock();
				try {
					toSend.add(vault.getReadable(token));
				} catch (LockException e) {
					failed.add(vault);
				} finally {
					vault.readUnlock(token);
				}
			}
			
			toGet = failed;
		}
		
		DataTransfer<BookableSection> transfer = new DataTransfer<BookableSection>(toSend);
		DataTransferMessage<BookableSection> message = new DataTransferMessage<BookableSection>(transfer, "BookableSection");
		
		Token t = communicationLock.writeLock();
		try {
			DataTransferReplyMessage reply = (DataTransferReplyMessage) 
					UnicastSocketClient.sendOneMessage(DATA_STORE_LOCATION, DATA_STORE_PORT, message, true);
			
			if (!message.getTransferId().equals((String) reply.getContents())) {
				// Something is very wrong on the other side if this happens
				new DataLoadException("Mismatching data transfer IDs");
			}
		} catch (CommunicationException e) {
			// Problems while talking to DataStore
			System.err.println("DistributedRepositoryMaster|backup: " + e.getMessage());
		} finally {
			communicationLock.writeUnlock(t);
		}
	}
}
