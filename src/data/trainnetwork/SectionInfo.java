package data.trainnetwork;

import java.io.Serializable;

/**
 * An object containing a section and some additional detail
 * @author Balazs Pete
 *
 */
public class SectionInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6284726094757907863L;
	
	private Section section;
	private String startStationID, endStationID;

	public SectionInfo(Section section, String startStationID, String endStationID) {
		this.section = section;
		this.startStationID = startStationID;
		this.endStationID = endStationID;
	}
	
	/**
	 * @return the section
	 */
	public Section getSection() {
		return section;
	}

	/**
	 * @return the startStationID
	 */
	public String getStartStationID() {
		return startStationID;
	}

	/**
	 * @return the endStationID
	 */
	public String getEndStationID() {
		return endStationID;
	}
}
