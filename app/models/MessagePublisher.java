package models;

import play.libs.Json;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.fasterxml.jackson.databind.JsonNode;

public class MessagePublisher {

	private final JedisPool pool;

	public MessagePublisher(JedisPool pool) {
		this.pool = pool;
	}
	
	public void publish(JsonNode message) {
		try (Jedis jedis = pool.getResource()) {
			jedis.publish(Constants.CHANNEL_NAME, Json.stringify(message));
		}
	}
}
