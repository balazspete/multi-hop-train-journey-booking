package communication.messages;

import java.io.Serializable;

/**
 * A message to request a {@link ClusterInfo}
 * @author Balazs Pete
 *
 */
public class ClusterHelloMessage extends Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8662740437625969549L;

	@Override
	public String getType() {
		return "ClusterHelloMessage";
	}

	@Override
	public Serializable getContents() {
		return null;
	}

	@Override
	public void setContents(Serializable content) {
	}

}
