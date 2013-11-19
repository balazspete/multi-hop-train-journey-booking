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
import node.NodeConstants;
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
	private final int 
		MASTER_PORT = NodeConstants.STATIC_CLUSTER_MASTER_PORT, 
		CLUSTER_PORT = NodeConstants.STATIC_CLUSTER_SLAVE_PORT;
	private String staticServerLocation;
	
	private CompanyRepositoryInterface companyInterface;
	
	public Client(String location) throws FatalNodeException {
		staticServerLocation = location;
		
		try {
			connectToStaticDataCluster(staticServerLocation, MASTER_PORT);
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
			if (args.length < 1 || !(args[0] instanceof String)) {
				throw new FatalNodeException("Arg1 required to be the master node's location of the static data cluster");
			}
			
			Client c = new Client(args[0]);
			c.start();
			//c.test();
		} catch (FatalNodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
}
