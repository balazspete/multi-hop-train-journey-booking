package data.system;

import java.util.HashMap;
import java.util.Set;

import org.json.simple.*;

/**
 * An object containing connection information regarding a system node
 * @author Balazs Pete
 *
 */
public class RouteToNodeMap extends HashMap<String, ClusterInfo> {

	private static final long serialVersionUID = -7866356790774985919L;
	
	public void addClusterInfo(ClusterInfo nodeInfo) {
		put(nodeInfo.getName(), nodeInfo);
	}
	
	public void addMultipleClusterInfo(Set<ClusterInfo> clusterInfos) {
		for (ClusterInfo info : clusterInfos) {
			addClusterInfo(info);
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
				rtnm.addClusterInfo(ClusterInfo.getFromJSON((JSONObject) entry));
			} catch (Exception e) {
				continue;
			}
		}
		
		return rtnm;
	}
}
