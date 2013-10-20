package communication.messages;

import java.io.Serializable;

import data.request.DataRequest;

/**
 * A {@link Message} containing a {@link DataRequest} of type DE
 * @author Balazs Pete
 *
 * @param <DE> The class of the object that the containing {@link DataRequest} is requesting for
 */
public class DataRequestMessage<DE> extends Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2010473277469682599L;

	private DataRequest<DE> request;
	private String requestType;
	
	/**
	 * Create a new instance of {@link DataRequestMessage}
	 * @param request The {@link DataRequest} to send
	 */
	public DataRequestMessage(DataRequest<DE> request) {
		this(request, null);
	}
	
	/**
	 * Create a new instance of {@link DataRequestMessage}, with the request type specified
	 * @param request The {@link DataRequest} to send
	 * @param requestType The type of object the {@link DataRequest} is requesting
	 */
	public DataRequestMessage(DataRequest<DE> request, String requestType) {
		this.request = request;
		this.requestType = requestType;
	}
	
	@Override
	public String getType() {
		return "DataRequestMessage" + (requestType == null ? "" : "<"+requestType+">");
	}

	@Override
	public Serializable getContents() {
		return request;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setContents(Serializable content) {
		request = (DataRequest<DE>) content;
	}
}
