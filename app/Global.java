import models.Constants;
import models.MessagePubSub;
import models.MessageRepository;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.Akka;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import scala.concurrent.duration.Duration;

import com.typesafe.plugin.RedisPlugin;

public class Global extends GlobalSettings {

	private MessagePubSub messagePubSub;

	public void onStart(Application app) {
		JedisPool pool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
		messagePubSub = new MessagePubSub(new MessageRepository(pool), Constants.CHANNEL_NAME);
		Akka.system().scheduler().scheduleOnce(Duration.Zero(), new Runnable() {
			@Override
			public void run() {
				try (Jedis j = pool.getResource()) {
					Logger.info("Subscribing to messages channel.");
					j.subscribe(messagePubSub, Constants.CHANNEL_NAME);
				}
			}
		}, Akka.system().dispatcher());
        Logger.info("Application has started");
    }

    public void onStop(Application app) {
    	if (messagePubSub != null && messagePubSub.isSubscribed()) {
    		messagePubSub.unsubscribe(Constants.CHANNEL_NAME);
    		Logger.info("Unsubscribed from messages channel.");
    	}
        Logger.info("Application shutdown...");
    }

}