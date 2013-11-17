package data.system;

import java.util.HashSet;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import util.JSONTools;
import data.MissingParameterException;

/**
 * A {@link NodeInfo} object which supports multiple addresses for a node type
 * @author Balazs Pete
 *
 */
public class ClusterInfo extends NodeInfo {

	private Set<String> locations;
	
	/**
	 * Create a instance of {@link NodeInfo}
	 * @param name The name of the node
	 */
	public ClusterInfo(String name) {
		super(name);
		locations = new HashSet<String>();
	}
	
	
	/**
	 * Add a location for a node 
	 * @param location A URI or IP address
	 */
	public void addLocation(String location) {
		locations.add(location.intern());
	}
	
	/**
	 * Get a location associated with the node
	 * @return A location (URI or IP address)
	 */
	public String getLocation() {
		return getRandomLocation();
	}
	
	/**
	 * Create a {@link NodeInfo} from a {@link JSONObject}
	 * @param object The input {@link JSONObject} containing the node information
	 * @return The newly created NodeInfo object
	 * @throws MissingParameterException Thrown if the input object does not contain the `name` and `locations` parameter
	 */
	public static ClusterInfo getFromJSON(JSONObject object) throws MissingParameterException {
		String name = (String) JSONTools.getParameter(object, "name");
		JSONArray locations = (JSONArray) JSONTools.getParameter(object, "locations");
		
		ClusterInfo nodeInfo = new ClusterInfo(name);
		for (Object location : locations) {
			nodeInfo.addLocation((String) location);
		}
		
		return nodeInfo;
	}

	private String getRandomLocation() {
		int size = locations.size();
		int randomIndex = (int) Math.random() * size;
		
		return (String) locations.toArray()[randomIndex];
	}
}
