package node.data;

import java.util.HashSet;
import java.util.Set;

import communication.protocols.Protocol;
import communication.unicast.UnicastSocketServer;

import data.system.NodeInfo;
import data.trainnetwork.SectionInfo;
import data.trainnetwork.Station;

public abstract class DataRepository extends Thread {
	
	private UnicastSocketServer server;
	
	/**
	 * Create a repository which listens for connection on a specified port
	 * @param port The port on which the repository should accept connections
	 */
	public DataRepository(int port) {
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
	 */
	protected abstract void initialize();
	
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
