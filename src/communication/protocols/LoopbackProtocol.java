package communication.protocols;

import communication.messages.*;

/**
 * A simple protocol to send back the received message to the other party
 * @author Balazs Pete
 *
 */
public class LoopbackProtocol implements Protocol {

	@Override
	public String getAcceptedMessageType() {
		return "TextMessage".intern();
	}

	@Override
	public Message processMessage(Message message) {
		return (TextMessage) message;
	}
}
