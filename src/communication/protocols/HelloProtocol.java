package communication.protocols;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Iterator;
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
		if (node.getLocation().equals("127.0.0.1")) {
			try {
				String addr = Inet4Address.getLocalHost().getHostAddress();
				node.addLocation(addr);
			} catch (UnknownHostException e) {
				/*
				 * If this fails, the node might become unreachable 
				 * to other machines on the network
				 * It's not a fatal error anyways...
				 */
				System.err.println(e.getMessage());
			}
		}
		
		HelloReplyMessage reply = new HelloReplyMessage();
		
		HelloType type = (HelloType) message.getContents();
		if (type == HelloType.HI) {
			if (!containsNode(node)) {
				nodes.add(node);
			}
			
			reply.setContents(HelloType.HI);
		} else {
			if (!containsNode(node)) {
				nodes.remove(node);
			}
			
			reply.setContents(HelloType.BYE);
		}
		
		reply.setHelloer(message.getSender());
	
		return reply;
	}

	@Override
	public boolean hasReply() {
		return true;
	}
	
	private boolean containsNode(NodeInfo node) {
		Iterator<NodeInfo> _nodes = nodes.iterator();
		while (_nodes.hasNext()) {
			if (_nodes.next().equals(node)) {
				return true;
			}
		}
		return false;
	}
}
