package node.central;

import java.io.*;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.*;

import node.NodeConstants;
import node.data.RepositoryException;
import node.data.StaticDataLoadException;

import org.json.simple.*;
import org.json.simple.parser.*;

import transaction.Lock.Token;

import communication.CommunicationException;
import communication.messages.ClusterHelloMessage;
import communication.messages.Message;
import communication.protocols.*;
import communication.unicast.UnicastSocketClient;
import data.MissingParameterException;
import data.system.*;
import data.trainnetwork.*;

/**
 * A {@link StaticDataRepository} serving raw static data
 * @author Balazs Pete
 *
 */
public class MasterDataRepository extends StaticDataRepository {
	
	private static Set<NodeInfo>
		companyClusterMaserNodes = new HashSet<NodeInfo>(),
		slaves = new HashSet<NodeInfo>();
	
	private static final int PORT = NodeConstants.STATIC_CLUSTER_MASTER_PORT;
	
	private static final String 
		CLUSTER_NAME = "CENTRAL STATIC DATA CLUSTER",
		ROUTES_DATA = "/Users/balazspete/Projects/multi-hop-train-booking/compiled_routes.json", 
		STATIONS_DATA = "/Users/balazspete/Projects/multi-hop-train-booking/stations.json",
		COMPANY_LOCATIONS_DATA = "/Users/balazspete/Projects/multi-hop-train-booking/companies.json";
	
	/**
	 * Create a new {@link MasterDataRepository}
	 * @throws RepositoryException Thrown if the initialisation failed
	 */
	public MasterDataRepository() throws RepositoryException {
		super(PORT);
		try {
			update();
			connectToCompanyDataClusters();
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
				routeToCompanies.add(new RouteToCompany(route));
				for (SectionInfo info : route) {
					sections.add(info);
				}
			}
			
			JSONObject companyLocations = (JSONObject) parser.parse(new FileReader(COMPANY_LOCATIONS_DATA));
			for (Object key : companyLocations.keySet()) {
				String route = (String) key,
						location = (String) companyLocations.get(key);
				
				NodeInfo node = new NodeInfo(route, location);
				companyClusterMaserNodes.add(node);
			}
			
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
	
	private void connectToCompanyDataClusters() {
		Token token = communicationsLock.writeLock();
		Message message = new ClusterHelloMessage();
		for (NodeInfo node : companyClusterMaserNodes) {
			try {
				UnicastSocketClient client = new UnicastSocketClient(node.getLocation(), NodeConstants.DYNAMIC_CLUSTER_STORE_PORT);
				Message reply = UnicastSocketClient.sendOneMessage(client, message, true);
				ClusterInfo info = (ClusterInfo) reply.getContents();
				nodes.add(info);
			} catch (CommunicationException e) {
				System.err.println(e.getMessage());
				new RepositoryException(e.getMessage());
			}
		}
		
		communicationsLock.writeUnlock(token);
	}

	public void test() {
		System.out.println(sections.size());
		System.out.println(stations.size());
		System.out.println(nodes);
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
			//repo.test();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
}
