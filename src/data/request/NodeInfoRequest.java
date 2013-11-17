package data.request;

import data.system.NodeInfo;

/**
 * A request to retrieve the list of all know nodes except the requester's own one
 * @author Balazs Pete
 *
 */
public class NodeInfoRequest extends DataRequest<NodeInfo> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1621610683148444915L;
	
	private NodeInfo notToGet = null;
	
	/**
	 * Create a {@link NodeInfoRequest} to retrieve all {@link NodeInfo}rmations
	 */
	public NodeInfoRequest() {
		this(null);
	}
	
	/**
	 * Create a {@link NodeInfoRequest} specifying which {@link NodeInfo} not to get
	 * @param notToGet The info to not retrieve (if null then get all)
	 */
	public NodeInfoRequest(NodeInfo notToGet) {
		this.notToGet = notToGet;  
	}
	
	@Override
	public boolean isRequested(NodeInfo dataEntry) {
		if (dataEntry == null || notToGet == null) return true;
		return !dataEntry.equals(notToGet);
	}
}
