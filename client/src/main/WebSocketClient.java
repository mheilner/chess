import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@ClientEndpoint
public class WebSocketClient {

    private Session session;

    public WebSocketClient() throws URISyntaxException, DeploymentException, IOException {
        URI uri = new URI("ws://localhost:8080/connect");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;
    }

    @OnMessage
    public void onMessage(String message) {
        // Handle incoming messages from server
        System.out.println("Received from server: " + message);
    }

    public void sendMessage(String message) throws Exception {
        if (session != null) {
            session.getBasicRemote().sendText(message);
        }
    }


}
