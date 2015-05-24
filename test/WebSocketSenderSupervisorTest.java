import models.WebSocketSenderSupervisor;

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
 * Unit test for the {@link WebSocketSenderSupervisor} actor.
 * 
 * @author zstorok
 *
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class WebSocketSenderSupervisorTest {

	@Test
	public void tell_jsonNode_ignored() {
		ActorSystem actorSystem = ActorSystem.create();
		Props props = Props.create(WebSocketSenderSupervisor.class);
		TestActorRef<Actor> actorRef = TestActorRef.create(actorSystem, props);
		Out out = Mockito.mock(WebSocket.Out.class);
		
		String jsonString = "{\"foo\":\"bar\"}";
		JsonNode jsonNode = Json.parse(jsonString);
		actorRef.tell(jsonNode, ActorRef.noSender());
		
		Mockito.verifyZeroInteractions(out);
	}
	
	@Test
	public void tell_addAndJsonNode_writeToSocket() {
		ActorSystem actorSystem = ActorSystem.create();
		Props props = Props.create(WebSocketSenderSupervisor.class);
		TestActorRef<Actor> actorRef = TestActorRef.create(actorSystem, props);
		Out out = Mockito.mock(WebSocket.Out.class);
		
		actorRef.tell(new WebSocketSenderSupervisor.Add(out), ActorRef.noSender());
		
		String jsonString = "{\"foo\":\"bar\"}";
		JsonNode jsonNode = Json.parse(jsonString);
		actorRef.tell(jsonNode, ActorRef.noSender());
		
		Mockito.verify(out).write(jsonNode);
	}
	
	@Test
	public void tell_removeFromEmpty() {
		ActorSystem actorSystem = ActorSystem.create();
		Props props = Props.create(WebSocketSenderSupervisor.class);
		TestActorRef<Actor> actorRef = TestActorRef.create(actorSystem, props);
		Out out = Mockito.mock(WebSocket.Out.class);
		
		actorRef.tell(new WebSocketSenderSupervisor.Remove(out), ActorRef.noSender());
	}
	
	@Test
	public void tell_addRemoveJsonNode_ignored() {
		ActorSystem actorSystem = ActorSystem.create();
		Props props = Props.create(WebSocketSenderSupervisor.class);
		TestActorRef<Actor> actorRef = TestActorRef.create(actorSystem, props);
		Out out = Mockito.mock(WebSocket.Out.class);
		
		actorRef.tell(new WebSocketSenderSupervisor.Add(out), ActorRef.noSender());
		actorRef.tell(new WebSocketSenderSupervisor.Remove(out), ActorRef.noSender());
		
		String jsonString = "{\"foo\":\"bar\"}";
		JsonNode jsonNode = Json.parse(jsonString);
		actorRef.tell(jsonNode, ActorRef.noSender());
		
		Mockito.verifyZeroInteractions(out);
	}
	
}
