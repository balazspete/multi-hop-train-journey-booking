package node.data;

import java.util.Set;

import org.joda.time.DateTime;

import data.system.NodeInfo;
import data.trainnetwork.SectionInfo;
import data.trainnetwork.Station;

/**
 * A description for a data loading mechanism
 * @author Balazs Pete
 *
 */
public interface DataLoader {

	/**
	 * Retrieve data from a {@link DataRepository} within a given time-frame
	 * @param from The start date of the time-frame, or <b>null</b> to not specify 
	 * @param until The end date of the time-frame, or <b>null</b> to not specify
	 * @param getStations True if the list of {@link Station}s should be retrieved, false otherwise 
	 */
	public void getData(DateTime from, DateTime until, boolean getStations);
	
	/**
	 * @return The retrieved set of {@link Sections}
	 */
	public Set<SectionInfo> getSections();

	/**
	 * @return The retrieved set of {@link Station}s
	 */
	public Set<Station> getStations();

	/**
	 * @return The retrieved set of {@link NodeInfo}rmations
	 */
	public Set<NodeInfo> getNodeInfos();
}
