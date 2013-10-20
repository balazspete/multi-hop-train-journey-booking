package data.system;

import java.util.*;

import org.json.simple.*;

import data.MissingParameterException;

import util.JSONTools;

/**
 * An object containing location information regarding a system node
 * @author Balazs Pete
 *
 */
public class NodeInfo {

	private String name;
	private Set<String> locations;
	
	/**
	 * Create a instance of {@link NodeInfo}
	 * @param name The name of the node
	 */
	public NodeInfo(String name) {
		this.name = name;
		locations = new HashSet<String>();
	}
	
	/**
	 * Get the name of the node
	 * @return The name of the node
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Add a location for a node 
	 * @param location A URI or IP address
	 * @return true if the location has been added, false otherwise
	 */
	public boolean addLocation(String location) {
		return locations.add(location.intern());
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
	public static NodeInfo getFromJSON(JSONObject object) throws MissingParameterException {
		String name = (String) JSONTools.getParameter(object, "name");
		JSONArray locations = (JSONArray) JSONTools.getParameter(object, "locations");
		
		NodeInfo nodeInfo = new NodeInfo(name);
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
