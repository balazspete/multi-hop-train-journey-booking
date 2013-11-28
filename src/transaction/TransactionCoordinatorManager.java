package transaction;

import java.util.HashMap;

/**
 * An object to manage {@link TransactionCoordinator}s
 * @author Balazs Pete
 *
 * @param <KEY>
 * @param <VALUE>
 */
public class TransactionCoordinatorManager<KEY, VALUE, RETURN> extends HashMap<String, TransactionCoordinator<KEY, VALUE, RETURN>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2367429176914475103L;

	public TransactionCoordinatorManager() {
		super();
	}
	
	// TODO periodically scan Coordinators and discard the ones in the DEAD stage
}
