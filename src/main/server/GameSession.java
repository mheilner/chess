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

    public ConcurrentHashMap<String, Session> getPlayers() {
        return players;
    }

    public ConcurrentHashMap<String, Session> getObservers() {
        return observers;
    }
}
