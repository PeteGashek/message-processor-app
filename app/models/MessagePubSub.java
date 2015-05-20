package models;

import java.util.Objects;

import play.Logger;
import play.libs.Json;
import redis.clients.jedis.JedisPubSub;

/**
 * Redis pub/sub listener implementation.
 * 
 * @author zstorok
 *
 */
public final class MessagePubSub extends JedisPubSub {

	private final MessageRepository messageRepository;
	private final String channel;

	public MessagePubSub(MessageRepository messageRepository, String channel) {
		this.channel = Objects.requireNonNull(channel, "Channel name must not be null");
		this.messageRepository = Objects.requireNonNull(messageRepository, "Message repository must not be null.");
	}

	@Override
	public void onMessage(String channel, String messageBody) {
		if (!channel.equals(this.channel)) {
			return;
		}
		try {
			messageRepository.persist(Json.parse(messageBody));
		} catch (RepositoryException e) {
			Logger.error("Error in message consumer.", e);
		}
	}

	@Override
	public void onPMessage(String arg0, String arg1, String arg2) {
	}

	@Override
	public void onPSubscribe(String arg0, int arg1) {
	}

	@Override
	public void onPUnsubscribe(String arg0, int arg1) {
	}

	@Override
	public void onSubscribe(String arg0, int arg1) {
	}

	@Override
	public void onUnsubscribe(String arg0, int arg1) {
	}
}