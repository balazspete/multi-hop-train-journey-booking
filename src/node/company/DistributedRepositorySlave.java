package node.company;

import java.util.HashSet;
import java.util.Set;

import node.data.RepositoryException;
import transaction.TransactionContent;
import transaction.TransactionCoordinator;
import transaction.Vault;
import communication.protocols.BookingProtocol;
import communication.protocols.CancelBookingProtocol;
import communication.protocols.CancelPreBookingProtocol;
import communication.protocols.HelloProtocol;
import communication.protocols.PreBookingProtocol;
import communication.protocols.Protocol;
import communication.protocols.SectionStatusUpdateRequestHandlingProtocol;
import communication.protocols.TransactionTerminationProtocol;
import communication.protocols.TransactionTerminationReplyProtocol;
import communication.protocols.TransactionExecutionProtocol;
import communication.protocols.TransactionExecutionReplyProtocol;
import data.trainnetwork.BookableSection;
import data.trainnetwork.Seat;

/**
 * The slave node for a distributed repository (aka Dynamic Data Cluster Slave)
 * @author Balazs Pete
 *
 */
public class DistributedRepositorySlave extends DistributedRepository {

	public DistributedRepositorySlave() throws RepositoryException {
		super();
	}

	@Override
	protected Set<Protocol> getProtocols() {
		Set<Protocol> protocols = new HashSet<Protocol>();
		
		// Accept and handle `Hello` requests from other nodes
		protocols.add(new HelloProtocol(nodes));
		
		// Accept and handle distributed transactions
		protocols.add(new TransactionExecutionProtocol<String, Vault<BookableSection>, Set<Seat>>(transactions, communicationLock));
		protocols.add(new TransactionExecutionReplyProtocol<String, Vault<BookableSection>, Set<Seat>>(transactionCoordinators));
		protocols.add(new TransactionTerminationProtocol<String, Vault<BookableSection>, Set<Seat>>(transactions, communicationLock));
		protocols.add(new TransactionTerminationReplyProtocol<String, Vault<BookableSection>, Set<Seat>>(transactionCoordinators));
		
		// Client booking/cancelling handling
		protocols.add(new PreBookingProtocol(transactionCoordinators, sections, nodes, communicationLock));
		protocols.add(new BookingProtocol(transactionCoordinators, sections, nodes, communicationLock));
		protocols.add(new CancelPreBookingProtocol(transactionCoordinators, sections, nodes, communicationLock));
		protocols.add(new CancelBookingProtocol(transactionCoordinators, sections, nodes, communicationLock));
		
		protocols.add(new SectionStatusUpdateRequestHandlingProtocol(sections));
		
		return protocols;
	}

	@Deprecated
	public void test() {
		try {
			sleep(5000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		TransactionContent<String, Vault<BookableSection>, Set<Seat>> c = TransactionContentGenerator.getTestContent();
		
		TransactionCoordinator<String, Vault<BookableSection>, Set<Seat>> tc
			= new TransactionCoordinator<String, Vault<BookableSection>, Set<Seat>>(c, sections, nodes, communicationLock);
		
		transactionCoordinators.put(tc.getTransactionId(), tc);
		
		tc.start();
	}
}
