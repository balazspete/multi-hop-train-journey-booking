package node.central;

import java.util.*;

import node.data.DataRepository;
import node.data.RepositoryException;
import data.system.NodeInfo;
import data.trainnetwork.*;

/**
 * An object describing a general data repository for the static data within the system
 * @author Balazs Pete
 *
 */
public abstract class StaticDataRepository extends DataRepository {

	protected static final int MAX_TRIES = 3;

	protected static Set<Station> stations = new HashSet<Station>();
	protected static Set<SectionInfo> sections = new HashSet<SectionInfo>();
	protected static Set<NodeInfo> nodes = new HashSet<NodeInfo>();
	
	/**
	 * Create a repository which listens for connection on a specified port
	 * @param port The port on which the repository should accept connections
	 * @throws RepositoryException Thrown if the initialisation failed
	 */
	public StaticDataRepository(int port) throws RepositoryException {
		 super(port);
	}
}
