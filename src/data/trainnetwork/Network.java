package data.trainnetwork;

import java.io.*;
import java.util.*;

import javax.swing.JFrame;

import org.jgraph.JGraph;
import org.jgrapht.*;
import org.jgrapht.ext.*;
import org.jgrapht.graph.*;
import org.json.simple.*;
import org.json.simple.parser.*;

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

	/**
	 * Create a new instance of {@link Network} with a given {@link EdgeFactory}
	 * @param edgeFactory {@link EdgeFactory}
	 */
	public Network(EdgeFactory<Station, Section> edgeFactory) {
		super(edgeFactory);
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
				
				if(from == null || to == null) continue;
				
				result = result && addEdge(from, to, si.getSection());
			}
		}
		return true;
	}
	
	/**
	 * Create a {@link Network} based on an input JSON
	 * @param object The JSON object
	 * @return The created {@link Network}
	 * @throws IllegalArgumentException Thrown if one of the arguments in the JSON are of illegal type
	 * @throws MissingParameterException Thrown if one of the required parameters is not defined for an element
	 */
	public static Network getNetworkFromJSON(JSONObject object) throws IllegalArgumentException, MissingParameterException {
		Network network = new Network(new ClassBasedEdgeFactory<Station, Section>(Section.class));
		
		JSONArray rawStations = (JSONArray) JSONTools.getParameter(object, "stations");
		Map<String, Station> stations = getStations(rawStations);
		network.addVertices(stations.values());
		
		JSONArray rawRoutes = (JSONArray) JSONTools.getParameter(object, "routes");
		Vector<Route> routes = getRoutes(rawRoutes);
		network.addRoutes(stations, routes);
		
		return network;
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
		try {
			obj = parser.parse(new FileReader("/Users/balazspete/Desktop/out.json"));
			Network network = getNetworkFromJSON((JSONObject) obj);
			
			JGraph jgraph = new JGraph(new JGraphModelAdapter(network));
			JFrame frame = new JFrame();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.pack();
			frame.add(jgraph);
			frame.setVisible(true);
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
		
	}
}
