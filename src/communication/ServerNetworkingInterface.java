package communication;

import java.util.HashMap;

import communication.protocols.*;
import communication.messages.*;

/**
 * A general server networking interface
 * @author Balazs Pete
 *
 */
public abstract class ServerNetworkingInterface extends Thread {

	protected HashMap<String, Protocol> protocolMap = new HashMap<String, Protocol>();
	
	/**
	 * Start accepting incoming connections and messages
	 * @throws CommunicationException Thrown in case of an error
	 */
	public abstract void acceptConnections() throws CommunicationException;
	
	/** 
	 * Add a Protocol to handle a certain type of message, if a previous protocol with the same message type has been added, it will be replaced
	 * @param protocol The Protocol to add
	 */
	public synchronized void putProtocol(Protocol protocol) {
		protocolMap.put(protocol.getAcceptedMessageType(), protocol);
	}
	
	/**
	 * Remove a specific protocol from the interface
	 * @param protocol The protocol to remove
	 */
	public synchronized void removeProtocol(Protocol protocol) {
		protocolMap.remove(protocol.getAcceptedMessageType());
	}
	
	/**
	 * Remove all protocols from the interface
	 */
	public void removeAllProtocols() {
		protocolMap = new HashMap<String, Protocol>();
	}

	/**
	 * Process an input message using one of the input protocols
	 * @param message The message to process
	 * @return The response message
	 * @throws InvalidMessageException Thrown in case the input message cannot be handled by the interface
	 */
	protected Message processMessage(Message message) throws InvalidMessageException {
		Protocol p = protocolMap.get(message.getType()); 
		
		if(p == null) 
			throw new InvalidMessageException("Message type cannot be handled by the interface");
		
		return p.processMessage(message);
	}
	
}
