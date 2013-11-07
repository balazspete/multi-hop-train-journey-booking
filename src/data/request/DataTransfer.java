package data.request;

import java.io.Serializable;
import java.util.Set;

/**
 * A wrapper used to transfer DE objects between nodes
 * @author Balazs Pete
 *
 * @param <DE> The class of the objects to be transferred 
 */
public class DataTransfer<DE> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2507569525543901107L;

	private Set<DE> data;
	
	/**
	 * Create a new data transfer instance
	 * @param data The data set to transfer
	 */
	public DataTransfer(Set<DE> data) {
		this.data = data;
	}
	
	/**
	 * Get the transferred data set
	 * @return The data set
	 */
	public Set<DE> getData() {
		return data;
	}
}
