package node.company;

import java.util.*;

import node.data.RepositoryException;
import transaction.*;
import communication.protocols.*;
import data.trainnetwork.BookableSection;

public class DistributedRepositoryMaster extends DistributedRepository {

	public DistributedRepositoryMaster() throws RepositoryException {
		super();
	}

	@Override
	protected Set<Protocol> getProtocols() {
		Set<Protocol> protocols = new HashSet<Protocol>();
		
		// Accept and handle `Hello` requests from other nodes
		protocols.add(new HelloProtocol(nodes));
		
		// Accept and handle distributed transactions
		protocols.add(new TransactionExecutionProtocol<String, Vault<BookableSection>>(transactions, communicationLock));
		protocols.add(new TransactionExecutionReplyProtocol<String, Vault<BookableSection>>(transactionCoordinators));
		protocols.add(new TransactionCommitProtocol<String, Vault<BookableSection>>(transactions, communicationLock));
		protocols.add(new TransactionCommitReplyProtocol<String, Vault<BookableSection>>(transactionCoordinators));
		
		return protocols;
	}

	public void test() {
		
		TransactionContent<String, Vault<BookableSection>> c = TransactionContentGenerator.getTestContent();
		
		TransactionCoordinator<String, Vault<BookableSection>> tc
			= new TransactionCoordinator<String, Vault<BookableSection>>(c, sections, nodes, communicationLock);
		
		transactionCoordinators.put(tc.getTransactionId(), tc);
		
		tc.start();
		
	}
	
	
	public static void main(String[] args) {
		DistributedRepositoryMaster r;
		try {
			r = new DistributedRepositoryMaster();
			r.start();
			r.test();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
	
}
