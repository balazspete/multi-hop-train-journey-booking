package data.trainnetwork;

public class SectionInfo {

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
