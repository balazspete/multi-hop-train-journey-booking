package data.trainnetwork;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.joda.time.LocalTime;
import org.json.simple.JSONObject;

import util.JSONTools;

import data.MissingParameterException;

/**
 * An {@link Edge} representing a path between two train {@link Stations} being part of a train line 
 * @author Balazs Pete
 *
 */
public class Section extends DefaultWeightedEdge {

	public enum ScoreMode {
		TravelTime, Cost, NumberOfHops 
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 116967749447316838L;
	
	public static ScoreMode scoreMode = ScoreMode.TravelTime; 
	
	private String routeID;
	private LocalTime startTime;
	private long journeyLength;
	private int cost;
	private int maxPassengers;

	/**
	 * Create a new instance of {@link Section}
	 * @param routeID The ID of the train route the 
	 * @param startTime The time at which the train leaves the station
	 * @param journeyLength The length of the journey on the {@link Section}
	 */
	public Section(String routeID, LocalTime startTime, long journeyLength) {
		this.routeID = routeID;
		this.startTime = startTime;
		this.journeyLength = journeyLength;
	}
	
	/**
	 * Create a new instance of {@link Section}
	 * @param routeID The ID of the train route the
	 * @param startTime The time at which the train leaves the station
	 * @param journeyLength The length of the journey on the {@link Section}
	 */
	public Section(String routeID, LocalTime startTime, long journeyLength, int cost) {
		this.routeID = routeID;
		this.startTime = startTime;
		this.journeyLength = journeyLength;
		this.cost = cost;
	}
	
	/**
	 * Set the maximum number of passengers the train can take on this section
	 * @param max The maximum number of passengers
	 */
	public void setMaxPassengers(int max) {
		this.maxPassengers =max;
	}
	
	/**
	 * Get the identifier of the route the {@link Section belongs to}
	 * @return The ID of the route
	 */
	public String getRouteID() {
		return routeID;
	}
	
	/**
	 * Get the time at which the train leaves the station
	 * @return The {@link LocalTime} of departure
	 */
	public LocalTime getStartTime() {
		return startTime;
	}
	
	/**
	 * Get the length of journey (in seconds) for the section
	 * @return The length of time in seconds
	 */
	public long getJourneyLength() {
		return journeyLength;
	}
	
	/**
	 * Get the maximum number of passengers for the section
	 * @return The maximum number of passengers
	 */
	public int getMaxPassengers() {
		return maxPassengers;
	}
	
	/**
	 * Create a {@link Section} from a corresponding {@link JSONObject}
	 * @param rawSection The JSON data for the section
	 * @param routeID The ID of the associated route
	 * @return The newly created {@link Section}
	 * @throws IllegalArgumentException Thrown if an argument within the JSON is of incorrect type
	 * @throws MissingParameterException Thrown if a required argument is not defined in the JSON
	 */
	public static Section getSectionFromJSON(JSONObject rawSection, String routeID) throws IllegalArgumentException, MissingParameterException {
		Object time = JSONTools.getParameter(rawSection, "time");
		Object length = JSONTools.getParameter(rawSection, "length");
		Object cost = JSONTools.getParameter(rawSection, "cost");
		
		return new Section(
			routeID,
			new LocalTime(time), 
			(Long) length,
			(int)(long)(Long) cost);
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Section)) return false;
		
		Section s = (Section) o;
		return routeID.equalsIgnoreCase(s.getRouteID()) &&
				startTime.equals(s.getStartTime()) &&
				journeyLength == s.getJourneyLength();
	}
	
	@Override
	protected double getWeight() {
		switch(scoreMode) {
			case Cost: return journeyLength * cost;
			default: return journeyLength;
		}
	}
	
	@Override
	public String toString() {
		return "<" + routeID + " >";
	}
}
