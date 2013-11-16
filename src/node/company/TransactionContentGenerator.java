package node.company;

import data.trainnetwork.BookableSection;
import data.trainnetwork.Seat;
import data.trainnetwork.SectionFullException;
import transaction.FailedTransactionException;
import transaction.TransactionContent;
import transaction.Vault;

public abstract class TransactionContentGenerator extends TransactionContent<String, Vault<BookableSection>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3685066724312335230L;

	public static TransactionContent<String, Vault<BookableSection>> getTestContent() {
		TransactionContent<String, Vault<BookableSection>> c 
			= new TransactionContent<String, Vault<BookableSection>>() {
			private static final long serialVersionUID = -8167406104784795108L;
			@Override
			public void run() throws FailedTransactionException {
				
				
				Vault<BookableSection> d = data.get("---");
				BookableSection s = (BookableSection) manager.writeLock(d);
				
				try {
					Seat _s = s.preReserve();
				} catch (SectionFullException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				System.out.println(s);
				
			}
		};
		
		return c;
	}
}
