package communication.messages;

import java.io.Serializable;

public class ErrorMessage extends Message{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2027800737509101804L;

	private String error;
	
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
