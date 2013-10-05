package communication.broadcast;

import java.util.*;

import communication.CommunicationException;
import communication.messages.*;

/**
 * A generic description of a broadcast sender node
 * @author Balazs Pete
 *
 */
public abstract class BroadcastSender extends Thread {
	
	private Set<BroadcastClientHandler> connectionHandlers = new HashSet<BroadcastClientHandler>();
	private Object monitor = new Object();

	/**
	 * Start accepting connections from {@link BroadcastReceiver}s and handle new connections
	 * @throws CommunicationException Thrown if an error occurred during setup
	 */
	protected abstract void acceptConnections() throws CommunicationException;
	
	/**
	 * Add a {@link BroadcastClientHandler}
	 * @param handler The {@link BroadcastClientHandler} to add
	 */
	public void addConnectionHandler(BroadcastClientHandler handler) {
		handler.setMonitor(monitor);
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
		
		synchronized(monitor) {
			monitor.notifyAll();
		}
	}
	
	public void run() {
		try {
			acceptConnections();
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
	}
}
