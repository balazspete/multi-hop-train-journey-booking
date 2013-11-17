package communication.messages;

import java.io.Serializable;

import data.system.NodeInfo;

/**
 * Message used to reply to a {@link HelloMessage}
 * @author Balazs Pete
 *
 */
public class HelloReplyMessage extends Message {

	private static final long serialVersionUID = 3552804975674803333L;
	
	@Override
	public String getType() {
		return "HelloReplyMessage";
	}
	
	private HelloType type;

	@Override
	public Serializable getContents() {
		return type;
	}

	@Override
	public void setContents(Serializable content) {
		type = (HelloType) content;
	}
	
	private NodeInfo helloer = null;
	
	/**
	 * Set the information of the node who sent the `Hello`
	 * @param helloer The {@link NodeInfo} of the helloer
	 */
	public void setHelloer(NodeInfo helloer) {
		this.helloer = helloer;
	}
	
	/**
	 * Get the information of the node who sent the `Hello`
	 */
	public NodeInfo getHelloer() {
		return helloer;
	}
}
