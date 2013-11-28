package node.central;

import java.util.*;

import transaction.Lock.Token;

import node.NodeConstants;
import node.data.RepositoryException;
import node.data.StaticDataLoadException;
import node.data.StaticDataLoader;
import communication.CommunicationException;
import communication.messages.HelloMessage;
import communication.messages.Message;
import communication.protocols.*;
import communication.unicast.UnicastSocketClient;
import data.system.NodeInfo;
import data.system.RouteToCompany;
import data.trainnetwork.*;

/**
 * A {@link StaticDataRepository} serving structured static data, retrieving raw information from a {@link MasterDataRepository}
 * @author Balazs Pete
 *
 */
public class SlaveDataRepository extends StaticDataRepository {

	private static final int PORT = NodeConstants.STATIC_CLUSTER_SLAVE_PORT;
	private static final int MASTER_PORT = NodeConstants.STATIC_CLUSTER_MASTER_PORT;
	
	private Network network;
	
	private static String MASTER_LOCATION;
	
	/**
	 * Create a {@link SlaveDataRepository}
	 * @throws RepositoryException Thrown if the initialisation failed
	 */
	public SlaveDataRepository() throws RepositoryException {
		super(PORT);
		helloToMaster();
	}
	
	@Override
	protected void initialize() {
		network = new Network();
		getDataFromMaster();
	}

	@Override
	protected Set<Protocol> getProtocols() {
		Set<Protocol> protocols = new HashSet<Protocol>();
		protocols.add(new DataRequestHandlingProtocol<Station>(stations, "Station"));
		protocols.add(new DataRequestHandlingProtocol<SectionInfo>(sections, "SectionInfo"));
		protocols.add(new DataRequestHandlingProtocol<NodeInfo>(nodes, "NodeInfo"));
		protocols.add(new DataRequestHandlingProtocol<RouteToCompany>(routeToCompanies, "RouteToCompany"));
		
		return protocols;
	}
	
	private void getDataFromMaster() {
		StaticDataLoader loader = new StaticDataLoader(MASTER_LOCATION, MASTER_PORT, communicationsLock);
		int tries = 0;
		while (true) {
			try {
				loader.getData(null, null, true);
				
				stations = loader.getStations();
				sections = loader.getSections();
				nodes = loader.getNodeInfos();
				routeToCompanies = loader.getRouteToCompanies();
				break;
			} catch (StaticDataLoadException e) {
				if (tries++ < MAX_TRIES) {
					System.err.println(e.getMessage() + "; will retry...");
					continue;
				}
				
				System.err.println(e.getMessage() + "; Mocking blank reply.");
				
				stations = new HashSet<Station>();
				sections = new HashSet<SectionInfo>();
				nodes = new HashSet<NodeInfo>();
				routeToCompanies = new HashSet<RouteToCompany>();
				break;
			}
		}
		
		network.update(stations, sections);
	}
	
	private void helloToMaster() throws RepositoryException {
		//--- begin HELLO 
		Token token = communicationsLock.writeLock();
		try {
			Message msg = HelloMessage.getHi();
			UnicastSocketClient.sendOneMessage(MASTER_LOCATION, MASTER_PORT, msg, true);
		} catch (CommunicationException e) {
			throw new RepositoryException("Failed to HELLO master: " + e.getMessage());
		} finally {
			communicationsLock.writeUnlock(token);
		}
		//--- end HELLO
	}
	
	public void test() {
		System.out.println(network.vertexSet().size());
		System.out.println(network.edgeSet().size());
	}

	public static void main(String[] args) {
		try {
			if (args.length < 1 || !(args[0] instanceof String)) {
				throw new RepositoryException("Arg1 required to be the master node's location of the static data cluster");
			}
			
			SlaveDataRepository.MASTER_LOCATION = args[0];
			SlaveDataRepository repo = new SlaveDataRepository();
			repo.start();
			//repo.test();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
}
