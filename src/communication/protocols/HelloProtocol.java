package communication.protocols;

import java.util.Set;

import communication.messages.HelloReplyMessage;
import communication.messages.HelloType;
import communication.messages.Message;
import data.system.NodeInfo;

public class HelloProtocol implements Protocol {

	private Set<NodeInfo> nodes;
	
	public HelloProtocol(Set<NodeInfo> nodes) {
		this.nodes = nodes;
	}
	
	@Override
	public String getAcceptedMessageType() {
		return "HelloMessage";
	}

	@Override
	public Message processMessage(Message message) {
		NodeInfo node = message.getSender();
		
		Message reply = new HelloReplyMessage();
		
		HelloType type = (HelloType) message.getContents();
		if (type == HelloType.HI) {
			if (!nodes.contains(node)) {
				nodes.add(node);
			}
			
			reply.setContents(HelloType.HI);
		} else {
			if (!nodes.contains(node)) {
				nodes.remove(node);
			}
			
			reply.setContents(HelloType.BYE);
		}
	
		return reply;
	}

	@Override
	public boolean hasReply() {
		return true;
	}
}
