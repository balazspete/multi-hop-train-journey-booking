package communication.protocols;

import communication.messages.Message;

public interface Protocol {

	public String getAcceptedMessageType();
	
	public Message processMessage(Object message);
	
}
