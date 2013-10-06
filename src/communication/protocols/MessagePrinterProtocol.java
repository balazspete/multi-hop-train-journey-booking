package communication.protocols;

import communication.messages.Message;

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
