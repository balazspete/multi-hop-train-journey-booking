package communication;

import communication.messages.Message;

public interface ClientNetworkingInterface {

	public boolean createConnection() throws CommunicationException;
	
	public boolean endConnection() throws CommunicationException;
	
	public void sendMessage(Message message) throws CommunicationException;
	
	public Message getMessage() throws CommunicationException;
	
}
