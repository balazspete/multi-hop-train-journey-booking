package data.request;

import data.trainnetwork.BookableSection;

/**
 * Data request used to retrieve all BookableSection data from a data store
 * @author Balazs Pete
 *
 */
public class BookableSectionDataRequest extends DataRequest<BookableSection> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2687725577488290473L;

	@Override
	public boolean isRequested(BookableSection dataEntry) {
		return true;
	}
}
