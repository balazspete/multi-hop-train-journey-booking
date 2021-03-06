package communication.messages;

import java.io.Serializable;

import data.system.NodeInfo;

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

	private NodeInfo from = null;
	
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
	
	/**
	 * Get the location of the source node
	 * @return The address of the sender
	 */
	public NodeInfo getSender() {
		return from;
	}
	
	/**
	 * Set the location of the source node
	 * @param from The address of the sender
	 */
	public void setSender(NodeInfo from) {
		this.from = from;
	}
}
