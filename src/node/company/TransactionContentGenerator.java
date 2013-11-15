package node.company;

import data.trainnetwork.BookableSection;
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
				// TODO Auto-generated method stub
//				BookableSection s = new BookableSection("section", 1, DateTime.now(), 10, 10);
//				System.out.println("EXECUTING");
//				data.put(s.getID(), new Vault<BookableSection>(s));
				System.out.println("tc:hello");
			}
		};
		
		return c;
	}
	
	
}
