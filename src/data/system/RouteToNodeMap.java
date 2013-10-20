package data.system;

import java.util.HashMap;
import java.util.Set;

import org.json.simple.*;

/**
 * An object containing connection information regarding a system node
 * @author Balazs Pete
 *
 */
public class RouteToNodeMap extends HashMap<String, NodeInfo> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7866356790774985919L;
	
	public void addNodeInfo(NodeInfo nodeInfo) {
		put(nodeInfo.getName(), nodeInfo);
	}
	
	public void addMultipleNodeInfo(Set<NodeInfo> nodeInfos) {
		for (NodeInfo info : nodeInfos) {
			addNodeInfo(info);
		}
	}
	
	/**
	 * Get a Route to node mapping from a list of raw NodeInfo
	 * @param list The {@link JSONArray} of raw {@link NodeInfo}
	 * @return The newly created {@link RouteToNodeMap}
	 */
	public static RouteToNodeMap getRoutToNodeMapFromJSON(JSONArray list) {
		RouteToNodeMap rtnm = new RouteToNodeMap();
		
		for (Object entry : list) {
			try {
				rtnm.addNodeInfo(NodeInfo.getFromJSON((JSONObject) entry));
			} catch (Exception e) {
				continue;
			}
		}
		
		return rtnm;
	}
}
