package communication.broadcast;

import java.util.*;

import communication.messages.*;

/**
 * A generic description of a broadcast sender node
 * @author Balazs Pete
 *
 */
public abstract class BroadcastSender extends Thread {
	
	private Set<BroadcastClientHandler> connectionHandlers = new HashSet<BroadcastClientHandler>();

	/**
	 * Start accepting connections from {@link BroadcastReceiver}s
	 */
	public abstract void acceptConnections();
	
	/**
	 * Add a {@link BroadcastClientHandler}
	 * @param handler The {@link BroadcastClientHandler} to add
	 */
	public void addConnectionHandler(BroadcastClientHandler handler) {
		connectionHandlers.add(handler);
	}
	
	/**
	 * Remove a {@link BroadcastClientHandler}
	 * @param handler The {@link BroadcastClientHandler} to remove
	 */
	public void removeConnectionHandler(BroadcastClientHandler handler) {
		connectionHandlers.remove(handler);
	}
	
	/**
	 * Send a {@link Message} to all connected {@link BroadcastReceiver}s
	 * @param message The {@link Message} to send
	 */
	public void broadcastMessage(Message message) {
		for(BroadcastClientHandler handler : connectionHandlers) {
			handler.addMessage(message);
		}
	}
	
}
