package communication.unicast;

import communication.CommunicationException;
import communication.protocols.ProtocolControlledMessenger;

/**
 * A generic client-to-server communication server interface
 * @author Balazs Pete
 *
 */
public abstract class UnicastServer extends ProtocolControlledMessenger {

	/**
	 * Start accepting incoming connections and messages
	 * @throws CommunicationException Thrown in case of an error
	 */
	public abstract void acceptConnections() throws CommunicationException;
}
