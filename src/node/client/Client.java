package node.client;

import node.data.ClientDataLoader;
import data.system.RouteToNodeMap;
import data.trainnetwork.Network;

/**
 * The main class, managing all threads on the client
 * @author Balazs Pete
 *
 */
public class Client {

	public void test() {
		return;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Network network = new Network();
		RouteToNodeMap map = new RouteToNodeMap();
		
		ClientDataLoader loader = new ClientDataLoader(network, map);
		loader.getData(null, null, true);
		
		System.out.println(network.edgeSet().size());
		System.out.println(network.vertexSet().size());
	}

	
	
}
