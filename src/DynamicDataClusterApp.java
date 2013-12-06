import node.company.*;
import node.data.RepositoryException;

/**
 * A launcher wrapper for the dynamic data cluster modules 
 * @author Balazs Pete
 *
 */
public class DynamicDataClusterApp {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Some arguments are required! Use --help for more information");
			return;
		}
		
		if (args[0].equalsIgnoreCase("--help")) {
			System.out.println(
				"DynamicDataClusterApp usage:\n" +
				"DynamicDataClusterApp \"data-store\"\n" +
				"DynamicDataClusterApp \"master\" data-store-location cluster-name\n" +
				"SynamicDataClusterApp master-location"
			);
			return;
		}
		
		if (args[0].equalsIgnoreCase("data-store")) {
			initDataStore(args);
			return;
		}
		
		if (args[0].equalsIgnoreCase("master")) {
			initMaster(args);
			return;
		}
		
		initSlave(args);
	}

	
	private static void initDataStore(String[] args) {
		try {
			if (args.length < 2 || !(args[1] instanceof String)) {
				throw new RepositoryException("Arg1 required to be the data store's save file location");
			}
			
			DistributedRepositoryDataStore.STORE_FILE_PATH= args[1];
			DistributedRepositoryDataStore ds = new DistributedRepositoryDataStore();
			ds.test();
			ds.start();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
	
	private static void initMaster(String[] args) {
		DistributedRepositoryMaster r;
		try {
			if (args.length < 2 || !(args[1] instanceof String)) {
				throw new RepositoryException("Arg1 required to be the data store's location");
			}
			
			if (args.length < 3 || !(args[2] instanceof String)) {
				throw new RepositoryException("Arg2 required to be the name of the dynamic data cluster");
			}

			DistributedRepositoryMaster.DATA_STORE_LOCATION = args[1];
			DistributedRepositoryMaster.CLUSTER_NAME = args[2];
			r = new DistributedRepositoryMaster();
			r.start();
			// TODO re-enable backups
			r.scheduledBackup();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
	
	private static void initSlave(String[] args) {
		DistributedRepositorySlave s;
		try {
			if (args.length < 1 || !(args[0] instanceof String)) {
				throw new RepositoryException("Arg1 required to be the master node's location");
			}
			
			DistributedRepositorySlave.DATA_STORE_LOCATION = args[0];
			s = new DistributedRepositorySlave();
			s.start();
			//s.test();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
}
