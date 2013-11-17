package node.client;

import java.util.HashMap;
import java.util.Map;

import communication.CommunicationException;
import communication.messages.ClusterHelloMessage;
import communication.messages.Message;
import communication.unicast.UnicastSocketClient;

import transaction.Lock.Token;
import transaction.WriteOnlyLock;

import node.FatalNodeException;
import node.data.ClientDataLoader;
import node.data.StaticDataLoadException;
import data.system.ClusterInfo;
import data.system.NodeInfo;
import data.trainnetwork.Network;

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
	private final int MASTER_PORT = 8000, CLUSTER_PORT = 7000;
	private String staticServerAddress = "localhost";
	
	public Client() throws FatalNodeException {
		try {
			connectToStaticDataCluster(staticServerAddress, MASTER_PORT);
		} catch (CommunicationException e) {
			throw new FatalNodeException(e.getMessage());
		}
		
		loadData();
	}
	
	public void loadData() throws FatalNodeException {
		network = new Network();
		routeToCompanies = new HashMap<String, NodeInfo>();
		
		loader = new ClientDataLoader(info.getLocation(), CLUSTER_PORT, network, routeToCompanies, communicationsLock);
		try {
			loader.getData(null, null, true);
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
	
	public void test() {
		while (true) {
			try {
				sleep(2000);
			} catch (InterruptedException e) {
				// Just loop around
			}
			
			System.out.println(network.edgeSet().size());
			System.out.println(network.vertexSet().size());
			
			System.out.println(routeToCompanies);
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Client c = new Client();
			c.start();
			c.test();
		} catch (FatalNodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
}
