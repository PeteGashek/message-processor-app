package models;

import java.util.HashMap;
import java.util.Map;

import play.mvc.WebSocket;
import scala.collection.JavaConversions;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Supervisor actor for keeping track of Websocket sender actors.
 * 
 * @author zstorok
 */
public class WebSocketSenderSupervisor extends UntypedActor {

	/**
	 * Message object for adding a Websocket.
	 * 
	 * @author zstorok
	 */
	public static final class Add {
		private final WebSocket.Out<JsonNode> socket;

		public Add(WebSocket.Out<JsonNode> socket) {
			this.socket = socket;
		}

		public WebSocket.Out<JsonNode> getSocket() {
			return socket;
		}
	}

	/**
	 * Message object for removing a Websocket.
	 * 
	 * @author zstorok
	 */
	public static final class Remove {
		private final WebSocket.Out<JsonNode> socket;

		public Remove(WebSocket.Out<JsonNode> socket) {
			this.socket = socket;
		}

		public WebSocket.Out<JsonNode> getSocket() {
			return socket;
		}
	}

	public static Props props() {
		return Props.create(WebSocketSenderSupervisor.class);
	}

	private final Map<WebSocket.Out<JsonNode>, ActorRef> socketMap = new HashMap<>();

	public void onReceive(Object message) throws Exception {
		if (message instanceof Add) {
			Add add = (Add) message;
			WebSocket.Out<JsonNode> socket = add.getSocket();
			ActorRef actorRef = context().actorOf(WebSocketSender.props(socket));
			socketMap.put(socket, actorRef);
		} else if (message instanceof Remove) {
			Remove remove = (Remove) message;
			WebSocket.Out<JsonNode> socket = remove.getSocket();
			if (socketMap.containsKey(socket)) {
				ActorRef actorRef = socketMap.get(socket);
				context().stop(actorRef);
			}
		} else if (message instanceof JsonNode) {
			JsonNode json = (JsonNode) message;
			JavaConversions.asJavaIterable(context().children()).forEach(ref -> ref.tell(json, self()));
		}
	}
}
