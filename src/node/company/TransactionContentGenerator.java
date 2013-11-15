package node.company;

import java.util.Map;

import org.joda.time.DateTime;

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
				BookableSection s = new BookableSection("section", 1, DateTime.now(), 10, 10);
				System.out.println("EXECUTING");
				
				Map <String, Vault<BookableSection>> _data = (Map<String, Vault<BookableSection>>) manager.writeLock(new Vault(data));
				_data.put(s.getID(), new Vault<BookableSection>(s));
				
				
			}
		};
		
		return c;
	}
	
	
}
