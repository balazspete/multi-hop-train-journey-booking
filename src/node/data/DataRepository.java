package node.data;

import java.util.Set;

import communication.protocols.Protocol;
import communication.unicast.UnicastSocketServer;

/**
 * An object describing a general data repository
 * @author Balazs Pete
 *
 */
public abstract class DataRepository extends Thread {
	
	private UnicastSocketServer server;
	
	/**
	 * Create a repository which listens for connection on a specified port
	 * @param port The port on which the repository should accept connections
	 * @throws RepositoryException 
	 */
	public DataRepository(int port) throws RepositoryException {
		 server = new UnicastSocketServer(port);
		 initialize();
		 loadProtocols();
	}
	
	@Override
	public void run() {
		server.start();
	}

	/**
	 * A method initialising all required components of the repository (executed at creation)
	 * @throws RepositoryException Thrown when the node initialisation has failed
	 */
	protected abstract void initialize() throws RepositoryException;
	
	/**
	 * Retrieve all protocols used by the repository
	 * @return The {@link Set} of {@link Protocol}s of the repository
	 */
	protected abstract Set<Protocol> getProtocols();

	private boolean loadProtocols() {
		for (Protocol protocol : getProtocols()) {
			server.putProtocol(protocol);
		}
		
		return true;
	}
}
