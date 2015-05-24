package models;

import java.util.Objects;

import play.libs.Json;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import akka.actor.Props;
import akka.actor.UntypedActor;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Actor for publishing messages using the Redis pus/sub service.
 * 
 * @author zstorok
 *
 */
public class RedisPublisher extends UntypedActor {

	private final JedisPool pool;

	public RedisPublisher(JedisPool pool) {
		this.pool = Objects.requireNonNull(pool, "Pool must not be null.");
	}

	public static Props props(JedisPool pool) {
        return Props.create(RedisPublisher.class, () -> new RedisPublisher(pool));
    }
	
	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof JsonNode) {
			JsonNode jsonNode = (JsonNode) message;
			Jedis jedis = pool.getResource();
			try {
				jedis.publish(Constants.CHANNEL_NAME, Json.stringify(jsonNode));
			} finally {
				pool.returnResource(jedis);
			}
		}
	}
}
