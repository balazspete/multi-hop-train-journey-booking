package communication.messages;

import java.io.Serializable;

/**
 * A message used to send a `Hello` request to other nodes
 * @author Balazs Pete
 *
 */
public class HelloMessage extends Message {

	private static final long serialVersionUID = -426394594241784658L;
	
	@Override
	public String getType() {
		return "HelloMessage";
	}

	private HelloType type;
	
	public HelloMessage(HelloType type) {
		this.type = type;
	}
	
	@Override
	public Serializable getContents() {
		return type;
	}

	@Override
	public void setContents(Serializable content) {
		type = (HelloType) content;
	}
	
	public static HelloMessage getHi() {
		return new HelloMessage(HelloType.HI);
	}
	
	public static HelloMessage getBye() {
		return new HelloMessage(HelloType.BYE);
	}
}
