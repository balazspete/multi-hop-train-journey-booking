package node.central;

import java.io.*;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.*;

import node.data.RepositoryException;
import node.data.StaticDataLoadException;

import org.json.simple.*;
import org.json.simple.parser.*;

import communication.protocols.*;
import data.MissingParameterException;
import data.system.*;
import data.trainnetwork.*;

/**
 * A {@link StaticDataRepository} serving raw static data
 * @author Balazs Pete
 *
 */
public class MasterDataRepository extends StaticDataRepository {
	
	private static Set<NodeInfo> slaves = new HashSet<NodeInfo>();
	
	private static final String 
		CLUSTER_NAME = "CENTRAL STATIC DATA CLUSTER",
		ROUTES_DATA = "/Users/balazspete/Projects/multi-hop-train-booking/compiled_routes.json", 
		STATIONS_DATA = "/Users/balazspete/Projects/multi-hop-train-booking/stations.json";
	
	/**
	 * Create a new {@link MasterDataRepository}
	 * @throws RepositoryException Thrown if the initialisation failed
	 */
	public MasterDataRepository() throws RepositoryException {
		// TODO load port# from config
		super(8000);
		try {
			update();
		} catch (StaticDataLoadException e) {
			throw new RepositoryException(e.getMessage());
		}
	}
	
	@Override
	protected void initialize() {
		sections = new HashSet<SectionInfo>();
		stations = new HashSet<Station>();
		nodes = new HashSet<NodeInfo>();
		routeToCompanies = new HashSet<RouteToCompany>();
	}
	
	/**
	 * Update the repository from the source JSON files
	 * @throws StaticDataLoadException 
	 */
	@SuppressWarnings("unchecked")
	public void update() throws StaticDataLoadException {
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
			
//			JSONArray nodes = (JSONArray) parser.parse(new FileReader(NODES_INFO));
//			for (Object _node : nodes) {
//				ClusterInfo node = ClusterInfo.getFromJSON((JSONObject) _node);
//				nodes.add(node);
//			}
			
		// Failed to load data, propagate exception to higher level
		} catch (FileNotFoundException e) {
			throw new StaticDataLoadException(e.getMessage());
		} catch (IOException e) {
			throw new StaticDataLoadException(e.getMessage());
		} catch (ParseException e) {
			throw new StaticDataLoadException(e.getMessage());
		} catch (MissingParameterException e) {
			throw new StaticDataLoadException(e.getMessage());
		}
	}

	@Override
	protected Set<Protocol> getProtocols() {
		Set<Protocol> protocols = new HashSet<Protocol>();
		
		// Accept and handle `Hello` requests from slaves
		protocols.add(new HelloProtocol(slaves));
		
		// Accept Cluster hello requests from clients
		protocols.add(new ClusterHelloProtocol(CLUSTER_NAME, slaves));
		
		// Accept and handle static data requests
		protocols.add(new DataRequestHandlingProtocol<Station>(stations, "Station"));
		protocols.add(new DataRequestHandlingProtocol<SectionInfo>(sections, "SectionInfo"));
		protocols.add(new DataRequestHandlingProtocol<NodeInfo>(nodes , "NodeInfo"));
		
		// Accept and handle requests for company node locations
		protocols.add(new DataRequestHandlingProtocol<RouteToCompany>(routeToCompanies, "RouteToCompany"));
		
		return protocols;
	}

	public void test() {
		System.out.println(sections.size());
		System.out.println(stations.size());
		System.out.println(slaves);
	}
	
	public static void main(String[] args) {
		try {
			System.out.println(Inet4Address.getLocalHost().getHostAddress());
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		MasterDataRepository repo;
		try {
			repo = new MasterDataRepository();
			repo.start();
			repo.test();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
}
