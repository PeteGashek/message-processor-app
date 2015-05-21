package controllers;

import java.util.List;

import models.Constants;
import models.MessagePublisher;
import models.MessageRepository;
import models.RepositoryException;
import play.libs.Akka;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import redis.clients.jedis.JedisPool;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;

import com.fasterxml.jackson.databind.JsonNode;
import com.typesafe.plugin.RedisPlugin;

/**
 * Controller for handling REST requests related to messages.
 * 
 * @author zstorok
 */
public class Messages extends Controller {
	
	public static Result persist() throws RepositoryException {
		JsonNode message = request().body().asJson();
		if (message == null) {
			return badRequest("Expecting application/json request body.");
		}
		// publish on Redis channel
		JedisPool pool = play.Play.application().plugin(RedisPlugin.class).jedisPool();
		MessagePublisher messagePublisher = new MessagePublisher(pool);
		messagePublisher.publish(message);
		
		// send message to connected Websocket clients via the supervisor actor
		ActorSelection supervisor = Akka.system().actorSelection(Constants.SUPERVISOR_ACTOR_PATH);
		supervisor.tell(message, ActorRef.noSender());
		
		return created();
	}
	
	public static Result findAll() throws RepositoryException {
		MessageRepository repository = new MessageRepository(play.Play.application().plugin(RedisPlugin.class).jedisPool());
		List<JsonNode> allMessages = repository.findAll();
		return ok(Json.toJson(allMessages));
	}
}
