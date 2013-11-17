package communication.messages;

import java.io.Serializable;

/**
 * Message used to acknowledge receipt of a Data transfer
 * @author Balazs Pete
 *
 */
public class DataTransferReplyMessage extends Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1601229647659443841L;

	private String transferId;
	
	/**
	 * Create a new {@link DataTransferReplyMessage}
	 * @param transferId The id of the transfer to acknowledge
	 */
	public DataTransferReplyMessage(String transferId) {
		this.transferId = transferId;
	}
	
	@Override
	public String getType() {
		return "DataTransferReplyMessage";
	}

	@Override
	public Serializable getContents() {
		return transferId;
	}

	@Override
	public void setContents(Serializable content) {
		this.transferId = (String) content;
	}

}
