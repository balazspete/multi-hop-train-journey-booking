package data.trainnetwork;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import util.JSONTools;

import data.MissingParameterException;

public class Route extends LinkedList<SectionInfo> {
	
	private String id;
	private int maxPassengers = -1;
	
	public Route(String id) {
		this.id = id;
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
	 * Create a {@link Route} from a {@link JSONObject}
	 * @param object The {@link JSONObject} containing the route information 
	 * @return The created {@link Route}
	 * @throws MissingParameterException Thrown in case a required parameter is not defined
	 */
	public static Route getRouteFromJSON(JSONObject object) throws MissingParameterException {
		String routeID = (String) JSONTools.getParameter(object, "routeID");
		int maxPassengers = (int)(long)(Long) JSONTools.getParameter(object, "maxPassengers");
		
		Route route = new Route(routeID);
		route.setMaxPassengers(maxPassengers);
		
		JSONArray rawSections = (JSONArray) JSONTools.getParameter(object, "route");
		for(Object entry : rawSections) {
			JSONObject rawSection = (JSONObject) entry;
			
			Section section = Section.getSectionFromJSON(rawSection, routeID);
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
