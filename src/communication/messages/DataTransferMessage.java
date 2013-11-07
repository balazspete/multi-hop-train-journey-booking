package communication.messages;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;

import data.request.DataTransfer;

/**
 * A message used to transfer data using a data transfer
 * @author Balazs Pete
 *
 * @param <DE> The type of data to transfer
 */
public class DataTransferMessage<DE> extends Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = 437489910381343481L;

	private DataTransfer<DE> transfer;
	private String type, transferId;
	
	/**
	 * Create a new data transfer message
	 * @param transfer The data transfer to send
	 */
	public DataTransferMessage(DataTransfer<DE> transfer) {
		this(transfer, null);
	}
	
	/**
	 * Create a new data transfer message
	 * @param transfer The data transfer to send
	 * @param type The type of data to be sent
	 */
	public DataTransferMessage(DataTransfer<DE> transfer, String type) {
		this.transfer = transfer;
		this.type = type;
		this.transferId = new BigInteger(130, new SecureRandom()).toString(32);
	}
	
	@Override
	public String getType() {
		return "DataTransferMessage" + (type == null ? "" : "<" + type + ">");
	}

	@Override
	public Serializable getContents() {
		return transfer;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setContents(Serializable content) {
		transfer = (DataTransfer<DE>) content;
	}

	/**
	 * Get the auto-generated ID of this transfer
	 * @return The ID
	 */
	public String getTransferId() {
		return transferId;
	}
}
