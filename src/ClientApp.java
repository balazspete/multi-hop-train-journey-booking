import node.FatalNodeException;
import node.client.Client;

/**
 * A wrapper for the client application
 * @author Balazs Pete
 *
 */
public class ClientApp {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			if (args.length < 1 || !(args[0] instanceof String)) {
				throw new FatalNodeException("First argument is required to be the master node's location (of the static data cluster)");
			}
			
			Client c = new Client(args[0]);
			c.start();
		} catch (FatalNodeException e) {
			System.err.println("A fatal exception occurred in the client");
			e.printStackTrace();
		}
	}
}
