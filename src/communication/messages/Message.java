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

	protected boolean important = false;
	
	/**
	 * Set the important flag of the message
	 * @param isImportant True if message should be important, false otherwise
	 */
	public void setImportant(boolean isImportant) {
		important = isImportant;
	}
	
	/**
	 * Poll the important flag of the message
	 * @return True if the message is important, false otherwise
	 */
	public boolean isImportant() {
		return important;
	}
	
	/**
	 * Get the type of the message
	 * @return The type of the message
	 */
	public abstract String getType();
	
	/**
	 * Get the contents of the message
	 * @return A {@link Serializable} object contained by the Message
	 */
	public abstract Serializable getContents();
	
	/**
	 * Set a {@link Serializable} object as the content of the message while overriding any previously stored content
	 * @param content The {@link Serializable} object to be contained by this {@link Message}
	 */
	public abstract void setContents(Serializable content);
	
}
