package node.data;

import java.util.Set;

import org.joda.time.DateTime;

import communication.CommunicationException;
import communication.messages.*;
import communication.unicast.UnicastSocketClient;
import data.request.*;
import data.system.NodeInfo;
import data.trainnetwork.*;

/**
 * An object that retrieves static data from a {@link DataRepository} using a {@link UnicastSocketClient}
 * @author Balazs Pete
 *
 */
public class StaticDataLoader implements DataLoader {
	
	private static final int MAX_TRIES = 1;

	private UnicastSocketClient client;
	
	private volatile Set<SectionInfo> sections;
	private volatile Set<Station> stations;
	private volatile Set<NodeInfo> nodeInfos;
	
	/**
	 * Create a {@link StaticDataLoader} with specific connection parameters
	 * @param host The URL or IP address of the {@link DataRepository}
	 * @param port The port the client should be connecting to the repository
	 */
	public StaticDataLoader(String host, int port) {
		client = new UnicastSocketClient(host, port);
	}
	
	@Override
	public void getData(DateTime from, DateTime until, boolean getStations) {
		int triesCount = 0;
		while (triesCount++ < MAX_TRIES) {
			try {
				client.createConnection();
				continue;
			} catch (CommunicationException e) {
				e.printStackTrace();
			}
		}
		
		DataRequest<SectionInfo> sectionRequest = new SectionDataRequest(from, until);
		Message message = new DataRequestMessage<SectionInfo>(sectionRequest, "SectionInfo");
		Message sectionsReply = trySendMessage(message, true);
		
		DataRequest<NodeInfo> routeMappingRequest = new NodeInfoDataRequest();
		message = new DataRequestMessage<NodeInfo>(routeMappingRequest, "NodeInfo");
		Message routeMappingReply = trySendMessage(message, true);
		
		Message stationsReply = null;
		if (getStations) {
			DataRequest<Station> stationRequest = new StationDataRequest(); 
			message = new DataRequestMessage<Station>(stationRequest, "Station");
			stationsReply = trySendMessage(message, true);
		}
		
		update(stationsReply, sectionsReply, routeMappingReply);
		

		triesCount = 0;
		while (triesCount++ < MAX_TRIES) {
			try {
				client.endConnection();
				continue;
			} catch (CommunicationException e) {
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void update(Message stationsMessage, Message sectionsMessage, Message nodesMessage) {
		stations = (Set<Station>) stationsMessage.getContents();
		sections = (Set<SectionInfo>) sectionsMessage.getContents();
		nodeInfos = (Set<NodeInfo>) nodesMessage.getContents();
	}
	
	private Message trySendMessage(Message message, boolean expectReply) {
		int tries = 0;
		while (tries++ < MAX_TRIES) {
			try {
				client.sendMessage(message);
				continue;
			} catch (CommunicationException e) {
			}
		}
		
		if (expectReply) {
			try {
				return client.getMessage();
			} catch (Exception e) {
				return null;
			}
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
}
