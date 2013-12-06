package communication.messages;

import java.io.Serializable;

/**
 * A simple message containing a String
 * @author Balazs Pete
 *
 */
@Deprecated
public class TextMessage extends Message {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7996405491053757702L;
	protected String message = null;
	
	/**
	 * Create an empty TextMessage 
	 */
	public TextMessage() {
	}
	
	/**
	 * Create a TextMessage
	 * @param message The String to send 
	 */
	public TextMessage(String message) {
		this.message = message;
	}
	
	@Override
	public Serializable getContents() {
		return message;
	}

	@Override
	public void setContents(Serializable content) {
		message = (String) content;
	}
	
	/**
	 * Set a String as the content of the message
	 * @param content The String to set as the content
	 */
	public void setContents(String content) {
		message = content;
	}

	@Override
	public String getType() {
		return "TextMessage";
	}

}
