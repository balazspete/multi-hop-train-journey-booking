package communication.messages;

public class TextMessage extends Message {

	protected String message = null;
	
	@Override
	public String getContents() {
		return message;
	}

	@Override
	public void setContents(Object content) {
		message = (String) content;
	}

}
