import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import models.MessageRepository;
import models.RepositoryException;

import org.junit.Test;

import play.libs.Json;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Unit test for {@link MessageRepository}.
 * 
 * @author zstorok
 */
public class MessageRepositoryTest {

	@Test(expected=NullPointerException.class)
	public void persist_null() throws RepositoryException {
		MessageRepository repository = mockEmptyMessageRepository();
		repository.persist(null);
	}
	
	@Test
	public void persist_JsonNode() throws RepositoryException {
		MessageRepository repository = mockEmptyMessageRepository();
		repository.persist(Json.newObject().put("foo", "bar"));
	}
	
	@Test
	public void findAll_empty() throws RepositoryException {
		MessageRepository repository = mockEmptyMessageRepository();
		List<JsonNode> all = repository.findAll();
		assertNotNull(all);
		assertTrue(all.isEmpty());
	}

	@Test
	public void findAll_singleResult() throws RepositoryException {
		String messageKey = "1";
		String messageValue = "{\"foo\":\"bar\"}";
		JsonNode messageValueJson = Json.parse(messageValue);
		MessageRepository repository = mockSingleItemMessageRepository(messageKey, messageValue);
		
		List<JsonNode> all = repository.findAll();
		assertNotNull(all);
		assertEquals(1, all.size());
		assertEquals(messageValueJson, all.get(0));
	}

	private MessageRepository mockEmptyMessageRepository() {
		JedisPool pool = mock(JedisPool.class);
		Jedis jedis = mock(Jedis.class);
		when(pool.getResource()).thenReturn(jedis);
		MessageRepository repository = new MessageRepository(pool);
		return repository;
	}
	
	private MessageRepository mockSingleItemMessageRepository(String key, String value) {
		JedisPool pool = mock(JedisPool.class);
		Jedis jedis = mock(Jedis.class);
		when(jedis.smembers("message-key-set")).thenReturn(Collections.singleton(key));
		when(jedis.get(key)).thenReturn(value);
		when(pool.getResource()).thenReturn(jedis);
		MessageRepository repository = new MessageRepository(pool);
		return repository;
	}
}
