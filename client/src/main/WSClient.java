import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@ClientEndpoint
public class WSClient {

    private Session session;
    private Gameplay gameplay;

    public WSClient(Gameplay gameplay) throws URISyntaxException, DeploymentException, IOException {
        this.gameplay = gameplay;
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
        gameplay.handleWebSocketMessage(message);
        System.out.println("Received from server: " + message);
    }

    public void sendMessage(String message) throws Exception {
        if (session != null) {
            session.getBasicRemote().sendText(message);
        }
    }


}
