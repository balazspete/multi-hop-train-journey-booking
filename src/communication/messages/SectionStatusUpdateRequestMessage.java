package communication.messages;

import java.io.Serializable;
import java.util.HashSet;

import data.trainnetwork.Section;

/**
 * A message to express a request intention for a section status update
 * @author Balazs Pete
 *
 */
public class SectionStatusUpdateRequestMessage extends Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2650450723405524129L;

	private HashSet<Section> sections = new HashSet<Section>();
	
	@Override
	public String getType() {
		return "SectionStatusUpdateRequestMessage";
	}

	@Override
	public Serializable getContents() {
		return sections;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setContents(Serializable content) {
		sections = (HashSet<Section>) content;
	}
}
