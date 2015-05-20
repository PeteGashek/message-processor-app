package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import play.libs.Json;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Repository for JSON messages.
 * 
 * @author zstorok
 */
public class MessageRepository {

	private static final String MESSAGE_PREFIX = "message:";
	private static final String MESSAGE_KEY_SET = "message-key-set";

	private JedisPool pool;

	public MessageRepository(JedisPool pool) {
		this.pool = pool;
	}

	/**
	 * Persists the JSON message.
	 * 
	 * @param message
	 *            the message to persist
	 * @throws RepositoryException
	 *             if an error occurred in the underlying datastore
	 */
	public void persist(JsonNode message) throws RepositoryException {
		try (Jedis j = pool.getResource()) {
			String key = MESSAGE_PREFIX + UUID.randomUUID().toString();
			// save message using random key
			j.set(key, Json.stringify(message));
			// save random key into key set
			j.sadd(MESSAGE_KEY_SET, key);
		} catch (JedisException e) {
			throw new RepositoryException("Error when persisting message.", e);
		}
	}

	/**
	 * Returns all JSON messages stored in the system.
	 * 
	 * @return all messages stored in the system
	 * @throws RepositoryException
	 *             if an error occurred in the underlying datastore
	 */
	public List<JsonNode> findAll() throws RepositoryException {
		try (Jedis j = pool.getResource()) {
			List<JsonNode> messages = new ArrayList<JsonNode>();
			Set<String> keys = j.smembers(MESSAGE_KEY_SET);
			if (keys.isEmpty()) {
				return Collections.emptyList();
			}
			for (String key : keys) {
				String value = j.get(key);
				if (value != null) {
					messages.add(Json.parse(value));
				}
			}
			return messages;
		} catch (JedisException e) {
			throw new RepositoryException("Error when retrieving messages.", e);
		}
	}
}
