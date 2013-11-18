package node;

/**
 * A list of constants definitions
 * @author Balazs Pete
 *
 */
public class NodeConstants {

	public static final int 
		STATIC_CLUSTER_MASTER_PORT = 8000,		// MasterDataRepository
		STATIC_CLUSTER_SLAVE_PORT = 8001,		// SlaveDataRepository
		DYNAMIC_CLUSTER_PORT = 8002,			// DistributedRepository
		DYNAMIC_CLUSTER_STORE_PORT = 8005;		// DistributedRepositoryDataStore
	
}
