package communication.protocols;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import transaction.Lock.Token;
import transaction.LockException;
import transaction.ShallowLock;
import transaction.Vault;

import communication.messages.Message;
import communication.messages.SectionStatusUpdateRequestMessage;
import data.trainnetwork.BookableSection;
import data.trainnetwork.Section;

/**
 * A protocol to handle section status update requests
 * @author Balazs Pete
 *
 */
public class SectionStatusUpdateRequestHandlingProtocol implements Protocol {

	private ShallowLock<Map<String, Vault<BookableSection>>> sections;
	
	public SectionStatusUpdateRequestHandlingProtocol(ShallowLock<Map<String, Vault<BookableSection>>> sections) {
		this.sections = sections;
	}
	
	@Override
	public String getAcceptedMessageType() {
		return "SectionStatusUpdateRequestMessage";
	}

	@SuppressWarnings("unchecked")
	@Override
	public Message processMessage(Message message) {
		Set<Section> requested = (Set<Section>) message.getContents();
		HashSet<Section> toReturn = new HashSet<Section>();
		
		Token token = sections.readLock();
		try {
			Map<String, Vault<BookableSection>> _sections = sections.getReadable(token);
			for (Section section : requested) {
				Vault<BookableSection> vault = _sections.get(section.getID());
				if (vault != null) {
					Token _token = vault.readLock();
					try {
						Section _section = vault.getReadable(_token);
						toReturn.add(_section);
					} catch (LockException e) {
						toReturn.add(BookableSection.getSectionFromId(section.getID()));
					} finally {
						vault.readUnlock(_token);
					}
				} else {
					toReturn.add(BookableSection.getSectionFromId(section.getID()));
				}
			}
		} catch (LockException e) {
			System.err.println(e.getMessage());
			// Just return whatever we have...
		} finally {
			sections.readUnlock(token);
		}
		
		SectionStatusUpdateRequestMessage reply = new SectionStatusUpdateRequestMessage();
		reply.setContents(toReturn);
		
		return reply;
	}

	@Override
	public boolean hasReply() {
		return true;
	}

}
