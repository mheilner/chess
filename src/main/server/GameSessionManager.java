package server;

import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.ServerMessage;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import server.GameSession;

public class GameSessionManager {
    private final ConcurrentHashMap<Integer, GameSession> gameSessions = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

    public void addPlayerToGame(int gameID, String playerName, Session session) {
        gameSessions.computeIfAbsent(gameID, k -> new GameSession()).addPlayer(playerName, session);
    }

    public void addObserverToGame(int gameID, String observerName, Session session) {
        gameSessions.computeIfAbsent(gameID, k -> new GameSession()).addObserver(observerName, session);
    }

    public void removeParticipantFromGame(int gameID, String participantName) {
        GameSession gameSession = gameSessions.get(gameID);
        if (gameSession != null) {
            gameSession.removeParticipant(participantName);
        }
    }

    public void broadcastToGame(int gameID, String excludeParticipantName, ServerMessage message) throws IOException {
        GameSession gameSession = gameSessions.get(gameID);
        if (gameSession != null) {
            String messageJson = gson.toJson(message);
            broadcast(messageJson, gameSession.getPlayers(), excludeParticipantName);
            broadcast(messageJson, gameSession.getObservers(), excludeParticipantName);
        }
    }

    private void broadcast(String message, ConcurrentHashMap<String, Session> sessions, String excludeParticipant) throws IOException {
        for (Map.Entry<String, Session> entry : sessions.entrySet()) {
            if (!entry.getKey().equals(excludeParticipant) && entry.getValue().isOpen()) {
                entry.getValue().getRemote().sendString(message);
            }
        }
    }

}