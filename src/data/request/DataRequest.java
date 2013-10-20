package data.request;

import java.io.Serializable;
import java.util.*;

/**
 * A generic object to help determine the requested data from another party
 * @author Balazs Pete
 *
 * @param <DE> The requested object's class
 */
public abstract class DataRequest<DE> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1341460094288053838L;

	/**
	 * Determine whether the input object is requested
	 * @param dataEntry The input object to check
	 * @return True if the object is requested, false otherwise
	 */
	public abstract boolean isRequested(DE dataEntry);
	
	/**
	 * Filter an input {@link Collection} for the requested entries
	 * @param input The {@link Collection} to filter
	 * @return The filtered {@link Collection}
	 */
	public Collection<DE> filterRequested(Collection<DE> input) {
		HashSet<DE> result = new HashSet<DE>();
		for (DE entry : input) {
			if (isRequested(entry))
				result.add(entry);
		}
		return result;
	}
}
