package data.trainnetwork;

import java.util.*;

import org.joda.time.DateTime;

public class BookableSection extends Section {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1899714743839518616L;
	
	private Set<Seat> reservedSeats, preReservedSeats;
	
	public BookableSection(String routeID, int sectionNumber, DateTime startTime,
			long journeyLength, int cost) {
		super(routeID, sectionNumber, startTime, journeyLength, cost);
		
		this.reservedSeats = new HashSet<Seat>();
		this.preReservedSeats = new HashSet<Seat>();
	}
	
	public BookableSection(String routeID, int sectionNumber, DateTime startTime,
			long journeyLength, int cost, Set<Seat> bookedSeats, Set<Seat> prebookedSeats) {
		super(routeID, sectionNumber, startTime, journeyLength, cost);
		
		this.reservedSeats = bookedSeats;
		this.preReservedSeats = prebookedSeats;
	}

	@Override
	public synchronized Status getStatus() {
		if (reservedSeats.size() + preReservedSeats.size() < maxPassengers) return Status.AVAILABLE;
		return Status.FULL;
	}

	@Override
	public void setStatus(Status status) {
		// Status is determined by seats
	}
	
	/**
	 * Reserve a pre-reserved seat
	 * A seat must have been pre-reserved with the use of {@link preReserve()}
	 * @param seat The seat to reserve
	 * @return true of the seat has been reserved
	 * @throws NoSuchSeatException Thrown if input seat has not been pre-reserved or does not exist
	 */
	public synchronized boolean reserve(Seat seat) throws NoSuchSeatException {
		if (!preReservedSeats.contains(seat))
			new NoSuchSeatException("No seat with id {" + seat.getId() + "} has been pre-booked");
		
		if (preReservedSeats.remove(seat)) {
			reservedSeats.add(seat);
		}
		
		return reservedSeats.contains(seat);
	}
	
	/**
	 * Pre-reserve a seat
	 * @return The assigned seat which has been pre-reserved
	 * @throws SectionFullException Thrown if there are no more free seats available
	 */
	public synchronized Seat preReserve() throws SectionFullException {
		if (getStatus() == Status.FULL) 
			throw new SectionFullException("No more free seats available, cannot pre-reseve more seats");
		
		Seat seat = new Seat();
		seat.addSection(this);
		preReservedSeats.add(seat);
		
		return seat;
	}
	
	/**
	 * Undo a reservation, created a free seat
	 * @param seat The seat to un-reserve
	 * @return True if the seat has been un-reserved
	 * @throws NoSuchSeatException Thrown if there is no such seat reserved
	 */
	public synchronized boolean undoReserve(Seat seat) throws NoSuchSeatException {
		if (!reservedSeats.contains(seat))
			throw new NoSuchSeatException("No reserved seat with id {" + seat.getId() + "} exists");
		
		return reservedSeats.remove(seat);
	}
	
	/**
	 * Unto a pre-reservation, making a new free seat
	 * @param seat The seat to make available
	 * @return True if the pre-reservation has been undone
	 * @throws NoSuchSeatException Thrown if there is no such seat pre-reserved
	 */
	public synchronized boolean undoPreReserve(Seat seat) throws NoSuchSeatException {
		if (!preReservedSeats.contains(seat))
			throw new NoSuchSeatException("No pre-reserved seat with id {" + seat.getId() + "} exists");
		
		return preReservedSeats.remove(seat);
	}
	
	/**
	 * 
	 * @param string
	 * @return
	 */
	public static BookableSection getSectionFromString(String string) {
		String[] parts = string.split("=");
		
		BookableSection section;
		try {
			section = new BookableSection(
				parts[0], 
				Integer.parseInt(parts[1]), 
				DateTime.parse(parts[5]), 
				Integer.parseInt(parts[4]), 
				Integer.parseInt(parts[3]));
			section.setMaxPassengers(Integer.parseInt(parts[2]));
		} catch (Exception e) {
			System.err.println(e.getMessage());
			section = null;
		}
		
		return section;
	}
	
}
