package server;

import org.eclipse.jetty.websocket.api.Session;
import java.util.concurrent.ConcurrentHashMap;

public class GameSession {
    private final ConcurrentHashMap<String, Session> players = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Session> observers = new ConcurrentHashMap<>();

    public void addPlayer(String playerName, Session session) {
        players.put(playerName, session);
    }

    public void addObserver(String observerName, Session session) {
        observers.put(observerName, session);
    }

    public void removeParticipant(String participantName) {
        // Participant could be a player or an observer
        players.remove(participantName);
        observers.remove(participantName);
    }

    public ConcurrentHashMap<String, Session> getPlayers() {
        return players;
    }

    public ConcurrentHashMap<String, Session> getObservers() {
        return observers;
    }
}
