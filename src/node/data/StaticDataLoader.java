package node.data;

import java.util.Set;

import org.joda.time.DateTime;

import transaction.Lock.Token;
import transaction.WriteOnlyLock;

import communication.CommunicationException;
import communication.messages.*;
import communication.unicast.UnicastSocketClient;
import data.request.*;
import data.system.NodeInfo;
import data.system.RouteToCompany;
import data.trainnetwork.*;

/**
 * An object that retrieves static data from a {@link DataRepository} using a {@link UnicastSocketClient}
 * @author Balazs Pete
 *
 */
public class StaticDataLoader implements DataLoader {
	
	private static final int MAX_TRIES = 1;

	private UnicastSocketClient client;
	
	protected static Set<SectionInfo> sections;
	protected static Set<Station> stations;
	protected static Set<NodeInfo> nodeInfos;
	protected static Set<RouteToCompany> routeToCompanies;
	
	protected WriteOnlyLock<Integer> monitor;
	
	/**
	 * Create a {@link StaticDataLoader} with specific connection parameters
	 * @param host The URL or IP address of the {@link DataRepository}
	 * @param port The port the client should be connecting to the repository
	 */
	public StaticDataLoader(String host, int port, WriteOnlyLock<Integer> monitor) {
		client = new UnicastSocketClient(host, port);
		this.monitor = monitor;
	}
	
	@Override
	public void getData(DateTime from, DateTime until, boolean getStations) throws StaticDataLoadException {
		//--- begin locked state
		Token token = monitor.writeLock();
		int triesCount = 0;
		while (triesCount++ < MAX_TRIES) {
			try {
				client.createConnection();
				break;
			} catch (CommunicationException e) {
				if (triesCount < MAX_TRIES) {
					System.err.println(e.getMessage() + "; will retry soon...");
					continue;
				}
				
				// Unlock monitor before throwing the exception
				monitor.writeUnlock(token);
				throw new StaticDataLoadException(e.getMessage());
			}
		}
		
		DataRequest<SectionInfo> sectionRequest = new SectionDataRequest(from, until);
		Message message = new DataRequestMessage<SectionInfo>(sectionRequest, "SectionInfo");
		Message sectionsReply = trySendMessage(message, true);
		
		DataRequest<NodeInfo> routeMappingRequest = new ClusterInfoRequest();
		message = new DataRequestMessage<NodeInfo>(routeMappingRequest, "NodeInfo");
		Message companyInfosReply = trySendMessage(message, true);
		
		DataRequest<RouteToCompany> companyMappingRequest = new RouteToCompanyDataRequest();
		message = new DataRequestMessage<RouteToCompany>(companyMappingRequest, "RouteToCompany");
		Message routeToCompanyReply = trySendMessage(message, true);
		
		Message stationsReply = null;
		if (getStations) {
			DataRequest<Station> stationRequest = new StationDataRequest(); 
			message = new DataRequestMessage<Station>(stationRequest, "Station");
			stationsReply = trySendMessage(message, true);
		}
		
		triesCount = 0;
		while (triesCount++ < MAX_TRIES) {
			try {
				client.endConnection();
				break;
			} catch (CommunicationException e) {
				System.err.println(e.getMessage());
			}
		}

		monitor.writeUnlock(token);
		//--- end locked state
		
		update(stationsReply, sectionsReply, companyInfosReply, routeToCompanyReply);
	}
	
	@SuppressWarnings("unchecked")
	protected void update(Message stationsMessage, Message sectionsMessage, Message companyInfosReply, Message routeToCompanyReply) {
		if (stationsMessage != null) {
			stations = (Set<Station>) stationsMessage.getContents();
		}
		
		sections = (Set<SectionInfo>) sectionsMessage.getContents();
		nodeInfos = (Set<NodeInfo>) companyInfosReply.getContents();
		routeToCompanies = (Set<RouteToCompany>) routeToCompanyReply.getContents();
	}
	
	private Message trySendMessage(Message message, boolean expectReply) {
		int tries = 0;
		while (tries++ < MAX_TRIES) {
			try {
				client.sendMessage(message);
				break;
			} catch (CommunicationException e) {
				System.err.println(e.getMessage() + "; will retry...");
			}
		}
		
		if (expectReply) {
			Message msg = null;
			try {
				msg = client.getMessage();
			} catch (CommunicationException e) {
				System.err.println(e.getMessage() + "; Message: " + msg);
				return null;
			} catch (InvalidMessageException e) {
				System.err.println(e.getMessage() + "; Message: " + msg);
				return null;
			}
			
			return msg;
		} else {
			return null;
		}
	}

	@Override
	public Set<SectionInfo> getSections() {
		return sections;
	}

	@Override
	public Set<Station> getStations() {
		return stations;
	}

	@Override
	public Set<NodeInfo> getNodeInfos() {
		return nodeInfos;
	}
	
	@Override
	public Set<RouteToCompany> getRouteToCompanies() {
		return routeToCompanies;
	}
}
