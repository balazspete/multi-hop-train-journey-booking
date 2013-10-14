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
		System.out.println(time.toString());
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
		Map<Station, Double> distances = new HashMap<Station, Double>();
		Map<Station, DateTime> times = new HashMap<Station, DateTime>();
		
		distances.put(source, 0.0);
		times.put(source, time);
		
		List<Station> nodeList = new LinkedList<Station>();
		nodeList.add(source);
		
		while(!nodeList.isEmpty()) {
			Station current = getMinimumDistance(nodeList, distances);
			DateTime currentTime = times.get(current);
			double myDistance = distances.get(current);
			Section previous = reversePath.get(current);
			
			for(Section section : network.outgoingEdgesOf(current)) {
				DateTime _time = section.getStartTime().toDateTimeToday();
				if (_time.isBefore(currentTime)) {
					_time.plusDays(1);
				}
				
				Station other = network.getEdgeTarget(section);
				
				Double distance = distances.get(other);
				if(distance == null) {
					distance = Double.POSITIVE_INFINITY;
				}
				
				double newDistance = 0;
				double weight = section.getWeight(previous);
				if(Section.scoreMode == Section.ScoreMode.TravelTime) {
					weight = _time.getMillis() + weight;
							//+ weight;
					System.out.println(weight);
					myDistance = weight;
				} else {
					newDistance = myDistance + weight;
				}
				
				if(newDistance < distance) {
					distances.put(other, newDistance);
					reversePath.put(other, section);
					nodeList.add(other);
					times.put(other, 
							_time.plusSeconds((int) section.getJourneyLength()));
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

