package communication.broadcast;

import java.util.*;
import java.util.concurrent.*;

import communication.messages.*;

public abstract class BroadcastClientHandler extends Thread {

	private final int MAX_TRIES = 3;
	
	private boolean sendBroadcasts = true;
	
	private Queue<Message> messageQueue = new ConcurrentLinkedQueue<Message>();
	private Object monitor = null;
	
	/**
	 * Set the monitor to be used by the {@link BroadcastClientHandler}s
	 * @param monitor The monitor to be used
	 */
	protected void setMonitor(Object monitor) {
		this.monitor = monitor;
	}
	
	/**
	 * Add a {@link Message} to be sent by the queue
	 * @param message The {@link Message} to be sent
	 */
	public void addMessage(Message message) {
		messageQueue.add(message);
	}
	
	/**
	 * Add a {@link Message} to be sent by the queue
	 * @param message The {@link Message} to be sent
	 * @param isImportant Flag marking if the {@link Message} is important
	 */
	public void addMessage(Message message, boolean isImportant) {
		message.setImportant(isImportant);
		messageQueue.add(message);
	}
	
	/**
	 * Terminate broadcasts
	 */
	protected void endBroadcasts() {
		sendBroadcasts = false;
	}
	
	/**
	 * Determine if the handler is still connected to the {@link BroadcastSocketReceiver}
	 * @return True of the connection is alive, false otherwise
	 */
	public boolean isConnected() {
		return sendBroadcasts;
	}
	
	/**
	 * Send the input {@link Message}
	 * @param message The {@link Message} to be sent
	 */
	protected abstract boolean sendMessage(Message message);
	
	@Override
	public void run() {
		synchronized(monitor) {
			while(sendBroadcasts) {
				try {
					monitor.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				sendMessages();
				monitor.notify();
			}
		}
	}

	/**
	 * Try to send the {@link Message}s in the queue
	 */
	private void sendMessages() {
		Queue<Message> newQueue = new ConcurrentLinkedQueue<Message>();
		for(Message message : messageQueue) {
			int tries = 0;
			boolean success = false;
			
			while (!success && tries++ < MAX_TRIES) {
				success = sendMessage(message);
			}
			
			if(!success && message.isImportant()) newQueue.add(message);
		}
		
		messageQueue = newQueue;
	}

}
