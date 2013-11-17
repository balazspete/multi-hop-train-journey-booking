package communication.protocols;

import java.util.Set;

import communication.messages.DataTransferMessage;
import communication.messages.DataTransferReplyMessage;
import communication.messages.Message;
import data.request.DataTransfer;

/**
 * Protocol to handle a data transfer
 * @author Balazs Pete
 *
 * @param <DATA>
 */
public class DataTransferHandlingProtocol<DATA> extends RequestHandlingProtocol {

	private Set<DATA> data;
	private String type;
	
	public DataTransferHandlingProtocol(Set<DATA> dataSet) {
		this(dataSet, null);
	}
	
	public DataTransferHandlingProtocol(Set<DATA> dataSet, String type) {
		this.data = dataSet;
		this.type = type;
	}
	
	@Override
	public String getAcceptedMessageType() {
		return "DataTransferMessage" + (type == null ? "" : "<" + type + ">");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Message processMessage(Message message) {
		DataTransferMessage<DATA> msg = (DataTransferMessage<DATA>) message;
		DataTransfer<DATA> transfer = (DataTransfer<DATA>) msg.getContents();
		Set<DATA> transferredData = transfer.getData();
		
		for (DATA entry : transferredData) {
			data.add(entry);
		}
		
		return new DataTransferReplyMessage(msg.getTransferId());
	}

	@Override
	public boolean hasReply() {
		return true;
	}

}
