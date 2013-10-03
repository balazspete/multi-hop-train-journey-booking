package communication.messages;

public abstract class Message {

	protected final String messageType = "Message";
	
	public String getType() {
		return messageType;
	}
	
	public abstract String getContents();
	
	public abstract void setContents(Object content);
	
}
