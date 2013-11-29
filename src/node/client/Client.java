package node.client;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JButton;
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
import data.system.Ticket;
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
	private TicketsWindow ticketsWindow;
	
	private boolean searching = false;
	
	private List<Ticket> tickets = new ArrayList<Ticket>();
	
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
		mainWindow.getBtnCancelABooking().addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				createNewTicketsWindow();
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
	
	public List<Section> findJourney(String source, String target, DateTime from) {
		AppliedDijkstraShortestPath dijkstra = new AppliedDijkstraShortestPath(network, getStation(source), getStation(target), from);
		return dijkstra.getPath();
	}
	
	public Set<Seat> bookJourney(HashSet<Section> path) throws BookingException {
		Set<Seat> seats = companyInterface.bookJourney(path);
		return seats;
	}
	
	public void cancelJourney(Set<Seat> seats) {
		companyInterface.cancelBooking(new HashSet<Seat>(seats));
	}
	
	public Set<Section> getStatusUpdate(Set<Section> sections) {
		return companyInterface.getStatusUpdate(sections);
	}
	
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
		final String originID = bookingWindow.getOriginID();
		final String destinationID = bookingWindow.getDestinationID();
		final DateTime from = bookingWindow.getStartDateTime();
		
		searching = true;
		System.out.println("Client: Starting path search with parameters {source:'" + originID + 
				"', target:'" + destinationID + "', time:'" + from + "'");
		
		final List<Section> sections = findJourney(originID, destinationID, from);
		System.out.println("Client: Search ended");
		
		searching = false;
		if (sections.size() == 0) {
			String message = "Your search returned no results";
			bookingWindow.printStatus(message);
			JOptionPane.showMessageDialog(bookingWindow, message);
			bookingWindow.printStatus(message);
			return;
		}
		
		bookingWindow.printStatus("Found a journey...");
		
		final PathWindow window = new PathWindow(network, sections, originID, destinationID);
		window.getBookingButton().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					Set<Seat> journey = bookJourney(new HashSet<Section>(sections));
					
					Ticket ticket = new Ticket(originID, destinationID, from, journey);
					tickets.add(ticket);
					
					JOptionPane.showMessageDialog(window, "Ticket has been booked!\n" + ticket);
					window.dispose();
				} catch (BookingException ex) {
					String message = "Failed to book journey: " + ex.getMessage();
					System.err.println(message);
					JOptionPane.showMessageDialog(null, message, "Failed to book journey", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		window.setVisible(true);
	}
	
	private void createNewTicketsWindow() {
		ticketsWindow = new TicketsWindow(tickets);
		Vector<JButton> buttons = ticketsWindow.getCancelButtons();
		for (int i = 0; i < buttons.size(); i++) {
			final int index = i;
			buttons.get(i).addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					cancelJourney(tickets.get(index).getSeats());
					ticketsWindow.dispose();
					JOptionPane.showMessageDialog(null, "Ticket cancelled");
				}
			});
		}
		ticketsWindow.setVisible(true);
	}
}
