package communication.protocols;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import node.company.TransactionContentGenerator;
import transaction.Lock.Token;
import transaction.LockException;
import transaction.ShallowLock;
import transaction.SudoTransactionContent;
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
import data.trainnetwork.Section;

/**
 * Protocol to handle requests to pre-book seats
 * @author Balazs Pete
 *
 */
public class PreBookingProtocol implements Protocol {

	private TransactionCoordinatorManager<String, Vault<BookableSection>, Set<Seat>> transactionCoordinators;
	private ShallowLock<Map<String, Vault<BookableSection>>> sections;
	private Set<NodeInfo> nodes;
	private WriteOnlyLock<Integer> monitor;
	
	public PreBookingProtocol(
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
		return "BookingMessage:" + BookingMessage.Action.PREBOOK;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Message processMessage(Message message) {
		HashSet<Section> sections = (HashSet<Section>) message.getContents();
		SudoTransactionContent<String, Vault<BookableSection>, Set<Seat>> content 
			= TransactionContentGenerator.getSeatPreBookingContent(sections);
		TransactionCoordinator<String, Vault<BookableSection>, Set<Seat>> coordinator 
			= new TransactionCoordinator<String, Vault<BookableSection>, Set<Seat>>(content, this.sections, nodes, monitor);
		
		transactionCoordinators.put(coordinator.getTransactionId(), coordinator);
		coordinator.start();
		
		Object test= null;
		Token t = this.sections.readLock();
		try {
			test =  this.sections.getReadable(t).values();
		} catch (LockException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			this.sections.readUnlock(t);
		}
		
		
		TransactionStatus status;
		while ((status = coordinator.getStatus()) !=TransactionStatus.DEAD && status != TransactionStatus.DONE) {
			System.out.println(status);
			System.out.println(sections);
			try {
				// Wait until notified or timed out
				synchronized (coordinator) {
					coordinator.wait(5000);
				}
			} catch (InterruptedException e) {
				System.err.println(e.getMessage());
			}
		}
		
		
		System.out.println("######################################################################");
		t = this.sections.readLock();
		try {
			System.out.println(this.sections.getReadable(t) == test);
			for( Vault<BookableSection> s : this.sections.getReadable(t).values()) {
				s.debugPrint();
			}
		} catch (LockException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			this.sections.readUnlock(t);
		}
		
		
		Message reply;
		if (coordinator.getStage() != TransactionStage.ABORT) {
			Set<Seat> data = (Set<Seat>) coordinator.getReturnedData();
			HashSet<Seat> returnedData = new HashSet<Seat>(data);
			reply = new BookingReplyMessage();
			reply.setContents(returnedData);
		} else {
			reply = new ErrorMessage("Transaction aborted");
		}
		
		return reply;
	}

	@Override
	public boolean hasReply() {
		return true;
	}

}
