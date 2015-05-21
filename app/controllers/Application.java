package controllers;

import models.Constants;
import models.WebSocketSenderSupervisor;
import play.libs.Akka;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Main controller.
 * 
 * @author zstorok
 */
public class Application extends Controller {

    public static Result index() {
        return ok(views.html.index.render());
    }
    
    public static WebSocket<JsonNode> socket() {
    	return WebSocket.whenReady((in, out) -> {
    		// add sender to supervisor actor
    		ActorSelection supervisor = Akka.system().actorSelection(Constants.SUPERVISOR_ACTOR_PATH);
    		supervisor.tell(new WebSocketSenderSupervisor.Add(out), ActorRef.noSender());
    		// remove sender when socket is closed
    		in.onClose(() -> supervisor.tell(new WebSocketSenderSupervisor.Remove(out), ActorRef.noSender()));
    	});
    }
}
