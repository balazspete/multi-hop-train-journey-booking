import java.net.Inet4Address;
import java.net.UnknownHostException;

import node.central.*;
import node.data.RepositoryException;

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
				"StaticDataClusterApp \"master\"\n" +
				"StaticDataClusterApp master-location\n"
			);
			return;
		}
		
		if (args[0].equalsIgnoreCase("master")) {
			initMaster();
		} else {
			initSlave(args);
		}
	}

	private static void initMaster() {
		try {
			System.out.println(Inet4Address.getLocalHost().getHostAddress());
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		
		MasterDataRepository repo;
		try {
			repo = new MasterDataRepository();
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
