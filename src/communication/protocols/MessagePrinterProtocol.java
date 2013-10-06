package communication.protocols;

import communication.messages.Message;

/**
 * A simple {@link Protocol} to print out the received message (no reply created)
 * @author Balazs Pete
 *
 */
public class MessagePrinterProtocol implements Protocol {

	@Override
	public String getAcceptedMessageType() {
		return "TextMessage";
	}

	@Override
	public Message processMessage(Object message) {
		System.out.println(((Message) message).getContents());
		return null;
	}
}
