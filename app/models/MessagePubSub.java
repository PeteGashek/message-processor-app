package models;

import play.Logger;
import play.libs.Json;
import redis.clients.jedis.JedisPubSub;

public final class MessagePubSub extends JedisPubSub {
	
	private final MessageRepository messageRepository;

	public MessagePubSub(MessageRepository messageRepository) {
		this.messageRepository = messageRepository;
	}
	
	@Override
	public void onMessage(String channel, String messageBody) {
		if (!channel.equals(Constants.CHANNEL_NAME)) {
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