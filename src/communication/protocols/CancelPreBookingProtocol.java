package communication.protocols;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import node.company.TransactionContentGenerator;

import communication.messages.BookingMessage;

import transaction.TransactionContent;
import transaction.TransactionCoordinatorManager;
import transaction.Vault;
import transaction.WriteOnlyLock;
import data.system.NodeInfo;
import data.trainnetwork.BookableSection;
import data.trainnetwork.Seat;

public class CancelPreBookingProtocol extends BookingProtocol {

	public CancelPreBookingProtocol(
			TransactionCoordinatorManager<String, Vault<BookableSection>, Set<Seat>> transactionCoordinators,
			Vault<Map<String, Vault<BookableSection>>> sections,
			Set<NodeInfo> nodes, WriteOnlyLock<Integer> monitor) {
		super(transactionCoordinators, sections, nodes, monitor);
	}

	@Override
	public String getAcceptedMessageType() {
		return "BookingMessage:" + BookingMessage.Action.PREBOOK_DETELE;
	}
	
	@Override
	protected TransactionContent<String, Vault<BookableSection>, Set<Seat>> getTransactionContent(HashSet<Seat> seats) {
		return TransactionContentGenerator.getSeatPrebookCancelContent(seats);
	}

	@Override
	public boolean hasReply() {
		return true;
	}
}
