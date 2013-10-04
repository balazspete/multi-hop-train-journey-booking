package communication;

import java.util.HashMap;

import communication.protocols.Protocol;
import communication.messages.Message;

public abstract class ServerNetworkingInterface extends Thread {

	protected HashMap<String, Protocol> protocolMap = new HashMap<String, Protocol>();
	
	public abstract void acceptConnections() throws CommunicationException;
	
	public synchronized void addProtocol(Protocol protocol) {
		protocolMap.put(protocol.getAcceptedMessageType(), protocol);
	}
	
	public synchronized void removeProtocol(Protocol protocol) {
		protocolMap.remove(protocol.getAcceptedMessageType());
	}
	
	public void removeAllProtocols() {
		protocolMap = new HashMap<String, Protocol>();
	}

	protected Message processMessage(Message message) {
		Protocol p = protocolMap.get(message.getType()); 
		return p.processMessage(message);
	}
	
}
