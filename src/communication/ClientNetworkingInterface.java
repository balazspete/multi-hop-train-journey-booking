package communication;

import communication.messages.Message;

public interface ClientNetworkingInterface {

	public void sendMessage(Message message) throws CommunicationException;
	
	public Message getMessage() throws CommunicationException;
	
}
