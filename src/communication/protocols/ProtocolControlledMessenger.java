package communication.protocols;

import java.util.HashMap;

import communication.messages.*;

/**
 * A networking interface which uses {@link Protocol}s to handle Messages
 * @author Balazs Pete
 *
 */
public abstract class ProtocolControlledMessenger extends Thread {

	protected volatile HashMap<String, Protocol> protocolMap = new HashMap<String, Protocol>();
	
	/** 
	 * Add a {@link Protocol} to handle a certain type of {@link Message}, if a previous protocol with the same message type has been added, it will be replaced
	 * @param protocol The {@link Protocol} to add
	 */
	public synchronized void putProtocol(Protocol protocol) {
		protocolMap.put(protocol.getAcceptedMessageType().intern(), protocol);
	}
	
	/**
	 * Remove a specific {@link Protocol} from the interface
	 * @param protocol The {@link Protocol} to remove
	 */
	public synchronized void removeProtocol(Protocol protocol) {
		protocolMap.remove(protocol.getAcceptedMessageType());
	}
	
	/**
	 * Remove all {@link Protocol}s from the interface
	 */
	public void removeAllProtocols() {
		protocolMap = new HashMap<String, Protocol>();
	}
	
	/**
	 * Process an input message using one of the input {@link Protocol}s
	 * @param message The {@link Message} to process
	 * @return The response {@link Message}
	 * @throws InvalidMessageException Thrown in case the input {@link Message} cannot be handled by the interface
	 */
	protected Message processMessage(Message message) throws InvalidMessageException {
		Protocol p = protocolMap.get(message.getType()); 
		
		if(p == null) 
			throw new InvalidMessageException("Message type cannot be handled by the interface");
		
		return p.processMessage(message);
	}
}
