package communication.messages;

import java.io.Serializable;

public abstract class Message implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8221561880823153671L;
	protected final String messageType = "Message";
	
	public String getType() {
		return messageType;
	}
	
	public abstract Object getContents();
	
	public abstract void setContents(Object content);
	
}
