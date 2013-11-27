package data.trainnetwork;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * A class representing a Seat within section
 * @author Balazs Pete
 *
 */
public class Seat implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3197202742981022435L;
	
	private String id, sectionId = null;
	
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
	 * Add the corresponding {@link Section} (will be stored as an ID reference)
	 * @param section The section the seat belongs to
	 */
	public void addSection(Section section) {
		sectionId = section.getID();
	}
	
	/**
	 * Get the ID of the corresponding {@link Section}
	 * @return The id of the section
	 */
	public String getSectionId() {
		return sectionId;
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