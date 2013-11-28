package node.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import algorithm.graph.AppliedDijkstraShortestPath;

import communication.CommunicationException;
import communication.messages.ClusterHelloMessage;
import communication.messages.Message;
import communication.unicast.UnicastSocketClient;

import transaction.Lock.Token;
import transaction.WriteOnlyLock;

import node.FatalNodeException;
import node.NodeConstants;
import node.data.ClientDataLoader;
import node.data.StaticDataLoadException;
import data.system.ClusterInfo;
import data.system.NodeInfo;
import data.trainnetwork.Network;
import data.trainnetwork.Seat;
import data.trainnetwork.Section;
import data.trainnetwork.Station;

/**
 * The main class, managing all threads on the client
 * @author Balazs Pete
 *
 */
public class Client extends Thread {
	
	private static WriteOnlyLock<Integer> communicationsLock = new WriteOnlyLock<Integer>(1);
	
	private static Network network;
	private static Map<String, NodeInfo> routeToCompanies;
	
	private ClusterInfo info;
	
	private ClientDataLoader loader;
	private final int 
		CLUSTER_PORT = NodeConstants.STATIC_CLUSTER_SLAVE_PORT;
	private String staticServerLocation;
	
	private CompanyRepositoryInterface companyInterface;
	
	public Client(String location) throws FatalNodeException {
		staticServerLocation = location;
		
		try {
			connectToStaticDataCluster(staticServerLocation, NodeConstants.STATIC_CLUSTER_MASTER_PORT);
		} catch (CommunicationException e) {
			throw new FatalNodeException(e.getMessage());
		}
		
		loadData();
		companyInterface = new CompanyRepositoryInterface(routeToCompanies, communicationsLock);
	}
	
	public void loadData() throws FatalNodeException {
		network = new Network();
		routeToCompanies = new HashMap<String, NodeInfo>();
		
		loader = new ClientDataLoader(info.getLocation(), CLUSTER_PORT, network, routeToCompanies, communicationsLock);
		try {
			loader.getData(null, null, true);
			
			loader.getStations();
		} catch (StaticDataLoadException e) {
			throw new FatalNodeException(e.getMessage());
		}
	}
	
	private void connectToStaticDataCluster(String masterLocation, int masterPort) throws CommunicationException {
		Token token = communicationsLock.writeLock();
		
		Message message = new ClusterHelloMessage();
		Message reply = UnicastSocketClient.sendOneMessage(masterLocation, masterPort, message, true);
		
		info = (ClusterInfo) reply.getContents();
		communicationsLock.writeUnlock(token);
	}
	
	
	
	@Override
	public void run() {
		System.out.println("running");
		test();
		
		
	}
	
	public Set<Seat> bookJourney(String source, String target) throws BookingException {
		AppliedDijkstraShortestPath dijkstra = new AppliedDijkstraShortestPath(network, getStation(source), getStation(target));
		HashSet<Section> path = new HashSet<Section>(dijkstra.getPath());
		
		Set<Seat> seats = companyInterface.bookJourney(path);
		System.out.println(seats);
		return seats;
	}
	
	public void cancelJourney(Set<Seat> seats) {
		companyInterface.cancelBooking(new HashSet<Seat>(seats));
	}
	
	public void test() {
		String
			source = "DUBPS",
			target = "GALWY";
		
		try {
			Set<Seat> seats = bookJourney(source, target);
			
			for (Seat seat : seats) {
				System.out.println(seat.toString());
			}
			
			cancelJourney(seats);
		} catch (BookingException e) {
			e.printStackTrace();
		}
	}
	
	private Station getStation(String stationId) {
		for (Station station : network.vertexSet()) {
			if (station.getID().equalsIgnoreCase(stationId)) {
				return station;
			}
		}
		
		return null;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			if (args.length < 1 || !(args[0] instanceof String)) {
				throw new FatalNodeException("Arg1 required to be the master node's location of the static data cluster");
			}
			
			Client c = new Client(args[0]);
			c.start();
		} catch (FatalNodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
}
