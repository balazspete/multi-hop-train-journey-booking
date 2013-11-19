package node.company;

import java.util.HashSet;
import java.util.Set;

import node.FatalNodeException;
import node.data.RepositoryException;
import transaction.Vault;
import communication.protocols.HelloProtocol;
import communication.protocols.Protocol;
import communication.protocols.TransactionCommitProtocol;
import communication.protocols.TransactionCommitReplyProtocol;
import communication.protocols.TransactionExecutionProtocol;
import communication.protocols.TransactionExecutionReplyProtocol;
import data.trainnetwork.BookableSection;
import data.trainnetwork.Seat;

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
		protocols.add(new TransactionCommitProtocol<String, Vault<BookableSection>, Set<Seat>>(transactions, communicationLock));
		protocols.add(new TransactionCommitReplyProtocol<String, Vault<BookableSection>, Set<Seat>>(transactionCoordinators));
		
		// TODO add client handling
		
		return protocols;
	}

	public void test() {
		while (true) {
			//sections.debugPrint();
			System.out.println(nodes);
			try {
				sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		DistributedRepositorySlave s;
		try {
			if (args.length < 1 || !(args[0] instanceof String)) {
				throw new RepositoryException("Arg1 required to be the master node's location of the static data cluster");
			}
			
			DistributedRepositorySlave.DATA_STORE_LOCATION = args[0];
			s = new DistributedRepositorySlave();
			s.start();
			//s.test();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
