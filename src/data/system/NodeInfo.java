package data.system;

import java.io.Serializable;

/**
 * An object containing location information regarding a system node
 * @author Balazs Pete
 *
 */
public class NodeInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7378201805278309534L;
	private String name, location;
	
	/**
	 * Create a instance of {@link NodeInfo}
	 * @param name The name of the node
	 */
	public NodeInfo(String name) {
		this(name, null);
	}
	
	/**
	 * Create a instance of {@link NodeInfo}
	 * @param name The name of the node
	 * @param location The location (URI or IP address) of the node
	 */
	public NodeInfo(String name, String location) {
		this.name = name;
		this.location = location;
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
	 */
	public void addLocation(String location) {
		this.location = location;
	}
	
	/**
	 * Get a location associated with the node
	 * @return A location (URI or IP address)
	 */
	public String getLocation() {
		return location;
	}
	
	public boolean equals(NodeInfo other) {
		if (other != null && other.location == location) {
			return true;
		} else if (other == null) {
			return false;
		} else {
			return location.equals(other.getLocation());
		}
	}
	
	public String toString() {
		return "<" + name + "|" + location + ">";
	}
}
