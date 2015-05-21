package models;

import play.mvc.WebSocket;
import akka.actor.Props;
import akka.actor.UntypedActor;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Actor for writing JSON messages to a Websocket.
 *  
 * @author zstorok
 */
public class WebSocketSender extends UntypedActor {
	public static Props props(WebSocket.Out<JsonNode> socket) {
        return Props.create(WebSocketSender.class, () -> new WebSocketSender(socket));
    }

    private final WebSocket.Out<JsonNode> socket;

    public WebSocketSender(WebSocket.Out<JsonNode> socket) {
        this.socket = socket;
    }
    
    public void onReceive(Object message) throws Exception {
        if (message instanceof JsonNode) {
        	socket.write((JsonNode)message);
        }
    }
}
