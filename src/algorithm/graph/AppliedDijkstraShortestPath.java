package algorithm.graph;

import java.util.*;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;

import data.trainnetwork.*;

/**
 * An implementation of Dijkstra's shortest path algorithm targeted to the {@link Network} data structure
 * @author Balazs Pete
 *
 */
public final class AppliedDijkstraShortestPath
{
	private Network network;
	private Station source, target;
	private DateTime time;
	
	private Map<Station, Section> reversePath = new HashMap<Station, Section>();
	private List<Section> path = new LinkedList<Section>();
	
	/**
	 * @param network The network
	 * @param source The source {@link Station}
	 * @param target The target {@link Station}
	 */
	public AppliedDijkstraShortestPath(Network network, Station source, Station target) {
		this(network, source, target, (new LocalTime(0)).toDateTimeToday());
	}
	
	/**
	 * @param network The network
	 * @param source The source {@link Station}
	 * @param target The target {@link Station}
	 * @param time The start time at the source
	 */
	public AppliedDijkstraShortestPath(Network network, Station source, Station target, DateTime time) {
		this.network = network;
		this.source = source;
		this.target = target;
		this.time = time;

		search();
		createPath();
	}
	
	/**
	 * Get the path, the list of {@link Section}, from source to target
	 * @return The path
	 */
	public List<Section> getPath() {
		return path;
	}
	
	private void search() {
		if (Section.scoreMode == Section.ScoreMode.TravelTime) {
			timeBasedSearch();
		} else if (Section.scoreMode == Section.ScoreMode.NumberOfHops) {
			double temp = Section.DIFFERENT_ROUTE_MULTIPLICATOR;
			Section.DIFFERENT_ROUTE_MULTIPLICATOR = Double.POSITIVE_INFINITY;
			timeBasedSearch();
			Section.DIFFERENT_ROUTE_MULTIPLICATOR = temp;
		} else {
			costBasedSearch();
		}
	}
	
	private void timeBasedSearch() {
		Map<Station, Double> distances = new HashMap<Station, Double>();
		Map<Station, DateTime> times = new HashMap<Station, DateTime>();
		
		times.put(source, time);
		
		List<Station> nodeList = new LinkedList<Station>();
		nodeList.add(source);
		
		while(!nodeList.isEmpty()) {
			Station current = getClosestInTime(nodeList, times);
			DateTime currentTime = times.get(current);
			
			for(Section section : network.outgoingEdgesOf(current)) {
				if (section.isAvailable(currentTime)) {
					continue;
				}

				Station target = network.getEdgeTarget(section);

				DateTime previousTime = times.get(target);
				DateTime newTime = section.getStartTime().plusSeconds((int) section.getJourneyLength());

				if(previousTime == null || newTime.isBefore(previousTime)) {
					reversePath.put(target, section);
					nodeList.add(target);
					times.put(target, newTime);
				}
			}
			
			nodeList.remove(current);
		}
	}
	
	private static Station getClosestInTime(List<Station> Q, Map<Station, DateTime> dist) {
		DateTime minimum = null;
		Station result = null;
		for(Station s : Q) {
			DateTime temp = dist.get(s);
			if(temp != null && (minimum == null || (temp.isBefore(minimum) || temp.isEqual(minimum)))) {
				result = s;
				minimum = temp;
			}
		}
		
		return result;
	}
	
	private void costBasedSearch() {
		Map<Station, Double> distances = new HashMap<Station, Double>();
		Map<Station, DateTime> times = new HashMap<Station, DateTime>();
		
		distances.put(source, 0.0);
		times.put(source, time);
		
		List<Station> nodeList = new LinkedList<Station>();
		nodeList.add(source);
		
		while(!nodeList.isEmpty()) {
			Station current = getMinimumDistance(nodeList, distances);
			DateTime currentTime = times.get(current);
			Section previous = reversePath.get(current);
			double myDistance = distances.get(current);
			
			for(Section section : network.outgoingEdgesOf(current)) {
				if (section.isAvailable(currentTime)) {
					continue;
				}

				Station other = network.getEdgeTarget(section);
				DateTime otherTime = times.get(other);

				Double distance = distances.get(other);
				if(distance == null) {
					distance = Double.POSITIVE_INFINITY;
				}

				double newDistance = myDistance + section.getWeight(previous);
				DateTime newTime = section.getStartTime().plusSeconds((int) section.getJourneyLength());

				if(newDistance < distance || (newDistance == distance && otherTime.isAfter(newTime))) {
					distances.put(other, newDistance);
					reversePath.put(other, section);
					nodeList.add(other);
					times.put(other, newTime);
				}
			}
			
			nodeList.remove(current);
		}
	}
	
	private static Station getMinimumDistance(List<Station> Q, Map<Station, Double> dist) {
		double minimum = Double.POSITIVE_INFINITY;
		Station result = null;
		for(Station s : Q) {
			Double temp = dist.get(s);
			if(temp != null && temp <= minimum) {
				result = s;
				minimum = temp;
			}
		}
		
		return result;
	}
	
	private void createPath() {
		Station s = target;
		while(reversePath.get(s) != null) {
			Section temp = reversePath.get(s);
			s = network.getEdgeSource(temp);
			if (temp != null) {
				path.add(0, temp);
			}
		}
	}
}

