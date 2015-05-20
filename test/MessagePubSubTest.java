import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import models.MessagePubSub;
import models.MessageRepository;
import models.RepositoryException;

import org.junit.Test;

import play.libs.Json;

/**
 * Unit test for {@link MessagePubSub}.
 * 
 * @author zstorok
 *
 */
public class MessagePubSubTest {

	@Test(expected=NullPointerException.class)
	public void constructor_nullRepository() {
		new MessagePubSub(null, "foo");
	}
	
	@Test(expected=NullPointerException.class)
	public void constructor_nullChannel() {
		new MessagePubSub(mock(MessageRepository.class), null);
	}
	
	@Test
	public void onMessage_sameChannel() throws RepositoryException {
		MessageRepository repository = mock(MessageRepository.class);
		String messageString = "{\"foo\":\"bar\"}";
		
		String channel = "channel";
		MessagePubSub messagePubSub = new MessagePubSub(repository, channel);
		messagePubSub.onMessage(channel, messageString);
		
		verify(repository).persist(Json.parse(messageString));
	}
	
	@Test
	public void onMessage_differentChannel() throws RepositoryException {
		MessageRepository repository = mock(MessageRepository.class);
		String messageString = "{\"foo\":\"bar\"}";
		
		String channel = "channel";
		MessagePubSub messagePubSub = new MessagePubSub(repository, channel);
		messagePubSub.onMessage("otherChannel", messageString);
		
		verify(repository, never()).persist(Json.parse(messageString));
	}
	
}
