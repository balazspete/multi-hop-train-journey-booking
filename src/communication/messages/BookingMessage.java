package communication.messages;

import java.io.Serializable;
import java.util.Collection;

/**
 * A message used to send booking requests to dynamic data nodes
 * @author Balazs Pete
 *
 */
public class BookingMessage extends Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4918480221290207351L;

	/** 
	 * Described the action to take when receiving this message
	 * @author Balazs Pete
	 *
	 */
	public enum Action {
		PREBOOK, PREBOOK_DETELE, RESERVE, CANCEL
	}
	
	private Action action = null;
	private Collection content = null;
	
	public BookingMessage(Action action) {
		this.action = action;
	}
	
	@Override
	public String getType() {
		return "BookingMessage:" + action;
	}

	@Override
	public Serializable getContents() {
		return (Serializable) content;
	}

	@Override
	public void setContents(Serializable content) {
		this.content = (Collection) content;
	}
	
	/**
	 * Get a pre-booking message
	 * @return The message
	 */
	public static BookingMessage getPrebookMessage() {
		return new BookingMessage(Action.PREBOOK);
	}
	
	/**
	 * Get a booking message
	 * @return The message
	 */
	public static BookingMessage getBookMessage() {
		return new BookingMessage(Action.RESERVE);
	}
	
	/**
	 * Get a cancelling message
	 * @return The message
	 */
	public static BookingMessage getCancelMessage() {
		return new BookingMessage(Action.CANCEL);
	}
}
