package node.company;

import java.util.HashSet;
import java.util.Set;

import transaction.Vault;

import communication.protocols.Protocol;
import communication.protocols.TransactionCommitProtocol;
import communication.protocols.TransactionExecutionProtocol;
import data.trainnetwork.BookableSection;

public class DistributedRepositorySlave extends DistributedRepository {

	@Override
	protected Set<Protocol> getProtocols() {
		Set<Protocol> protocols = new HashSet<Protocol>();
		protocols.add(new TransactionExecutionProtocol<String, Vault<BookableSection>>(transactions));
		protocols.add(new TransactionCommitProtocol<String, Vault<BookableSection>>(transactions));
		
		// TODO add client handling
		
		return protocols;
	}

	
	
	public static void main(String[] args) {
		DistributedRepositorySlave s = new DistributedRepositorySlave();
		s.start();
		
	}
	
}
