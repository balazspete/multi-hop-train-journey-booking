package communication;

/**
 * An exception describing a communication error
 * @author Balazs Pete
 *
 */
public class CommunicationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1153395142101460769L;

	/**
	 * Create a new {@link CommunicationException}
	 * @param message The {@link Message} to be contained by the exception
	 */
	private CommunicationException(String message) {
		super(message);
	}
	
	public static final CommunicationException 
		UNREACHABLE_HOST 			= new CommunicationException("Specified host is unreachable or does not exist"),
		
		CANNOT_USE_PORT 			= new CommunicationException("Failed to use specified port"),
		CANNOT_OPEN_CONNECTION		= new CommunicationException("Failed to open connection"),
		CANNOT_CLOSE_CONNECTION		= new CommunicationException("Failed to close connection"),
		CANNOT_SERIALIZE_MESSAGE	= new CommunicationException("Failed to serialize message"),
		CANNOT_SEND_MESSAGE			= new CommunicationException("Failed to send message"),
		CANNOT_READ_MESSAGE			= new CommunicationException("Failed to open/read message"),
	
		CONNECTION_TERMINATED		= new CommunicationException("Connection with remote terminated");
}
