package handler;

import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import spark.Spark;
@WebSocket
public class WebSocketRequestHandler {

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        session.getRemote().sendString("WebSocketRequestHandler received a message: " + message);
    }

}