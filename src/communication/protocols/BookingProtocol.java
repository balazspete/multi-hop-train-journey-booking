package communication.protocols;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import node.company.TransactionContentGenerator;
import transaction.ShallowLock;
import transaction.TransactionContent;
import transaction.TransactionCoordinator;
import transaction.TransactionCoordinator.TransactionStage;
import transaction.TransactionCoordinator.TransactionStatus;
import transaction.TransactionCoordinatorManager;
import transaction.Vault;
import transaction.WriteOnlyLock;
import communication.messages.BookingMessage;
import communication.messages.BookingReplyMessage;
import communication.messages.ErrorMessage;
import communication.messages.Message;
import data.system.NodeInfo;
import data.trainnetwork.BookableSection;
import data.trainnetwork.Seat;

public class BookingProtocol implements Protocol {

	private TransactionCoordinatorManager<String, Vault<BookableSection>, Set<Seat>> transactionCoordinators;
	private ShallowLock<Map<String, Vault<BookableSection>>> sections;
	private Set<NodeInfo> nodes;
	private WriteOnlyLock<Integer> monitor;
	
	public BookingProtocol(
			TransactionCoordinatorManager<String, Vault<BookableSection>, Set<Seat>> transactionCoordinators, 
			ShallowLock<Map<String, Vault<BookableSection>>> sections,
			Set<NodeInfo> nodes,
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
		
		TransactionContent<String, Vault<BookableSection>, Set<Seat>> content = getTransactionContent(seats);
		TransactionCoordinator<String, Vault<BookableSection>, Set<Seat>> coordinator 
			= new TransactionCoordinator<String, Vault<BookableSection>, Set<Seat>>(content, this.sections, nodes, monitor);	

		transactionCoordinators.put(coordinator.getTransactionId(), coordinator);
		coordinator.start();
		
		TransactionStatus status;
		while ((status = coordinator.getStatus()) !=TransactionStatus.DEAD && status != TransactionStatus.DONE) {
			System.out.println(status);
			try {
				// Wait until notified or timed out
				synchronized (coordinator) {
					coordinator.wait(5000);
				}
			} catch (InterruptedException e) {
				System.err.println(e.getMessage());
			}
		}
		
		Message reply;
		TransactionStage stage = coordinator.getStage();
		if (stage != TransactionStage.ABORT && stage != TransactionStage.ABORTED) {
			reply = new BookingReplyMessage();
			
			Object data = coordinator.getReturnedData(); 
			if (data != null) {
				HashSet<Seat> returnedData = new HashSet<Seat>((Set<Seat>) data);
				reply.setContents(returnedData);
			}
		} else {
			// This shouldn't really happen...
			reply = new ErrorMessage("Transaction aborted");
		}
		
		return reply;
	}
	
	protected TransactionContent<String, Vault<BookableSection>, Set<Seat>> getTransactionContent(HashSet<Seat> seats) {
		return TransactionContentGenerator.getSeatReservingContent(seats);
	}

	@Override
	public boolean hasReply() {
		return true;
	}

}
