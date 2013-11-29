package data.trainnetwork;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.joda.time.DateTime;
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
	
	public enum Status {
		AVAILABLE, FULL
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 116967749447316838L;
	
	public static ScoreMode scoreMode = ScoreMode.TravelTime; 
	public static double DIFFERENT_ROUTE_MULTIPLICATOR = 2;
	
	protected String routeID;
	protected DateTime startTime;
	protected long journeyLength;
	protected int cost, maxPassengers, sectionNumber;
	/**
	 * @return the cost
	 */
	public int getCost() {
		return cost;
	}

	protected Status status;

	/**
	 * Create a new instance of {@link Section}
	 * @param routeID The ID of the train route the 
	 * @param startTime The time at which the train leaves the station
	 * @param journeyLength The length of the journey on the {@link Section}
	 */
	public Section(String routeID, int sectionNumber, DateTime startTime, long journeyLength) {
		this(routeID, sectionNumber, startTime, journeyLength, 0);
	}
	
	/**
	 * Create a new instance of {@link Section}
	 * @param routeID The ID of the train route the
	 * @param startTime The time at which the train leaves the station
	 * @param journeyLength The length of the journey on the {@link Section}
	 */
	public Section(String routeID, int sectionNumber, DateTime startTime, long journeyLength, int cost) {
		this(routeID, sectionNumber, startTime, journeyLength, cost, Status.AVAILABLE);
	}
	
	/**
	 * Create a new instance of {@link Section}
	 * @param routeID The ID of the train route the
	 * @param startTime The time at which the train leaves the station
	 * @param journeyLength The length of the journey on the {@link Section}
	 * @param status The availability status of the section
	 */
	public Section(String routeID, int sectionNumber, DateTime startTime, long journeyLength, int cost, Status status) {
		this.routeID = routeID;
		this.sectionNumber = sectionNumber;
		this.startTime = startTime;
		this.journeyLength = journeyLength;
		this.cost = cost;
		this.status = status;
		this.setStatus(status);
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
	 * @return The {@link DateTime} of departure
	 */
	public DateTime getStartTime() {
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
	 * @param sectionNumber The position of theSection within the train route
	 * @return The newly created {@link Section}
	 * @throws IllegalArgumentException Thrown if an argument within the JSON is of incorrect type
	 * @throws MissingParameterException Thrown if a required argument is not defined in the JSON
	 */
	public static Section getSectionFromJSON(JSONObject rawSection, String routeID, int sectionNumber) throws IllegalArgumentException, MissingParameterException {
		return getSectionFromJSON(rawSection, routeID, sectionNumber, (new LocalTime(0)).toDateTimeToday());
	}
	
	/**
	 * Create a {@link Section} from a corresponding {@link JSONObject}
	 * @param rawSection The JSON data for the section
	 * @param routeID The ID of the associated route
	 * @param sectionNumber The position of the section within the train route
	 * @param baseTime {@link DateTime} specifying the date for the section's time
	 * @return The newly created {@link Section}
	 * @throws IllegalArgumentException Thrown if an argument within the JSON is of incorrect type
	 * @throws MissingParameterException Thrown if a required argument is not defined in the JSON
	 */
	public static Section getSectionFromJSON(JSONObject rawSection, String routeID, int sectionNumber, DateTime baseTime) throws IllegalArgumentException, MissingParameterException {
		Object time = JSONTools.getParameter(rawSection, "time");
		Object length = JSONTools.getParameter(rawSection, "length");
		Object cost = JSONTools.getParameter(rawSection, "cost");
		Status status = Status.AVAILABLE;
		try {
			String _status = (String) JSONTools.getParameter(rawSection, "status");
			if(_status.equalsIgnoreCase("full")) {
				status = Status.FULL;
			}
		} catch(MissingParameterException e) {
		}
		
		return new Section(
			routeID,
			sectionNumber,
			new DateTime(time), 
			(Long) length,
			(int)(long)(Long) cost,
			status);
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
			case Cost: return cost;
			default: return journeyLength;
		}
	}
	
	public double getWeight(Section other) {
		if (other != null && isOfSameRoute(other)) {
			return getWeight();
		} else {
			return getWeight() * DIFFERENT_ROUTE_MULTIPLICATOR;
		}
	}
	
	public boolean isOfSameRoute(Section other) {
		return other.getRouteID().equalsIgnoreCase(routeID);
	}
	
	@Override
	public String toString() {
		return "" + routeID + "=" + sectionNumber + "=" + maxPassengers + "=" + cost + "=" + journeyLength + "=" + startTime.toString();
	}

	/**
	 * Get the {@link Status} of the {@link Section}
	 * @return The current status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * Set the {@link Status} of the section
	 * @param status The new status
	 */
	public void setStatus(Status status) {
		this.status = status;
	}
	
	/**
	 * Check against the current time whether the section is available to book
	 * @return True if the section can be booked, false otherwise
	 */
	public boolean isAvailable() {
		return isAvailable(DateTime.now());
	}
	
	/**
	 * Check against an input {@link DateTime} whether the section is available to book
	 * @param time The {@link DateTime} to check against
	 * @return true if the section can be booked, false otherwise
	 */
	public boolean isAvailable(DateTime time) {
		return time.isAfter(startTime) && status == Status.AVAILABLE;
	}
	
	/**
	 * Get the unique ID of the section
	 * @return The ID
	 */
	public String getID() {
		return "" + routeID + "=" + sectionNumber + "=" + maxPassengers + "=" + startTime.toString();
	}
	
	/**
	 * Get the ID of the class of the section
	 * @param id The id of the section
	 * @return The ID
	 */
	public static String getRouteIDFromID(String id) {
		return id.split("=")[0];
	}
	
	/**
	 * Get a section from its ID
	 * @param id The ID of the section
	 * @return The section
	 */
	public static Section getSectionFromId(String id) {
		throw new UnsupportedOperationException();
	}
	
	@Deprecated
	public static void main(String[] args) {
		System.out.println(getRouteIDFromID("fwfwe=cewcwef=vwv"));
	}
}
