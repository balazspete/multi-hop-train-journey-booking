package node.data;

import java.util.Set;

import communication.messages.Message;

import data.system.NodeInfo;
import data.system.RouteToNodeMap;
import data.trainnetwork.Network;
import data.trainnetwork.SectionInfo;
import data.trainnetwork.Station;

/**
 * A {@link DataLoader} specific to clients
 * @author Balazs Pete
 *
 */
public class ClientDataLoader extends StaticDataLoader {

	private Network network;
	private RouteToNodeMap map;
	
	/**
	 * Create a new {@link ClientDataLoader}
	 * @param network The {@link Network} that should be updated with the loaded data 
	 * @param map The {@link RouteToNodeMap} that should be updated with the retrieved node data
	 */
	public ClientDataLoader(Network network, RouteToNodeMap map) {
		// TODO retrieve information from config
		super(null, -1);
		this.network = network;
		this.map = map;
	}
	
	@SuppressWarnings("unchecked")
	private void update(Message stationsMessage, Message sectionsMessage, Message nodesMessage) {
		Set<Station> stations = (Set<Station>) stationsMessage.getContents();
		Set<SectionInfo> sections = (Set<SectionInfo>) sectionsMessage.getContents();
		
		network.update(stations, sections);
		
		map.addMultipleNodeInfo((Set<NodeInfo>) nodesMessage.getContents());
	}
}
