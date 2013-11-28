package data.trainnetwork;

import java.util.LinkedList;

import org.json.simple.*;

import util.JSONTools;

import data.MissingParameterException;

/**
 * An object describing a train route
 * @author Balazs Pete
 *
 */
public class Route extends LinkedList<SectionInfo> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2823284930510141078L;
	
	private String id, company = null;
	private int maxPassengers = -1;
	
	public Route(String id, String company) {
		this.id = id;
		this.company = company;
	}
	
	/**
	 * Get the ID of the route
	 * @return The ID
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Set the maximum number of passengers a train can take on this route 
	 * @param max The maximum number of passengers
	 */
	public void setMaxPassengers(int max) {
		maxPassengers = max;
	}
	
	/**
	 * Get the maximum number of passengers the train can take on this route
	 * @return The maximum number of passengers
	 */
	public int getMaxPassengers() {
		return maxPassengers;
	}
	
	/**
	 * Get the company with which the route is associated with
	 * @return The name of the company
	 */
	public String getCompany() {
		return company;
	}
	
	/**
	 * Create a {@link Route} from a {@link JSONObject}
	 * @param object The {@link JSONObject} containing the route information 
	 * @return The created {@link Route}
	 * @throws MissingParameterException Thrown in case a required parameter is not defined
	 */
	public static Route getRouteFromJSON(JSONObject object) throws MissingParameterException {
		String routeID = (String) JSONTools.getParameter(object, "routeID");
		int maxPassengers = (int)(long)(Long) JSONTools.getParameter(object, "maxPassengers");
		String company = (String) JSONTools.getParameter(object, "company");
		
		Route route = new Route(routeID, company);
		route.setMaxPassengers(maxPassengers);
		
		int count = 0;
		JSONArray rawSections = (JSONArray) JSONTools.getParameter(object, "route");
		for(Object entry : rawSections) {
			JSONObject rawSection = (JSONObject) entry;
			
			Section section = Section.getSectionFromJSON(rawSection, routeID, count++);
			section.setMaxPassengers(maxPassengers);
			
			route.add(
				new SectionInfo(
					section,
					(String) JSONTools.getParameter(rawSection, "start"), 
					(String) JSONTools.getParameter(rawSection, "end")));
		}

		return route;
	}
}
