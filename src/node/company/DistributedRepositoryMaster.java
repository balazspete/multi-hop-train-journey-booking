package node.company;

import java.util.*;

import transaction.Vault;

import communication.protocols.Protocol;
import communication.protocols.TransactionCommitProtocol;
import communication.protocols.TransactionExecutionProtocol;
import data.trainnetwork.BookableSection;

public class DistributedRepositoryMaster extends DistributedRepository {

	@Override
	protected Set<Protocol> getProtocols() {
		Set<Protocol> protocols = new HashSet<Protocol>();
		protocols.add(new TransactionExecutionProtocol<String, Vault<BookableSection>>(transactions));
		protocols.add(new TransactionCommitProtocol<String, Vault<BookableSection>>(transactions));
		
		return protocols;
	}

	public static void main(String[] args) {
		new DistributedRepositoryMaster().start();
	}
	
}
