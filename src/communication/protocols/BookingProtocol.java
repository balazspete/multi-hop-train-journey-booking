package communication.protocols;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import node.company.TransactionContentGenerator;

import transaction.TransactionContent;
import transaction.TransactionCoordinator;
import transaction.TransactionCoordinator.TransactionStage;
import transaction.TransactionCoordinatorManager;
import transaction.Vault;
import transaction.WriteOnlyLock;

import communication.messages.BookingMessage;
import communication.messages.BookingReplyMessage;
import communication.messages.Message;
import data.system.NodeInfo;
import data.trainnetwork.BookableSection;
import data.trainnetwork.Seat;

public class BookingProtocol implements Protocol {

	private TransactionCoordinatorManager<String, Vault<BookableSection>, Set<Seat>> transactionCoordinators;
	private Vault<Map<String, Vault<BookableSection>>> sections;
	private HashSet<NodeInfo> nodes;
	private WriteOnlyLock<Integer> monitor;
	
	public BookingProtocol(
			TransactionCoordinatorManager<String, Vault<BookableSection>, Set<Seat>> transactionCoordinators, 
			Vault<Map<String, Vault<BookableSection>>> sections,
			HashSet<NodeInfo> nodes,
			WriteOnlyLock<Integer> monitor) {
		this.transactionCoordinators = transactionCoordinators;
		this.sections = sections;
		this.nodes = nodes;
		this.monitor = monitor;
	}
	
	@Override
	public String getAcceptedMessageType() {
		return "BookingMessage:" + BookingMessage.Action.RESERVE;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Message processMessage(Message message) {
		HashSet<Seat> seats = (HashSet<Seat>) message.getContents();
		
		TransactionContent<String, Vault<BookableSection>, Set<Seat>> content
			= TransactionContentGenerator.getSeatReservingContent(seats);
		TransactionCoordinator<String, Vault<BookableSection>, Set<Seat>> coordinator 
			= new TransactionCoordinator<String, Vault<BookableSection>, Set<Seat>>(content, this.sections, nodes, monitor);	

		transactionCoordinators.put(coordinator.getTransactionId(), coordinator);
		
		TransactionStage stage;
		// No need to wait for TransactionStatus.DONE, it's okay to reply when the transaction is in COMMIT or ABORT stage
		while ((stage = coordinator.getStage()) == TransactionStage.COMMIT || stage == TransactionStage.ABORT) {
			try {
				// Wait until notified or timed out
				System.out.println("gonna wait for transaction end");
				coordinator.wait(5000);
			} catch (InterruptedException e) {
				System.err.println(e.getMessage());
			}
		}
		
		HashSet<Seat> returnedData = new HashSet<Seat>(content.getReturnedData());
		BookingReplyMessage reply = new BookingReplyMessage();
		reply.setContents(returnedData);
		
		System.out.println("content: " + returnedData);
		
		return reply;
	}

	@Override
	public boolean hasReply() {
		return true;
	}

}
