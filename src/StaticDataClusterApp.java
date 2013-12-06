import node.central.*;
import node.data.RepositoryException;

/**
 * A launcher wrapper for the static data cluster modules 
 * @author Balazs Pete
 *
 */
public class StaticDataClusterApp {

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
				"StaticDataClusterApp usage:\n" +
				"StaticDataClusterApp \"master\" stations-and-routes company-locations\n" +
				"StaticDataClusterApp master-location\n"
			);
			return;
		}
		
		if (args[0].equalsIgnoreCase("master")) {
			initMaster(args);
		} else {
			initSlave(args);
		}
	}

	private static void initMaster(String[] args) {
		try {
			if (args.length < 3) {
				throw new RepositoryException("Arg2 - 'raw stations&routes data', Arg3 - 'company locations'");
			}
		
			MasterDataRepository repo = new MasterDataRepository(args[1], args[2]);
			repo.start();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
	
	private static void initSlave(String[] args) {
		try {
			if (args.length < 1 || !(args[0] instanceof String)) {
				throw new RepositoryException("Arg1 required to be the master node's location of the static data cluster");
			}
			
			SlaveDataRepository.MASTER_LOCATION = args[0];
			SlaveDataRepository repo = new SlaveDataRepository();
			repo.start();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
}
