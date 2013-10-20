package communication.messages;

import java.io.Serializable;

/**
 * A message containing a reply for a {@link DataRequestMessage}
 * @author Balazs Pete
 *
 * @param <DE> The type of data to transmit
 */
public class DataRequestReplyMessage<DE> extends Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5041931564515513813L;

	private DE data;
	
	/**
	 * Create a new instance of {@link DataRequestReplyMessage}
	 * @param data The requested data
	 */
	public DataRequestReplyMessage(DE data) {
		this.data = data;
	}
	
	@Override
	public String getType() {
		return "DataRequestReplyMessage";
	}

	@Override
	public Serializable getContents() {
		return (Serializable) data;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setContents(Serializable content) {
		this.data = (DE) content;
	}

}
