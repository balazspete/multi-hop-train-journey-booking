package data.request;

import data.system.*;

/**
 * A {@link DataRequest} for {@link NodeInfo}rmations
 * @author Balazs Pete
 *
 */
public class ClusterInfoRequest extends DataRequest<NodeInfo> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3239902312940920491L;

	@Override
	public boolean isRequested(NodeInfo dataEntry) {
		// Request information regarding all nodes within the system
		return true;
	}
}
