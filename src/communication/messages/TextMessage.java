package communication.messages;

public class TextMessage extends Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7996405491053757702L;
	protected String message = null;
	
	protected final String messageType = "TextMessage";
	
	public TextMessage(String message) {
		this.message = message;
	}
	
	@Override
	public Object getContents() {
		return message;
	}

	@Override
	public void setContents(Object content) {
		message = (String) content;
	}

}
