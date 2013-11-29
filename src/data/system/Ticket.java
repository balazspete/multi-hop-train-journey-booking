package data.system;

import java.util.Set;

import org.joda.time.DateTime;

import data.trainnetwork.Seat;

/**
 * An object describing a booked journey
 * <i>Contains the start and end station IDs, the start time, and the set of seats</i>
 * @author Balazs Pete
 *
 */
public class Ticket {

	private String source, target;
	private DateTime start;
	private Set<Seat> seats;
	
	public Ticket(String source, String target, DateTime start, Set<Seat> seats) {
		this.source = source;
		this.target = target;
		this.start = start;
		this.seats = seats;
	}

	/**
	 * @return the start time
	 */
	public DateTime getStartTime() {
		return start;
	}
	
	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @return the target
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * @return the seats
	 */
	public Set<Seat> getSeats() {
		return seats;
	}

	@Override
	public String toString() {
		return source + " - " + target + " | " + seats;
	}
}
