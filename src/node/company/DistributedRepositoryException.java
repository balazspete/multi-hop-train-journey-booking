package node.company;

import node.data.RepositoryException;

public class DistributedRepositoryException extends RepositoryException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3275148715123858388L;

	public DistributedRepositoryException() {
		super();
	}
	
	public DistributedRepositoryException(String message) {
		super(message);
	}
	
	public static final DistributedRepositoryException 
		FAILED_TO_LOAD_DATA = new DistributedRepositoryException("Failed to load data from the repository data store");
	
}
