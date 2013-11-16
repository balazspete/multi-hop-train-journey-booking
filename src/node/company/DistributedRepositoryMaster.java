package node.company;

import java.util.*;

import org.joda.time.DateTime;

import transaction.*;
import transaction.Lock.Token;

import communication.protocols.*;
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
		BookableSection s = new BookableSection("section", 1, DateTime.now(), 10, 10);
		s.setMaxPassengers(100);
		Vault<BookableSection> v = new Vault<BookableSection>(s);
		sections.put("---", v);
		TransactionContent<String, Vault<BookableSection>> c = TransactionContentGenerator.getTestContent();
		
		NodeInfo i = new NodeInfo("VAIO");
		i.addLocation("192.168.1.13");
		
		Set<NodeInfo> ni = new HashSet<NodeInfo>();
		ni.add(i);
		
		TransactionCoordinator<String, Vault<BookableSection>> tc
			= new TransactionCoordinator<String, Vault<BookableSection>>(c, sections, ni, communicationLock);
		
		transactionCoordinators.put(tc.getTransactionId(), tc);
		
		tc.start();
		
		while (true) {
			Token t = v.readLock();
			try {
				System.out.println("sections-check" + v.getReadable(t).toString());
			} catch (LockException e) {
				e.printStackTrace();
			} finally {
				v.readUnlock(t);
			}
			
			try {
				sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	public static void main(String[] args) {
		DistributedRepositoryMaster r = new DistributedRepositoryMaster();
		r.start();
		r.test();
	}
	
}
