package node.central;

import java.io.*;
import java.util.*;

import org.json.simple.*;
import org.json.simple.parser.*;

import communication.protocols.*;

import data.MissingParameterException;
import data.system.NodeInfo;
import data.trainnetwork.*;

/**
 * A {@link StaticDataRepository} serving raw static data
 * @author Balazs Pete
 *
 */
public class MasterDataRepository extends StaticDataRepository {

	private static final String 
		ROUTES_DATA = "/Users/balazspete/Projects/multi-hop-train-booking/compiled_routes.json", 
		STATIONS_DATA = "/Users/balazspete/Projects/multi-hop-train-booking/stations.json", 
		NODES_INFO = "/Users/balazspete/Projects/multi-hop-train-booking/nodes.json";
	
	/**
	 * Create a new {@link MasterDataRepository}
	 */
	public MasterDataRepository() {
		// TODO load port# from config
		super(8000);
		update();
	}
	
	@Override
	protected void initialize() {
		sections = new HashSet<SectionInfo>();
		stations = new HashSet<Station>();
		nodes = new HashSet<NodeInfo>();
	}
	
	/**
	 * Update the repository from the source JSON files
	 */
	@SuppressWarnings("unchecked")
	public void update() {
		JSONParser parser = new JSONParser();
		
		try {
			JSONArray _stations = (JSONArray) parser.parse(new FileReader(STATIONS_DATA));
			for (Object _station : _stations) {
				Station station = Station.getStationFromJSON((JSONObject) _station);
				stations.add(station);
			}

			JSONArray routes = (JSONArray) parser.parse(new FileReader(ROUTES_DATA));
			for (Object _route : routes) {
				Route route = Route.getRouteFromJSON((JSONObject) _route);
				for (SectionInfo info : route) {
					sections.add(info);
				}
			}
			
			JSONArray nodes = (JSONArray) parser.parse(new FileReader(NODES_INFO));
			for (Object _node : nodes) {
				NodeInfo node = NodeInfo.getFromJSON((JSONObject) _node);
				nodes.add(node);
			}
			
		// TODO add some failure handling
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (MissingParameterException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Set<Protocol> getProtocols() {
		Set<Protocol> protocols = new HashSet<Protocol>();
		protocols.add(new DataRequestHandlingProtocol<Station>(stations, "Station"));
		protocols.add(new DataRequestHandlingProtocol<SectionInfo>(sections, "SectionInfo"));
		protocols.add(new DataRequestHandlingProtocol<NodeInfo>(nodes , "NodeInfo"));
		
		return protocols;
	}

	public void test() {
		System.out.println(sections.size());
		System.out.println(stations.size());
	}
	
	public static void main(String[] args) {
		MasterDataRepository repo = new MasterDataRepository();
		repo.start();
		repo.test();
	}
}
