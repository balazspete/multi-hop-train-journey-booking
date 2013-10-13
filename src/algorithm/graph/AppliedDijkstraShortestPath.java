package algorithm.graph;

import java.util.*;

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
	private LocalTime time;
	
	private Map<Station, Section> reversePath = new HashMap<Station, Section>();
	private List<Section> path = new LinkedList<Section>();
	
	/**
	 * @param network The network
	 * @param source The source {@link Station}
	 * @param target The target {@link Station}
	 */
	public AppliedDijkstraShortestPath(Network network, Station source, Station target) {
		this(network, source, target, new LocalTime(0));
	}
	
	/**
	 * @param network The network
	 * @param source The source {@link Station}
	 * @param target The target {@link Station}
	 * @param time The start time at the source
	 */
	public AppliedDijkstraShortestPath(Network network, Station source, Station target, LocalTime time) {
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
		Map<Station, Double> distances = new HashMap<Station, Double>();
		Map<Station, LocalTime> times = new HashMap<Station, LocalTime>();
		
		distances.put(source, 0.0);
		times.put(source, time);
		
		List<Station> nodeList = new LinkedList<Station>();
		nodeList.add(source);
		
		while(!nodeList.isEmpty()) {
			Station current = getMinimumDistance(nodeList, distances);
			LocalTime currentTime = times.get(current);
			double myDistance = distances.get(current);
			Section previous = reversePath.get(current);
			
			for(Section section : network.outgoingEdgesOf(current)) {
				if (section.getStartTime().isBefore(currentTime)) {
					continue;
				}
				
				Station other = network.getEdgeTarget(section);
				
				Double distance = distances.get(other);
				if(distance == null) {
					distance = Double.POSITIVE_INFINITY;
				}
				
				double newDistance = myDistance + section.getWeight(previous);
				
				if(newDistance < distance) {
					distances.put(other, newDistance);
					reversePath.put(other, section);
					nodeList.add(other);
					times.put(other, section.getStartTime().plusSeconds((int) section.getJourneyLength()));
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

