import models.WebSocketSender;

import org.junit.Test;
import org.mockito.Mockito;

import play.libs.Json;
import play.mvc.WebSocket;
import play.mvc.WebSocket.Out;
import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.TestActorRef;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Unit test for the {@link WebSocketSender} actor.
 * 
 * @author zstorok
 *
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class WebSocketSenderTest {

	@Test
	public void tell_jsonNode_writtenToSocket() {
		ActorSystem actorSystem = ActorSystem.create();
		Out out = Mockito.mock(WebSocket.Out.class);
		Props props = Props.create(WebSocketSender.class, out);
		TestActorRef<Actor> actorRef = TestActorRef.create(actorSystem, props);
		
		String jsonString = "{\"foo\":\"bar\"}";
		JsonNode jsonNode = Json.parse(jsonString);
		actorRef.tell(jsonNode, ActorRef.noSender());
		
		Mockito.verify(out).write(jsonNode);
	}
	
	@Test
	public void tell_object_ignored() {
		ActorSystem actorSystem = ActorSystem.create();
		Out out = Mockito.mock(WebSocket.Out.class);
		Props props = Props.create(WebSocketSender.class, out);
		
		TestActorRef<Actor> actorRef = TestActorRef.create(actorSystem, props);
		actorRef.tell(new Object(), ActorRef.noSender());
		
		Mockito.verifyZeroInteractions(out);
	}
	
}
