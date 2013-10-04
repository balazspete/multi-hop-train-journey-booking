package communication.messages;

import java.io.Serializable;

/**
 * A skeleton of a message wrapper of objects used for communication between two nodes
 * @author Balazs Pete
 *
 */
public abstract class Message implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8221561880823153671L;
	
	/**
	 * Get the type of the message
	 * @return The type of the message
	 */
	public abstract String getType();
	
	/**
	 * Get the contents of the message
	 * @return A Serializable object contained by the Message
	 */
	public abstract Serializable getContents();
	
	/**
	 * Set a Serializable object as the content of the message while overriding any previously stored content
	 * @param content The Serializable object to be contained by this Message 
	 */
	public abstract void setContents(Serializable content);
	
}
