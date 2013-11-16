package node.central;

import java.util.*;

import node.data.RepositoryException;
import node.data.StaticDataLoader;
import communication.protocols.*;
import data.system.NodeInfo;
import data.trainnetwork.*;

/**
 * A {@link StaticDataRepository} serving structured static data, retrieving raw information from a {@link MasterDataRepository}
 * @author Balazs Pete
 *
 */
public class SlaveDataRepository extends StaticDataRepository {

	private Network network;
	
	/**
	 * Create a {@link SlaveDataRepository}
	 * @throws RepositoryException Thrown if the initialisation failed
	 */
	public SlaveDataRepository() throws RepositoryException {
		// TODO load port# from config
		super(7000);
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
		
		return protocols;
	}
	
	private void getDataFromMaster() {
		// TODO load connection info from config
		StaticDataLoader loader = new StaticDataLoader("localhost", 8000);
		loader.getData(null, null, true);
		
		stations = loader.getStations();
		sections = loader.getSections();
		nodes = loader.getNodeInfos();
		network.update(stations, sections);
	}
	
	public void test() {
		System.out.println(network.vertexSet().size());
		System.out.println(network.edgeSet().size());
	}

	public static void main(String[] args) {
		SlaveDataRepository repo;
		try {
			repo = new SlaveDataRepository();
			repo.start();
			repo.test();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
}
