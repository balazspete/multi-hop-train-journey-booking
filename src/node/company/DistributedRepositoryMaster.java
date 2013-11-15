package node.company;

import java.util.*;

import org.joda.time.DateTime;

import transaction.FailedTransactionException;
import transaction.TransactionContent;
import transaction.TransactionCoordinator;
import transaction.Vault;

import communication.protocols.Protocol;
import communication.protocols.TransactionCommitProtocol;
import communication.protocols.TransactionCommitReplyProtocol;
import communication.protocols.TransactionExecutionProtocol;
import communication.protocols.TransactionExecutionReplyProtocol;
import data.system.NodeInfo;
import data.trainnetwork.BookableSection;

public class DistributedRepositoryMaster extends DistributedRepository {

	@Override
	protected Set<Protocol> getProtocols() {
		Set<Protocol> protocols = new HashSet<Protocol>();
		
		protocols.add(new TransactionExecutionProtocol<String, Vault<BookableSection>>(transactions, communicationLock));
		protocols.add(new TransactionExecutionReplyProtocol<String, Vault<BookableSection>>(transactionCoordinators));
		
		protocols.add(new TransactionCommitProtocol<String, Vault<BookableSection>>(transactions, communicationLock));
		protocols.add(new TransactionCommitReplyProtocol<String, Vault<BookableSection>>(transactionCoordinators));
		
		return protocols;
	}

	public void test() {
		TransactionContent<String, Vault<BookableSection>> c = TransactionContentGenerator.getTestContent();
		
		NodeInfo i = new NodeInfo("VAIO");
		i.addLocation("192.168.1.13");
		
		Set<NodeInfo> ni = new HashSet<NodeInfo>();
		ni.add(i);
		
		TransactionCoordinator<String, Vault<BookableSection>> tc
			= new TransactionCoordinator<String, Vault<BookableSection>>(c, sections, ni, communicationLock);
		
		transactionCoordinators.put("00", tc);
		
		tc.start();
	}
	
	
	public static void main(String[] args) {
		DistributedRepositoryMaster r = new DistributedRepositoryMaster();
		r.start();
		r.test();
		
	}
	
}
