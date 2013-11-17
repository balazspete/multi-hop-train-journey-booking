package communication.messages;

/**
 * The type of message indicating whether 
 *  - the node is requesting to join or to leave
 *  - the node has added or removed the requesting node to/from its list of recipients 
 *  
 * @author Balazs Pete
 *
 */
public enum HelloType {
	HI, BYE
}
