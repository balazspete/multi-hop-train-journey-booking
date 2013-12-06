package node.company;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import data.trainnetwork.BookableSection;
import data.trainnetwork.NoSuchSeatException;
import data.trainnetwork.Seat;
import data.trainnetwork.Section;
import data.trainnetwork.SectionFullException;
import transaction.FailedTransactionException;
import transaction.Lock.Token;
import transaction.LockException;
import transaction.SudoTransactionContent;
import transaction.TransactionContent;
import transaction.Vault;

/**
 * CreateGet the {@link TransactionContent} for the required transaction type 
 * @author Balazs Pete
 *
 */
public abstract class TransactionContentGenerator extends TransactionContent<String, Vault<BookableSection>, Set<Seat>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3685066724312335230L;

	@Deprecated
	public static TransactionContent<String, Vault<BookableSection>, Set<Seat>> getTestContent() {
		TransactionContent<String, Vault<BookableSection>, Set<Seat>> c 
			= new TransactionContent<String, Vault<BookableSection>, Set<Seat>>() {
			private static final long serialVersionUID = -8167406104784795108L;
			@Override
			public void script(Token t) throws FailedTransactionException, LockException {
				System.out.println("Transaction hello test");
			}
		};
		
		return c;
	}
	
	/**
	 * Get the {@link TransactionContent} for prebooking seats
	 * @param sections The sections we wish to book seats for
	 * @return The generated transaction content
	 */
	public static SudoTransactionContent<String, Vault<BookableSection>, Set<Seat>> getSeatPreBookingContent(final HashSet<Section> sections) {
		SudoTransactionContent<String, Vault<BookableSection>, Set<Seat>> content 
				= new SudoTransactionContent<String, Vault<BookableSection>, Set<Seat>>() {
			/**/
			private static final long serialVersionUID = 4315165970519232479L;

			@Override
			public void script(Token t) throws FailedTransactionException, LockException {
				Map<String, Vault<BookableSection>> data = dataLock.getWriteable(t);
				
				HashSet<Vault<BookableSection>> vaults = new HashSet<Vault<BookableSection>>();
				for (Section s : sections) {
					Vault<BookableSection> section;
					if (data.containsKey(s.getID())) {
						section = data.get(s.getID());	
					} else {
						BookableSection _section = BookableSection.getSectionFromString(s.toString());
						section = new Vault<BookableSection>(_section);
						data.put(_section.getID(), section);
					}
					
					manager.writeLock(section);
					vaults.add(section);
				}
				
				dataToReturn = new HashSet<Seat>();
				for (Vault<BookableSection> vault : vaults) {
					BookableSection section = vault.getWriteable(manager.getToken(vault));
					
					try {
						Seat seat = section.preReserve();
						dataToReturn.add(seat);
					} catch (SectionFullException e) {
						throw new FailedTransactionException(e.getMessage());
					}
				}
			}
		};
				
		return content;
	}
	
	/**
	 * Get the {@link TransactionContent} for booking seats
	 * @param sections The sections we wish to book seats for
	 * @return The generated transaction content
	 */
	public static TransactionContent<String, Vault<BookableSection>, Set<Seat>> getSeatReservingContent(final HashSet<Seat> seats) {
		TransactionContent<String, Vault<BookableSection>, Set<Seat>> content 
				= new TransactionContent<String, Vault<BookableSection>, Set<Seat>>() {
			/**/
			private static final long serialVersionUID = -5015419185790185115L;

			@Override
			public void script(Token t) throws FailedTransactionException, LockException {
				Map<String, Vault<BookableSection>> data = dataLock.getReadable(t);
				
				for (Seat seat : seats) {
					String sectionId = seat.getSectionId();
					
					Vault<BookableSection> vault = data.get(sectionId);
					manager.writeLock(vault);
					
					try {
						vault.getWriteable(manager.getToken(vault)).reserve(seat);
					} catch (NoSuchSeatException e) {
						// This should not be happening as seats have been already pre-reserved, but just in case...
						throw new FailedTransactionException("Failed to book seat (id:" + seat.getId() + "):" + e.getMessage());
					}
				}
				
				// Just return the input, if transaction has failed we won't get to this point
				dataToReturn = seats;
			}
		};
		
		return content;
	}
	
	/**
	 * Get the {@link TransactionContent} for cancelling seats
	 * @param sections The seats we wish to cancel
	 * @return The generated transaction content
	 */
	public static TransactionContent<String, Vault<BookableSection>, Set<Seat>> getSeatCancellingContent(final HashSet<Seat> seats) {
		TransactionContent<String, Vault<BookableSection>, Set<Seat>> content 
				= new TransactionContent<String, Vault<BookableSection>, Set<Seat>>() {
			/**/
			private static final long serialVersionUID = -5015419185790185115L;

			@Override
			public void script(Token t) throws FailedTransactionException, LockException {
				Map<String, Vault<BookableSection>> data = dataLock.getReadable(t);
				
				for (Seat seat : seats) {
					String sectionId = seat.getSectionId();
					
					Vault<BookableSection> vault = data.get(sectionId);
					manager.writeLock(vault);
					
					try {
						vault.getWriteable(manager.getToken(vault)).undoReserve(seat);
					} catch (NoSuchSeatException e) {
						e.printStackTrace();
						// This should not be happening as seats have been already reserved, but just in case...
						throw new FailedTransactionException("Failed to cancel seat (id:" + seat.getId() + "):" + e.getMessage());
					}
				}
				
				// Just return the input, if transaction has failed we won't get to this point
				dataToReturn = seats;
			}
		};
		
		return content;
	}
	
	/**
	 * Get the {@link TransactionContent} for cancelling prebookings
	 * @param sections The seats we wish to cancel
	 * @return The generated transaction content
	 */
	public static TransactionContent<String, Vault<BookableSection>, Set<Seat>> getSeatPrebookCancelContent(final HashSet<Seat> seats) {
		TransactionContent<String, Vault<BookableSection>, Set<Seat>> content 
				= new TransactionContent<String, Vault<BookableSection>, Set<Seat>>() {
			/**/
			private static final long serialVersionUID = -5015419185790185115L;

			@Override
			public void script(Token t) throws FailedTransactionException, LockException {
				Map<String, Vault<BookableSection>> data = dataLock.getReadable(t);
				
				for (Seat seat : seats) {
					String sectionId = seat.getSectionId();
					
					Vault<BookableSection> vault = data.get(sectionId);
					manager.writeLock(vault);
					
					try {
						vault.getWriteable(manager.getToken(vault)).undoPreReserve(seat);
					} catch (NoSuchSeatException e) {
						e.printStackTrace();
						// This should not be happening as seats have been already pre-reserved, but just in case...
						throw new FailedTransactionException("Failed to delete pre-reserved seat (id:" + seat.getId() + "):" + e.getMessage());
					}
				}
				
				// Just return the input, if transaction has failed we won't get to this point
				dataToReturn = seats;
			}
		};
		
		return content;
	}
}
