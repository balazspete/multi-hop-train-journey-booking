package node.company;

import java.io.*;
import java.util.*;

import org.joda.time.DateTime;

import communication.protocols.DataRequestHandlingProtocol;
import communication.protocols.DataTransferHandlingProtocol;
import communication.protocols.Protocol;
import data.trainnetwork.BookableSection;
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
	public static class Store extends HashSet<BookableSection> implements Serializable {

		private static final long serialVersionUID = -5604189350057652145L;
		
		private static final String 
			CACHE = "/Users/balazspete/Projects/multi-hop-train-booking/distributed_datastore.cache";
		
		/**
		 * Load a {@link Store} from the default file
		 * @return The loaded store
		 * @throws StoreActionException Thrown if the specified creation of load failed (may be due to file errors, etc)
		 */
		public static Store restore() throws StoreActionException {
			return restore(CACHE);
		}
		
		/**
		 * Load a {@link Store} from a file
		 * @param The file to load from
		 * @return The loaded store
		 * @throws StoreActionException Thrown if the specified creation of load failed (may be due to file errors, etc)
		 */
		public static Store restore(String path) throws StoreActionException {
			Store store = null;
			try {
				FileInputStream in = new FileInputStream(path);
				ObjectInputStream input = new ObjectInputStream(in);
				store = (Store) input.readObject();
				input.close();
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
				throw new StoreActionException(e.getMessage());
			}
			
			return store;
		}
		
		/**
		 * Save the store to the default file (location)
		 * @throws StoreActionException Thrown if an error occurred
		 */
		public void save() throws StoreActionException {
			save(CACHE);
		}
		
		/**
		 * Save the store to a file
		 * @param path The path to the file
		 * @throws StoreActionException thrown if an error occurred
		 */
		public void save(String path) throws StoreActionException {
			try {
				FileOutputStream out = new FileOutputStream(path);
				ObjectOutputStream output = new ObjectOutputStream(out);
				output.writeObject(this);
				output.close();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
				throw new StoreActionException(e.getMessage());
			}
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
		
		private Store store;
		
		public StoreSaver(Store store) {
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
				
				final Store temp = store;
				try {
					temp.save();
				} catch (StoreActionException e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected static Store sections;
	protected StoreSaver saver;
	
	public DistributedRepositoryDataStore() throws RepositoryException {
		// TODO load from config
		super(8005);
	}

	@Override
	protected void initialize() {
		try {
			sections = Store.restore();
		} catch (StoreActionException e) {
			// No previously created cache, initiate blank Store
			sections = new Store();
		}
		
		saver = new StoreSaver(sections);
		saver.start();
	}

	@Override
	protected Set<Protocol> getProtocols() {
		Set<Protocol> protocols = new HashSet<Protocol>();
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
	
	/**
	 * Run a {@link DistributedRepositoryDataStore}
	 * @param args
	 */
	public static void main(String[] args) {
		DistributedRepositoryDataStore ds;
		try {
			ds = new DistributedRepositoryDataStore();
			ds.test();
			ds.start();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
}
