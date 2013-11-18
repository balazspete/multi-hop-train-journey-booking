package node.data;

import java.util.HashSet;
import java.util.Map;

import transaction.WriteOnlyLock;

import communication.messages.Message;

import data.system.NodeInfo;
import data.system.RouteToCompany;
import data.trainnetwork.Network;
import data.trainnetwork.Station;

/**
 * A {@link DataLoader} specific to clients
 * @author Balazs Pete
 *
 */
public class ClientDataLoader extends StaticDataLoader {

	private Network network;
	Map<String, NodeInfo> routeToCompanyInfo;
	
	/**
	 * Create a new {@link ClientDataLoader}
	 * @param network The {@link Network} that should be updated with the loaded data 
	 * @param map The {@link RouteToCompanyMap} that should be updated with the retrieved node data
	 */
	public ClientDataLoader(String address, int port, Network network, Map<String, NodeInfo> routeToCompanyInfo, WriteOnlyLock<Integer> monitor) {
		super(address, port, monitor);
		this.network = network;
		this.routeToCompanyInfo = routeToCompanyInfo;
	}
	
	@Override
	protected void update(Message stationsMessage, Message sectionsMessage, Message companyInfosReply, Message routeToCompanyReply) {
		super.update(stationsMessage, sectionsMessage, companyInfosReply, routeToCompanyReply);
		
		if (stationsMessage == null) {
			stations = new HashSet<Station>();
		}
		
		network.update(stations, sections);
		
		for (RouteToCompany rtc : routeToCompanies) {
			System.out.println(rtc.getKey() + " " + rtc.getValue());
			NodeInfo node = getNodeForCompany(rtc.getValue());
			routeToCompanyInfo.put(rtc.getKey(), node);
		}
		
		System.out.println(nodeInfos + " " + routeToCompanies);
	}
	
	/**
	 * Get the mappings from routeID to company {@link NodeInfo}
	 * @return The map
	 */
	public Map<String, NodeInfo> getRouteToCompanyInfo() {
		return routeToCompanyInfo;
	}
	
	private NodeInfo getNodeForCompany(String company) {
		for (NodeInfo node : nodeInfos) {
			if (node.getName().equalsIgnoreCase(company)) {
				return node;
			}
		}
		
		return null;
	}
}
