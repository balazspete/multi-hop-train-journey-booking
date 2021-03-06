package data.trainnetwork;

import java.io.*;
import java.util.*;

import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.json.simple.*;
import org.json.simple.parser.*;

import algorithm.graph.AppliedDijkstraShortestPath;

import util.JSONTools;

import data.MissingParameterException;

/**
 * A {@link DefaultDirectedWeightedGraph} representing the Train network
 * @author Balazs Pete
 *
 */
public class Network extends DirectedWeightedMultigraph<Station, Section> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4239399173964630465L;
	
	Map<String, Station> stationMap;
	
	/**
	 * Create a new instance of {@link Network} with a given {@link EdgeFactory}
	 * @param edgeFactory {@link EdgeFactory}
	 */
	public Network() {
		super(new ClassBasedEdgeFactory<Station, Section>(Section.class));
		stationMap = new LinkedHashMap<String, Station>();
	}

	@Override
	public boolean addVertex(Station vertex) {
		boolean result = super.addVertex(vertex);
		if(result) stationMap.put(vertex.getID(), vertex);
		return result;
	}
	
	/**
	 * Add a {@link Collection} of {@link Station}s to the Network
	 * @param vertices The {@link Station}s to add to the network
	 * @return True if all {@link Station}s have been added 
	 */
	public boolean addVertices(Collection<Station> vertices) {
		boolean result = true;
		for(Station station : vertices) {
			result = result && addVertex(station);
		}
		return result;
	}
	
	@Override
	public boolean addEdge(Station source, Station target, Section section) {
		if(containsEdge(section)) {
			removeEdge(section);
		}
		
		return super.addEdge(source, target, section);
	}
	
	/**
	 * Add a {@link Collection} of {@link Routes} to the {@link Network}
	 * @param stations The {@link Stations} lookup {@link Map}
	 * @param routes The {@link Collection} of {@link Stations} to add
	 * @return True if all {@link Section}s in all {@link Routes} have been added to the {@link Network}, false otherwise
	 */
	public boolean addRoutes(Map<String, Station> stations, Collection<Route> routes) {
		boolean result = true;
		for(Route route : routes) {
			for(SectionInfo si : route) {
				Station from = stations.get(si.getStartStationID());
				Station to = stations.get(si.getEndStationID());
				
				if(from == null || to == null) {
					System.out.println("======\nError\n" + si.getStartStationID() + "\n" + si.getEndStationID() );
					continue;
				}
				
				result = result && addEdge(from, to, si.getSection());
			}
		}
		return result;
	}
	
	/**
	 * Create a {@link Network} based on an input JSON
	 * @param object The JSON object
	 * @return The created {@link Network}
	 * @throws IllegalArgumentException Thrown if one of the arguments in the JSON are of illegal type
	 * @throws MissingParameterException Thrown if one of the required parameters is not defined for an element
	 */
	public static Network getNetworkFromJSON(JSONObject object) throws IllegalArgumentException, MissingParameterException {
		Network network = new Network();
		
		JSONArray rawStations = (JSONArray) JSONTools.getParameter(object, "stations");
		network.createStations(rawStations);
		
		JSONArray rawRoutes = (JSONArray) JSONTools.getParameter(object, "routes");
		network.createSections(rawRoutes);
		
		return network;
	}
	
	/**
	 * Get a {@link Station} from its ID or null if no such station is in the network
	 * @param stationID The station's ID
	 * @return The station or null
	 */
	public Station getStation(String stationID) {
		return stationMap.get(stationID);
	}
	
	/**
	 * Add {@link Station}s to the {@link Network} from a {@link JSONArray}
	 * @param stationsData The {@link JSONArray} containing the list of {@link Station}s
	 * @return True if all stations within stationsData has been added, false otherwise
	 */
	public boolean createStations(JSONArray stationsData) {
		Map<String, Station> stations;
		try {
			stations = getStations(stationsData);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return addVertices(stations.values());
	}
	
	/**
	 * Add {@link Section}s to the {@link Network} from a {@link JSONArray} containing {@link Route} information
	 * @param routesData The {@link JSONArray} containing a list of {@link Routes}
	 * @return True if all {@link Section}s have been added correctly
	 */
	public boolean createSections(JSONArray routesData) {
		Vector<Route> routes;
		try {
			routes = getRoutes(routesData);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return addRoutes(stationMap, routes);
	}
	
	public void update(Set<Station> stations, Set<SectionInfo> sections) {
		for (Station s : stations) {
			addVertex(s);
		}
		
		for (SectionInfo s : sections) {
			addEdge(
				getStation(s.getStartStationID()), 
				getStation(s.getEndStationID()), 
				s.getSection());
		}
	}
	
	private static Map<String, Station> getStations(JSONArray stationsData) throws IllegalArgumentException, MissingParameterException {
		Map<String, Station> stations = new HashMap<String, Station>();
		for (Object entry : stationsData) {
			JSONObject object = (JSONObject) entry;
			Station s = Station.getStationFromJSON(object);
			stations.put(s.getID(), s);
		}
		return stations;
	}
	
	private static Vector<Route> getRoutes(JSONArray routesData) throws IllegalArgumentException, MissingParameterException {
		Vector<Route> routes = new Vector<Route>();
		for (Object entry : routesData) {
			JSONObject object = (JSONObject) entry;
			Route r = Route.getRouteFromJSON(object);
			routes.add(r);
		}
		return routes;
	}
	
	public static void main(String[] args) {
		JSONParser parser = new JSONParser();
		Object obj;
		Network network = null;
		
		try {
			obj = parser.parse(new FileReader("/Users/balazspete/Projects/multi-hop-train-booking/out.json"));
			network = getNetworkFromJSON((JSONObject) obj);
			
//			JGraph jgraph = new JGraph(new JGraphModelAdapter(network));
//			JFrame frame = new JFrame();
//			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//			frame.pack();
//			frame.add(jgraph);
//			frame.setVisible(true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (MissingParameterException e) {
			e.printStackTrace();
		}
		
		Section.scoreMode = Section.ScoreMode.TravelTime;
		Station o = network.getStation("GALWY");
		Station t = network.getStation("CORKS");
		
		System.out.println(o.getName() + " - " + t.getName());
		
		AppliedDijkstraShortestPath dijkstra = new AppliedDijkstraShortestPath(network, o, t);
		System.out.println(network.getEdgeSource(dijkstra.getPath().get(0)));
		for(Section s : dijkstra.getPath()) {
			System.out.println(network.getEdgeTarget(s) + " - " + s.getStartTime().toString());
		}
		
		
	}
	
	

}
