package communication.messages;

import java.io.Serializable;

import data.system.ClusterInfo;

/**
 * Message used to relay a {@link ClusterInfo} to a requesting entity
 * @author Balazs Pete
 *
 */
public class ClusterHelloReplyMessage extends Message {

	private static final long serialVersionUID = 4151331983482991392L;

	private ClusterInfo info;
	
	public ClusterHelloReplyMessage(ClusterInfo info) {
		this.info = info;
	}
	
	@Override
	public String getType() {
		return "ClusterHelloReplyMessage";
	}

	@Override
	public Serializable getContents() {
		return info;
	}

	@Override
	public void setContents(Serializable content) {
		this.info = (ClusterInfo) content;
	}

}
