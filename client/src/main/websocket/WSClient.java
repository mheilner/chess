package websocket;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import chessPkg.CPosition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataAccess.DataAccessException;
import dataAccess.GameDao;
import handler.WSHandler;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import serverFacade.Gameplay;

@ClientEndpoint
public class WSClient {

    private Session session;
    private Gameplay gameplay;
    private final Gson gson;
    // Existing constructor for when serverFacade.Gameplay is available
    public WSClient(Gameplay gameplay) throws URISyntaxException, DeploymentException, IOException {
        this.gameplay = gameplay;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(ChessPiece.class, new GameDao.ChessPieceDeserializer())
                .registerTypeAdapter(ChessPiece.class, new GameDao.ChessPieceSerializer())
                .registerTypeAdapter(ChessPosition.class, new WSHandler.CPositionDeserializer()) // Use CPositionDeserializer for ChessPosition
                .registerTypeAdapter(CPosition.class, new WSHandler.CPositionSerializer())
                .registerTypeAdapter(ChessGame.class, new GameDao.ChessGameDeserializer())
                .create();

        connectToServer();
    }

    // New constructor for use without serverFacade.Gameplay instance
    public WSClient(Gson gson) throws URISyntaxException, DeploymentException, IOException {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(ChessPiece.class, new GameDao.ChessPieceDeserializer())
                .registerTypeAdapter(ChessPosition.class, new WSHandler.CPositionDeserializer()) // Use CPositionDeserializer for ChessPosition
                .registerTypeAdapter(CPosition.class, new WSHandler.CPositionSerializer())
                .registerTypeAdapter(ChessGame.class, new GameDao.ChessGameDeserializer())
                .create();
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
    public void onMessage(String message) throws DataAccessException {
        if (gameplay != null) {
            gameplay.handleWebSocketMessage(message, gson);
        } else {
            System.out.println("Received from server (no gameplay): " + message);
        }
    }

    public void sendMessage(String message) throws Exception {
        if (session != null) {
            session.getBasicRemote().sendText(message);
        }
    }

}
