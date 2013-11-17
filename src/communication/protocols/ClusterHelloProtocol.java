package communication.protocols;

import java.util.Set;

import communication.messages.ClusterHelloReplyMessage;
import communication.messages.Message;
import data.system.ClusterInfo;
import data.system.NodeInfo;

/**
 * A protocol used to relay the {@link ClusterInfo} to a requesting node
 * @author Balazs Pete
 *
 */
public class ClusterHelloProtocol implements Protocol {

	private String clusterName; 
	private Set<NodeInfo> nodeInfos;
	
	public ClusterHelloProtocol(String clusterName, Set<NodeInfo> nodeInfos) {
		this.clusterName = clusterName;
		this.nodeInfos = nodeInfos;
	}
	
	@Override
	public String getAcceptedMessageType() {
		return "ClusterHelloMessage";
	}

	@Override
	public Message processMessage(Message message) {
		ClusterInfo info = new ClusterInfo(clusterName);
		
		for (NodeInfo node : nodeInfos) {
			info.addLocation(node.getLocation());
		}
		
		return new ClusterHelloReplyMessage(info);
	}

	@Override
	public boolean hasReply() {
		return true;
	}

}
