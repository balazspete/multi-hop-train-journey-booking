package transaction;

import java.util.HashMap;

/**
 * An object to manage {@link TransactionCoordinator}s
 * @author Balazs Pete
 *
 * @param <KEY>
 * @param <VALUE>
 */
public class TransactionCoordinatorManager<KEY, VALUE> extends HashMap<String, TransactionCoordinator<KEY, VALUE>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2367429176914475103L;

	public TransactionCoordinatorManager() {
		super();
	}
}
