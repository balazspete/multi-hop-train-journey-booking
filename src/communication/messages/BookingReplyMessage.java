package communication.messages;

import java.io.Serializable;

/**
 * A message used to reply to a {@link BookingMessage}
 * @author Balazs Pete
 *
 */
public class BookingReplyMessage extends Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6523630926093753916L;

	public enum Result {
		SUCCESS, FAILURE
	}
	
	private Serializable content;
	
	@Override
	public String getType() {
		return "BookingReplyMessage";
	}

	@Override
	public Serializable getContents() {
		return content;
	}

	@Override
	public void setContents(Serializable content) {
		this.content = content;
	}

}
