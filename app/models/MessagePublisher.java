package models;

import java.util.Objects;

import play.libs.Json;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.fasterxml.jackson.databind.JsonNode;

public class MessagePublisher {

	private final JedisPool pool;

	public MessagePublisher(JedisPool pool) {
		this.pool = Objects.requireNonNull(pool, "Pool must not be null.");
	}
	
	public void publish(JsonNode message) {
		Objects.requireNonNull(message, "Message must not be null.");
		try (Jedis jedis = pool.getResource()) {
			jedis.publish(Constants.CHANNEL_NAME, Json.stringify(message));
		}
	}
}
