package node.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import node.NodeConstants;

import communication.CommunicationException;
import communication.messages.BookingMessage;
import communication.messages.BookingMessage.Action;
import communication.messages.Message;
import communication.messages.SectionStatusUpdateRequestMessage;
import communication.unicast.UnicastSocketClient;

import transaction.Lock.Token;
import transaction.WriteOnlyLock;

import data.system.NodeInfo;
import data.trainnetwork.Seat;
import data.trainnetwork.Section;

/**
 * An object to give a high level (information based) layer to communicate with the distributed repositories
 * @author Balazs Pete
 *
 */
public class CompanyRepositoryInterface {

	private Map<String, NodeInfo> routeToCompanies;
	private WriteOnlyLock<Integer> communicationsLock;
	
	public CompanyRepositoryInterface(Map<String, NodeInfo> routeToCompanies, WriteOnlyLock<Integer> communicationsLock) {
		this.routeToCompanies = routeToCompanies;
		this.communicationsLock = communicationsLock;
	}
	
	/**
	 * Attempt to book {@link Seat}s for a a set of {@link Section}s 
	 * @param sections The sections to book seats for
	 * @throws BookingException Thrown if the booking was not possible, or an error has occurred
	 */
	public Set<Seat> bookJourney(Set<Section> sections) throws BookingException {
		HashMap<NodeInfo, HashSet<Section>> nodeToSections = sortSectionsByCompany(sections);
		
		HashMap<NodeInfo, HashSet<Seat>> prebookedSeats;
		try {
			prebookedSeats = prebookHelper(nodeToSections);
		} catch (BookingException e) {
			// Server side exception handling was done in prebookHelper
			System.err.println("Failed to pre-book tickets: " + e.getMessage());
			prebookedSeats = null;
			throw e;
		}
		
		HashSet<Seat> seats = null;
		if (prebookedSeats != null) {
			try {
				seats = reserveHelper(prebookedSeats);
			} catch (BookingException e) {
				// Server side exception handling was done in prebookHelper
				System.err.println("Failed to book tickets: " + e.getMessage());
				throw e;
			}
		}
		
		return seats;
	}
	
	/**
	 * Cancel a journey
	 * @param seats
	 */
	public void unbookJourney(Set<Seat> seats) {
		HashSet<Seat> _seats = new HashSet<Seat>(seats);
		cancelBooking(_seats);
	}
	
	@SuppressWarnings("unchecked")
	private HashMap<NodeInfo, HashSet<Seat>> prebookHelper(HashMap<NodeInfo, HashSet<Section>> nodeToSections) throws BookingException {
		HashMap<NodeInfo, HashSet<Seat>> prebookedSeats = new HashMap<NodeInfo, HashSet<Seat>>();
		
		for (NodeInfo company : nodeToSections.keySet()) {
			BookingMessage message = new BookingMessage(Action.PREBOOK);
			message.setContents(nodeToSections.get(company));
		
			Message reply = sendMessageToCompany(company, message);
			if (reply.getType().equals("ErrorMessage")) {
				cancelBooking(prebookedSeats, Action.PREBOOK_DETELE);
				throw new BookingException("Prebooking tickets failed: " + reply.getContents());
			}
		
			HashSet<Seat> seats = (HashSet<Seat>) reply.getContents();
			prebookedSeats.put(company, seats);
		}
		
		return prebookedSeats;
	}
	
	@SuppressWarnings("unchecked")
	private HashSet<Seat> reserveHelper(HashMap<NodeInfo, HashSet<Seat>> companyToSeats) throws BookingException {
		HashSet<Seat> seats = new HashSet<Seat>();
		
		for (NodeInfo company : companyToSeats.keySet()) {
			BookingMessage message = new BookingMessage(Action.RESERVE);
			message.setContents(companyToSeats.get(company));
		
			Message reply = sendMessageToCompany(company, message);
			if (reply == null || reply.getType().equals("ErrorMessage")) {
				cancelBooking(seats);
				throw new BookingException("Booking tickets failed:" + reply.getContents());
			}
		
			seats.addAll((HashSet<Seat>) reply.getContents());
		}
		
		return seats;
	}
	
	public void cancelBooking(HashSet<Seat> seats) {
		HashMap<NodeInfo, HashSet<Seat>> companyToSeats = new HashMap<NodeInfo, HashSet<Seat>>();
		
		for (Seat seat : seats) {
			String sectionId = seat.getAbstractSectionId();
			NodeInfo company = routeToCompanies.get(sectionId);
			
			HashSet<Seat> _seats;
			if (!companyToSeats.containsKey(company)) {
				_seats = new HashSet<Seat>();
				companyToSeats.put(company, _seats);
			} else {
				_seats = companyToSeats.get(company);
			}
			
			_seats.add(seat);
		}

		cancelBooking(companyToSeats, Action.CANCEL);
	}
	
	private void cancelBooking(HashMap<NodeInfo, HashSet<Seat>> companyToSeats, Action bookingAction) {
		for (NodeInfo company : companyToSeats.keySet()) {
			BookingMessage message = new BookingMessage(bookingAction);
			message.setContents(companyToSeats.get(company));
			
			sendMessageToCompany(company, message);
		}
	}
	
	/**
	 * Get a status update on the input Sections
	 * @param sections The sections to request a status update on
	 * @return The updated sections
	 */
	@SuppressWarnings("unchecked")
	public Set<Section> getStatusUpdate(Set<Section> sections) {
		HashMap<NodeInfo, HashSet<Section>> companyToSections = sortSectionsByCompany(sections);
		HashSet<Section> replied = new HashSet<Section>();
		
		for (NodeInfo company : companyToSections.keySet()) {
			
			SectionStatusUpdateRequestMessage message = new SectionStatusUpdateRequestMessage();
			message.setContents(companyToSections.get(company));
		
			Message reply = sendMessageToCompany(company, message);
			replied.addAll((HashSet<Section>) reply.getContents());
		}
		
		return replied;
	}
	
	// Private method, we will not return interfaces to avoid unnecessary casting 
	private HashMap<NodeInfo, HashSet<Section>> sortSectionsByCompany(Set<Section> sections) {
		HashMap<NodeInfo, HashSet<Section>> nodeToSections = new HashMap<NodeInfo, HashSet<Section>>();
		
		for (Section section : sections) {
			String routeId = section.getRouteID();
			NodeInfo node = routeToCompanies.get(routeId);
			
			HashSet<Section> set;
			if (!nodeToSections.containsKey(node)) {
				set = new HashSet<Section>();
				nodeToSections.put(node, set);
			} else {
				set = nodeToSections.get(node);
			}
			
			set.add(section);
		}
		
		return nodeToSections;
	}
	
	private Message sendMessageToCompany(NodeInfo info, Message message) {
		Token token = communicationsLock.writeLock();
		
		UnicastSocketClient client = new UnicastSocketClient(info.getLocation(), NodeConstants.DYNAMIC_CLUSTER_PORT);
		
		Message reply = null;
		try {
			reply = UnicastSocketClient.sendOneMessage(client, message, true);
		} catch (CommunicationException e) {
			// A null reply will represent a problem, we assume that all communications will require a reply
			System.err.println(e.getMessage());
		}
		
		communicationsLock.writeUnlock(token);
		return reply;
	}
	
	
	
	
}
