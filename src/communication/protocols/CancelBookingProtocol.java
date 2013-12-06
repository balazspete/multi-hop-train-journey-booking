package communication.protocols;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import node.company.TransactionContentGenerator;
import transaction.ShallowLock;
import transaction.TransactionContent;
import transaction.TransactionCoordinatorManager;
import transaction.Vault;
import transaction.WriteOnlyLock;
import communication.messages.BookingMessage;
import data.system.NodeInfo;
import data.trainnetwork.BookableSection;
import data.trainnetwork.Seat;

/**
 * A protocol used to handle booking cancellation transaction messages 
 * @author Balazs Pete
 *
 */
public class CancelBookingProtocol extends BookingProtocol {

	public CancelBookingProtocol(
			TransactionCoordinatorManager<String, Vault<BookableSection>, Set<Seat>> transactionCoordinators,
			ShallowLock<Map<String, Vault<BookableSection>>> sections,
			Set<NodeInfo> nodes, WriteOnlyLock<Integer> monitor) {
		super(transactionCoordinators, sections, nodes, monitor);
	}

	@Override
	public String getAcceptedMessageType() {
		return "BookingMessage:" + BookingMessage.Action.CANCEL;
	}
	
	@Override
	protected TransactionContent<String, Vault<BookableSection>, Set<Seat>> getTransactionContent(HashSet<Seat> seats) {
		return TransactionContentGenerator.getSeatCancellingContent(seats);
	}

	@Override
	public boolean hasReply() {
		return true;
	}
}
