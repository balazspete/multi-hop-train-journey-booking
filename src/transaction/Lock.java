package transaction;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import transaction.Lock.Token.LockType;

import com.rits.cloning.Cloner;

/**
 * An object used to lock a resource
 * @author Balazs Pete
 *
 * @param <T> The type of the locked resource
 */
public class Lock<T> {
	
	static public class Token {
		public enum LockType {
			READ, WRITE
		}
		
		private String id;
		private LockType type;
		
		public Token(LockType type) {
			id = new BigInteger(130, new SecureRandom()).toString(32);
			this.type = type;
		}
		
		public LockType getLockType() {
			return type;
		}
		
		public boolean equals(Token other) {
			return id.equals(other.getId());
		}
		
		protected String getId() {
			return id;
		}
		
		public String toString() {
			return id;
		}
	}
	
	private boolean writeMode = false; 
	
	private HashSet<Token> currentLocks = new HashSet<Token>();
	private ConcurrentLinkedQueue<Token> pendingQueue = new ConcurrentLinkedQueue<Token>();
	
	ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	
	protected T lockedData;
	protected Cloner cloner = new Cloner();
	
	/**
	 * Create a new lock with its stored data
	 * @param data The resource to be made lockable
	 */
	public Lock(T data) {
		lockedData = cloner.deepClone(data);
	}
	
	/**
	 * Get a copy of the stored resource
	 * @return A deep copy of the resource within the lock
	 * @throws LockException 
	 */
	public T getReadable(Token token) throws LockException {
		if (!currentLocks.contains(token)) {
			throw new LockException("You do not have a read lock on the item");
		}
		
		return cloner.deepClone(lockedData);
	}
	
	/**
	 * Get the stored resource
	 * @return The resource stored in the lock
	 * @throws LockException Thrown if the thread has not secured a write lock 
	 */
	public T getWriteable(Token token) throws LockException {
		if (!writeMode || !currentLocks.contains(token)) {
			throw new LockException("You do not have a write lock on the item");
		}
		
		return lockedData;
	}
	
	/**
	 * Get a read lock
	 */
	public synchronized Token readLock() {
		Token t = new Token(LockType.READ);
		pendingQueue.add(t);
		
		while (writeMode || !pendingQueue.peek().equals(t)) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		
		
		currentLocks.add(pendingQueue.remove());
		notifyAll();
		return t;
	}
	
	/**
	 * Discard the read lock
	 * @throws LockException 
	 */
	public boolean readUnlock(Token token) {
		//rwl.readLock().unlock();
		return removeLock(token);
	}
	
	/**
	 * Get a write lock
	 */
	public synchronized Token writeLock() {
		Token t = new Token(LockType.WRITE);
		pendingQueue.add(t);
		
		while (currentLocks.size() > 0 || !pendingQueue.peek().equals(t)) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		
		writeMode = true;
		currentLocks.add(pendingQueue.remove());
		
		notifyAll();
		
		return t;
	}
	
	/**
	 * Discard the write lock
	 * @return True if the lock has been removed
	 */
	public boolean writeUnlock(Token token) {
		return removeLock(token);
	}
	
	private synchronized boolean removeLock(Token t) {
		if (!currentLocks.contains(t)) {
			return false;
		}
		
		if (writeMode) 
			writeMode = false;
		
		currentLocks.remove(t);
		notifyAll();
		return true;
	}
	
	public final static Lock<Integer> lock = new Lock<Integer>(new Integer(2));
	public static void main(String[] args) {
		

		Runnable b = new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
//				try {
//					wait(200);
//				} catch (InterruptedException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
				
				Token t = lock.writeLock();
				try {
					System.out.println(lock.getWriteable(t));
				} catch (LockException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					System.out.println("DONE B");
					lock.writeUnlock(t);
				}
				
					
			}
		};
		Runnable a = new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Token t = lock.readLock();
				try {
					System.out.println("b:"+lock.getReadable(t));
				} catch (LockException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					System.out.println("DONE A");
					lock.readUnlock(t);
				}
				
				
				//lock.readUnlock(t);
					
				
			}
		};
		

		new Thread(a).start();
		//
		new Thread(b).start();new Thread(b).start();new Thread(b).start();
		new Thread(a).start();new Thread(a).start();new Thread(a).start();
		new Thread(a).start();new Thread(a).start();new Thread(a).start();
		//
		
		
		
	}
}
