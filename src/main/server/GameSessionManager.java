package server;

import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.ServerMessage;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import server.GameSession;

public class GameSessionManager {
    private static GameSessionManager instance = null;
    private final ConcurrentHashMap<Integer, GameSession> gameSessions = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

    private GameSessionManager() {
        //Private Constructor
    }

    public static GameSessionManager getInstance() {
        if (instance == null) {
            instance = new GameSessionManager();
        }
        return instance;
    }

    public void addPlayerToGame(int gameID, String playerName, Session session) {
        gameSessions.computeIfAbsent(gameID, k -> new GameSession()).addPlayer(playerName, session);
    }

    public void addObserverToGame(int gameID, String observerName, Session session) {
        gameSessions.computeIfAbsent(gameID, k -> new GameSession()).addObserver(observerName, session);
    }

    public void removeParticipantFromGame(int gameID, String participantName) {
        GameSession gameSession = gameSessions.get(gameID);
        if (gameSession != null) {
            Session session = gameSession.getPlayers().remove(participantName);
            if (session == null) {
                session = gameSession.getObservers().remove(participantName);
            }
            if (session != null && session.isOpen()) {
                    session.close();
                }
            }
        }



    public void broadcastToGame(int gameID, Session excludeSession, ServerMessage message) {
        GameSession gameSession = gameSessions.get(gameID);
        if (gameSession != null) {
            String messageJson = gson.toJson(message);
            broadcast(messageJson, gameSession.getPlayers(), excludeSession);
            broadcast(messageJson, gameSession.getObservers(), excludeSession);
        }
    }

    private void broadcast(String message, ConcurrentHashMap<String, Session> sessions, Session excludeSession) {
        for (Map.Entry<String, Session> entry : sessions.entrySet()) {
            Session currentSession = entry.getValue();
            if (currentSession != excludeSession && currentSession.isOpen()) {
                try {
                    currentSession.getRemote().sendString(message);
                } catch (IOException e) {
                    // Handle exception, for example logging it
                    // Consider what action to take if unable to send message
                }
            }
        }
    }

}