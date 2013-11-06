package data.trainnetwork;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * A class representing a Seat within section
 * @author Balazs Pete
 *
 */
public class Seat {
	
	private String id;
	
	/**
	 * Create a new Seat
	 * @param id
	 */
	public Seat() {
		this.id = new BigInteger(130, new SecureRandom()).toString(32);
	}
	
	/**
	 * Get the ID of the Seat
	 * @return
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Compare another seat with this one
	 * @param seat The other seat to compare with
	 * @return True if the two seats are identical
	 */
	public boolean equals(Seat seat) {
		return id.equals(seat.getId());
	}
}