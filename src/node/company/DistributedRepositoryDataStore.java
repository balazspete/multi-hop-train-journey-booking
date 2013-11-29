package node.company;

import java.io.*;
import java.util.*;

import org.joda.time.DateTime;

import communication.protocols.DataRequestHandlingProtocol;
import communication.protocols.DataTransferHandlingProtocol;
import communication.protocols.HelloProtocol;
import communication.protocols.Protocol;
import data.system.NodeInfo;
import data.trainnetwork.BookableSection;
import node.NodeConstants;
import node.data.DataRepository;
import node.data.RepositoryException;

/**
 * A node used to store BookableSection data
 * @author Balazs Pete
 *
 */
public class DistributedRepositoryDataStore extends DataRepository {
	
	/**
	 * Exception thrown when an error occurred in the Store
	 * @author Balazs Pete
	 *
	 */
	public static class StoreActionException extends Exception {
		private static final long serialVersionUID = 8297465545519510059L;
		public StoreActionException(String message) {
			super(message);
		}
	}
	
	/**
	 * An extended {@link Set} used by {@link DistributedRepositoryDataStore}
	 * Contains functionality to save and load data from a file 
	 * @author Balazs Pete
	 *
	 */
	public static class Store<DATA> extends HashSet<DATA> implements Serializable {

		private static final long serialVersionUID = -5604189350057652145L;
		private static final String
			EXTENSION = ".cache",
			// TODO get this from args or something
			CACHE = "/Users/balazspete/Projects/multi-hop-train-booking/distributed_datastore";
		
		private String name, cache;
		
		public Store(String name) {
			this(name, CACHE);
		}
		
		public Store(String name, String cache) {
			this.name = name;
			this.cache = cache;
		}
		
		/**
		 * Load a {@link Store} from the default file
		 * @return The loaded store
		 * @throws StoreActionException Thrown if the specified creation of load failed (may be due to file errors, etc)
		 */
		public static Store<?> restore(String name) throws StoreActionException {
			return restore(CACHE, name);
		}
		
		/**
		 * Load a {@link Store} from a file
		 * @param The file to load from
		 * @return The loaded store
		 * @throws StoreActionException Thrown if the specified creation of load failed (may be due to file errors, etc)
		 */
		public static Store<?> restore(String path, String name) throws StoreActionException {
			String fullPath = getPath(path, name);
			
			Store<?> store = null;
			try {
				FileInputStream in = new FileInputStream(fullPath);
				ObjectInputStream input = new ObjectInputStream(in);
				store = (Store<?>) input.readObject();
				input.close();
				in.close();
			} catch (Exception e) {
				throw new StoreActionException(e.getMessage());
			}
			
			return store;
		}
		
		/**
		 * Save the store to the default file (location)
		 * @throws StoreActionException Thrown if an error occurred
		 */
		public void save() throws StoreActionException {
			save(cache);
		}
		
		/**
		 * Save the store to a file
		 * @param path The path to the file
		 * @throws StoreActionException thrown if an error occurred
		 */
		public void save(String path) throws StoreActionException {
			try {
				FileOutputStream out = new FileOutputStream(getPath(path, name));
				ObjectOutputStream output = new ObjectOutputStream(out);
				output.writeObject(this);
				output.close();
				out.close();
			} catch (Exception e) {
				throw new StoreActionException(e.getMessage());
			}
		}
		
		private static String getPath(String path, String name) {
			return path + "_" + name + EXTENSION;
		}
	}
	
	/**
	 * An object used to save the store regularly in the specified time intervals
	 * @author balazs Pete
	 *
	 */
	public static class StoreSaver extends Thread {
		// Back up every 100 seconds (should be increased to about 5 minutes)
		public static final int SAVE_PERIOD = 1000 * 100;
		
		private Store<?> store;
		
		public StoreSaver(Store<?> store) {
			this.store = store;
		}
		
		@Override
		public void run() {
			while (true) {
				try {
					sleep(SAVE_PERIOD);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				final Store<?> temp = store;
				try {
					temp.save();
				} catch (StoreActionException e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected static Store<BookableSection> sections;
	protected StoreSaver saver;
	protected static Store<NodeInfo> nodes;
	
	public DistributedRepositoryDataStore() throws RepositoryException {
		super(NodeConstants.DYNAMIC_CLUSTER_STORE_PORT);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void initialize() {
		sections = (Store<BookableSection>) getStore("bookablesections");
		saver = new StoreSaver(sections);
		saver.start();
		
		// NodeInfos should not be backed up
		nodes = new Store<NodeInfo>("nodeinfos");
	}
	
	@SuppressWarnings("rawtypes")
	private Store<?> getStore(String name) {
		try {
			return Store.restore(name);
		} catch (StoreActionException e) {
			// No previously created cache, initiate blank Store
			System.err.println("No previous cache found for `" + name + "`, creating blank data-store...");
			return new Store(name);
		}
	}

	@Override
	protected Set<Protocol> getProtocols() {
		Set<Protocol> protocols = new HashSet<Protocol>();
		
		// Accept and handle `Hello` requests from other nodes
		protocols.add(new HelloProtocol(nodes));
		
		// Accept and handle data requests for <NodeInfo>s
		protocols.add(new DataRequestHandlingProtocol<NodeInfo>(nodes, "NodeInfo"));
		
		// Accept and handle data requests for <BookableSection>s
		protocols.add(new DataRequestHandlingProtocol<BookableSection>(sections, "BookableSection"));
		protocols.add(new DataTransferHandlingProtocol<BookableSection>(sections, "BookableSection"));
		
		return protocols;
	}
	
	public void test() {
		if (sections.size() == 0) {
			BookableSection s = new BookableSection("id", 1, DateTime.now(), 20, 20);
			s.setMaxPassengers(100);
			sections.add(s);
		}
	}
}
