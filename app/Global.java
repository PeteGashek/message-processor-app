import java.util.concurrent.TimeUnit;

import models.Constants;
import models.MessagePubSub;
import models.MessageRepository;
import models.WebSocketSenderSupervisor;
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
		// initialize Redis listener
		JedisPool pool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
		messagePubSub = new MessagePubSub(new MessageRepository(pool), Constants.CHANNEL_NAME);
		Akka.system().scheduler().scheduleOnce(Duration.create(10, TimeUnit.MILLISECONDS), new Runnable() {
			@Override
			public void run() {
				Jedis j = pool.getResource();
				try {
					Logger.info("Subscribing to messages channel.");
					j.subscribe(messagePubSub, Constants.CHANNEL_NAME);
				} finally {
					pool.returnResource(j);
				}
			}
		}, Akka.system().dispatcher());
		// initialize sender supervisor actor
		Akka.system().actorOf(WebSocketSenderSupervisor.props(), Constants.SUPERVISOR_ACTOR_NAME);
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