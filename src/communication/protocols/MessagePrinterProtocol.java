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
	public Message processMessage(Message message) {
		System.out.println(message.getContents());
		return null;
	}

	@Override
	public boolean hasReply() {
		return false;
	}
}
