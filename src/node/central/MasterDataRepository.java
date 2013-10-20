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
 * A {@link DataRepository} serving raw static data
 * @author Balazs Pete
 *
 */
public class MasterDataRepository extends DataRepository {

	private static final String 
		ROUTES_DATA = "/Users/balazspete/Desktop/routes.json", 
		STATIONS_DATA = "/Users/balazspete/Desktop/stations.json", 
		NODES_INFO = "/Users/balazspete/Desktop/nodes.json";
	
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
	public void update() {
		JSONParser parser = new JSONParser();
		
		try {
			JSONArray stations = (JSONArray) parser.parse(new FileReader(STATIONS_DATA));
			for (Object _station : stations) {
				Station station = Station.getStationFromJSON((JSONObject) _station);
				this.stations.add(station);
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

	public static void main(String[] args) {
		MasterDataRepository repo = new MasterDataRepository();
		repo.start();
	}
}
