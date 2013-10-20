package data.request;

import data.trainnetwork.Station;

/**
 * A {@link DataRequest} requesting {@link Station}s
 * @author Balazs Pete
 *
 */
public class StationDataRequest extends DataRequest<Station> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6929396275393980214L;

	@Override
	public boolean isRequested(Station dataEntry) {
		// Request information regarding all stations
		return true;
	}
}
