package data.request;

import org.joda.time.DateTime;

import data.trainnetwork.SectionInfo;

public class SectionDataRequest extends DataRequest<SectionInfo> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3145325602571971353L;
	
	private final static int DEFAULT_TO_DAYS = 2;
	
	private DateTime from, until;
	
	/**
	 * Create a {@link SectionDataRequest} for {@link Section}s with start times <b>equal to or later than <i>now</i></b> and <b>less than <i>now</i> plus 2 days</b> 
	 */
	public SectionDataRequest() {
		this(DateTime.now());
	}
	
	/**
	 * Create a {@link SectionDataRequest} for {@link Section}s with start times <b>equal to or later than <i>from</i></b> and <b>less than <i>from</i> plus 2 days</b>
	 * @param from The {@link DateTime} corresponding to the start times of the {@link Section}s 
	 */
	public SectionDataRequest(DateTime from) {
		this(from, from.plusDays(DEFAULT_TO_DAYS));
	}
	
	/**
	 * Create a {@link SectionDataRequest} for {@link Section}s with start times <b>equal to or later than <i>from</i></b> and <b>less than <i>from</i> plus <i>numberOfDays</i> days</b>
	 * @param from The {@link DateTime} corresponding to the start times of the {@link Section}s
	 * @param numberOfDays The length of requested section in days from <i>from</i> 
	 */
	public SectionDataRequest(DateTime from, int numberOfDays) {
		this(from, from.plusDays(numberOfDays));
	}
	
	/**
	 * Create a {@link SectionDataRequest} for {@link Section}s with start times <b>equal to or later than <i>from</i></b> and <b>less than <i>until</i></b>
	 * @param from The {@link DateTime} corresponding to the start times of the {@link Section}s
	 * @param until The {@link DateTime} marking the end of the requested range 
	 */
	public SectionDataRequest(DateTime from, DateTime until) {
		this.from = from;
		this.until = until;
	}

	@Override
	public boolean isRequested(SectionInfo section) {
		boolean fromCheck = false, untilCheck = false;
		
		if(from == null) fromCheck = true;
		if(until == null) untilCheck = true;
		
		if(fromCheck && untilCheck) {
			return true;
		} else {
			DateTime time;
			try {
				time = section.getSection().getStartTime();
			} catch (Exception e) {
				return false;
			}
		
			if(!fromCheck && from.isBefore(time)) fromCheck = true;
			if(!untilCheck && until.isAfter(time)) untilCheck = true;
			
			return fromCheck && untilCheck;
		}
	}
}
