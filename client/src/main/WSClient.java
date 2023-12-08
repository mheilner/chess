import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@ClientEndpoint
public class WSClient {

    private Session session;
    private Gameplay gameplay;

    // Existing constructor for when Gameplay is available
    public WSClient(Gameplay gameplay) throws URISyntaxException, DeploymentException, IOException {
        this.gameplay = gameplay;
        connectToServer();
    }

    // New constructor for use without Gameplay instance
    public WSClient() throws URISyntaxException, DeploymentException, IOException {
        this.gameplay = null;
        connectToServer();
    }

    private void connectToServer() throws URISyntaxException, DeploymentException, IOException {
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
        if (gameplay != null) {
            gameplay.handleWebSocketMessage(message);
        } else {
            // Handle messages when Gameplay is not available
            System.out.println("Received from server (no gameplay): " + message);
        }
    }

    public void sendMessage(String message) throws Exception {
        if (session != null) {
            session.getBasicRemote().sendText(message);
        }
    }

}
