package communication.protocols;

import java.util.*;

import communication.messages.*;
import data.request.DataRequest;

/**
 * A protocol to handle {@link DataRequestMessage}s and reply with a {@link DataRequestReplyMessage}
 * @author Balazs
 *
 * @param <DATA> The type of the requested data
 */
public class DataRequestHandlingProtocol<DATA> extends RequestHandlingProtocol {

	private Set<DATA> data;
	private String type;
	
	/**
	 * Create a new instance of {@link DataRequestHandlingProtocol} with the data set to query  
	 * @param dataSet The data-set to work on
	 */
	public DataRequestHandlingProtocol(Set<DATA> dataSet) {
		this(dataSet, null);
	}
	
	/**
	 * Create a new instance of {@link DataRequestHandlingProtocol} with the data set to query  
	 * @param dataSet The data-set to work on
	 * @param type The type of {@link DataRequestMessage} to accept
	 */
	public DataRequestHandlingProtocol(Set<DATA> dataSet, String type) {
		this.data = dataSet;
		this.type = type;
	}
	
	@Override
	public String getAcceptedMessageType() {
		return "DataRequestMessage" + (type == null ? "" : "<"+type+">");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Message processMessage(Message message) {
		DataRequest<DATA> request = (DataRequest<DATA>) message.getContents();
		Set<DATA> requestedData = new HashSet<DATA>();
		
		for (DATA entry : data) {
			if (request.isRequested(entry)) {
				requestedData.add(entry);
			}
		}
		
		return new DataRequestReplyMessage<Set<DATA>>(requestedData);
	}
}
