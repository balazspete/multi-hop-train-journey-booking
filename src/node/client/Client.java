package node.client;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import org.joda.time.DateTime;

import algorithm.graph.AppliedDijkstraShortestPath;

import communication.CommunicationException;
import communication.messages.ClusterHelloMessage;
import communication.messages.Message;
import communication.unicast.UnicastSocketClient;

import transaction.Lock.Token;
import transaction.WriteOnlyLock;

import node.FatalNodeException;
import node.NodeConstants;
import node.client.gui.*;
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
	
	public static final int MAX_DATES = 5;
	
	private static WriteOnlyLock<Integer> communicationsLock = new WriteOnlyLock<Integer>(1);
	
	private static Network network;
	private static Map<String, NodeInfo> routeToCompanies;
	
	private ClusterInfo info;
	
	private ClientDataLoader loader;
	private final int 
		CLUSTER_PORT = NodeConstants.STATIC_CLUSTER_SLAVE_PORT;
	private String staticServerLocation;
	
	private CompanyRepositoryInterface companyInterface;
	
	private MainWindow mainWindow;
	private BookingWindow bookingWindow;
	
	private boolean searching = false;
	
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
		mainWindow = new MainWindow();
		bookingWindow = new BookingWindow(network.vertexSet());
		mainWindow.getBtnBookTrainJourney().addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				bookingWindow.setVisible(true);
			}
		});
		
		bookingWindow.getSearchButton().addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!searching) handleSearchRequest();
			}
		});
		
		mainWindow.setVisible(true);
	}
	
	public HashSet<Section> findJourney(String source, String target, DateTime from) {
		AppliedDijkstraShortestPath dijkstra = new AppliedDijkstraShortestPath(network, getStation(source), getStation(target), from);
		return new HashSet<Section>(dijkstra.getPath());
	}
	
	public Set<Seat> bookJourney(HashSet<Section> path) throws BookingException {
		Set<Seat> seats = companyInterface.bookJourney(path);
		System.out.println(seats);
		return seats;
	}
	
	public void cancelJourney(Set<Seat> seats) {
		companyInterface.cancelBooking(new HashSet<Seat>(seats));
	}
	
	public Set<Section> getStatusUpdate(Set<Section> sections) {
		return companyInterface.getStatusUpdate(sections);
	}
	
//	public void test() {
//		String
//			source = "DUBPS",
//			target = "GALWY";
//		
//		try {
//			HashSet<Section> sections = findJourney(source, target);
//			Set<Seat> seats = bookJourney(sections);
//			
//			for (Seat seat : seats) {
//				System.out.println(seat.toString());
//			}
//			
//			System.out.println(getStatusUpdate(sections));
//			
//			
////			cancelJourney(seats);
//		} catch (BookingException e) {
//			e.printStackTrace();
//		}
//	}
	
	private Station getStation(String stationId) {
		for (Station station : network.vertexSet()) {
			if (station.getID().equalsIgnoreCase(stationId)) {
				return station;
			}
		}
		
		return null;
	}
	
	private void handleSearchRequest() {
		bookingWindow.printStatus("Searching...");
		String originID = bookingWindow.getOriginID();
		String destinationID = bookingWindow.getDestinationID();
		DateTime from = bookingWindow.getStartDateTime();
		
		searching = true;
		System.out.println("Client: Starting path search with parameters {source:'" + originID + 
				"', target:'" + destinationID + "', time:'" + from + "'");
		
		HashSet<Section> sections = findJourney(originID, destinationID, from);
		System.out.println("Client: Search ended");
		searching = false;

		if (sections.size() == 0) {
			String message = "Your search returned no results";
			bookingWindow.printStatus(message);
			JOptionPane.showMessageDialog(bookingWindow, message);
			return;
		}
		
		// TODO display result & book
	}
}
