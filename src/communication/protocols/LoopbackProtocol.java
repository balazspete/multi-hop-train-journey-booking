package communication.protocols;

import communication.messages.*;

public class LoopbackProtocol implements Protocol {

	@Override
	public String getAcceptedMessageType() {
		return "TextMessage";
	}

	@Override
	public Message processMessage(Object message) {
		return (TextMessage) message;
	}

}
