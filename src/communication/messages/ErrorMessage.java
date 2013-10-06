package communication.messages;

import java.io.Serializable;

/**
 * A {@link Message} communication an error to another party
 * @author Balazs
 *
 */
public class ErrorMessage extends Message{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2027800737509101804L;

	private String error;
	
	/**
	 * Create a message with a specified error message
	 * @param errorMessage The error message text
	 */
	public ErrorMessage(String errorMessage) {
		error = errorMessage;
	}
	
	@Override
	public Serializable getContents() {
		return error;
	}

	@Override
	public void setContents(Serializable content) {
		error = (String) content;
	}

	@Override
	public String getType() {
		return "ErrorMessage";
	}
}
