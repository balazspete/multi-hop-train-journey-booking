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
	
	private Set<BroadcastReceiverHandler> connectionHandlers = new HashSet<BroadcastReceiverHandler>();
	private Object monitor = new Object();

	/**
	 * Start accepting connections from {@link BroadcastReceiver}s and handle new connections
	 * @throws CommunicationException Thrown if an error occurred during setup
	 */
	protected abstract void acceptConnections() throws CommunicationException;
	
	/**
	 * Add a {@link BroadcastReceiverHandler}
	 * @param handler The {@link BroadcastReceiverHandler} to add
	 */
	public void addConnectionHandler(BroadcastReceiverHandler handler) {
		handler.setMonitor(monitor);
		connectionHandlers.add(handler);
	}
	
	/**
	 * Remove a {@link BroadcastReceiverHandler}
	 * @param handler The {@link BroadcastReceiverHandler} to remove
	 */
	public void removeConnectionHandler(BroadcastReceiverHandler handler) {
		connectionHandlers.remove(handler);
	}
	
	/**
	 * Send a {@link Message} to all connected {@link BroadcastReceiver}s
	 * @param message The {@link Message} to send
	 */
	public void broadcastMessage(Message message) {
		for(BroadcastReceiverHandler handler : connectionHandlers) {
			if(handler.isAlive()) {
				handler.addMessage(message);
			} else {
				removeConnectionHandler(handler);
			}
		}
		
		synchronized(monitor) {
			monitor.notifyAll();
		}
	}
	
	@Override
	public void run() {
		try {
			acceptConnections();
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
	}
}
