# Message Processor Application

This is a simple Play Framework application that receives JSON messages, puts 
them on a Redis pub/sub channel, persists them to Redis and sends out the 
received messages to connected clients over Websockets.

## Usage

By default, the application is configured to listen on port 9000.

Messages can be sent by `POST`ing a JSON request with the appropriate 
`application/json` content type header to `http://example.com:9000/messages`.

The messages currently saved in the system can be retrieved as a JSON array by 
sending a `GET` request to `http://example.com:9000/messages`.

The web UI for monitoring incoming messages can be accessed by visiting 
`http://example.com:9000` using a modern browser that supports Websockets. The 
page is initially empty, but it will show JSON messages as they start coming in.

## Implementation

### Controllers

Consistent with the framework's conventions, the controllers are implemented in 
the `app/controllers` package.

#### Web UI

The `Application` controller serves the simple HTML page for monitoring the 
incoming messages, and also provides the Websocket connection required for 
real-time communication from the server to the client browser.

#### REST service

The REST service endpoints are implemented in the `Messages` controller.

`POST /messages` expects an `application/json` message in the request body. 
Returns `201 Created` if the request was successfully processed, `400 Bad 
Request` if the request was not valid JSON.

`GET /messages` returns the messages currently persisted in the system as a 
JSON array. 

### Application logic

The main application logic is implemented in the `app/models` package.

#### Actors

There are three actors in the application:

* `WebSocketSender` receives `JsonNode` messages and writes them to the 
underlying Websocket connection.
* `WebSocketSenderSupervisor` manages the connected Websocket clients by 
handling `Add` and `Remove` messages and delegating `JsonNode` messages to all 
currently active `WebSocketSender` actors.
* `RedisPublisher` publishes `JsonNode` messages on its underlying Redis 
pub/sub channel.

#### Message repository

Messages are persisted and retrieved using the `MessageRepository` class. The 
keys of newly persisted messages are saved to a set in Redis, which in turn is 
used for retrieving all persisted messages. This method is preferred to simply 
using the KEYS command in application code.

#### Redis pub/sub listener

The `MessagePubSub` listener is registered in the `Global` class at application 
startup and is removed when the application is shut down.
