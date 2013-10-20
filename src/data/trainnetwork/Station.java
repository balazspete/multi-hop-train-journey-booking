package data.trainnetwork;

import java.io.Serializable;

import org.json.simple.JSONObject;

import util.JSONTools;

import data.MissingParameterException;
import data.graph.Vertex;

/**
 * A {@link Vertex} representing a train {@link Station} in the network
 * @author Balazs Pete
 *
 */
public class Station implements Vertex, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7835704912216857559L;
	
	private String name, id;
	
	/**
	 * Create a new instance of a {@link Station}
	 * @param name The name of the {@link Station}
	 * @param id The {@link Station}'s ID
	 */
	public Station (String name, String id) {
		this.name = name;
		this.id = id;
	}
	
	/**
	 * Get the name of the {@link Station}
	 * @return The name of the {@link Station}
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get the ID of the {@link Station}
	 * @return The ID of the {@link Station}
	 */
	public String getID() {
		return id;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Station)) return false;
		
		Station s = (Station) o;
		return name.equalsIgnoreCase(s.getName()) &&
				id.equalsIgnoreCase(s.getID());
	}
	
	@Override
	public String toString() {
		return "<" + id + "> " + name;
	}
	
	public static Station getStationFromJSON(JSONObject object) throws IllegalArgumentException, MissingParameterException {
		return new Station(
			(String) JSONTools.getParameter(object, "name"),
			(String) JSONTools.getParameter(object, "id")
		);
	}
}
