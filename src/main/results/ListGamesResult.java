package results;

import model.Game;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the result of listing all games.
 */
public class ListGamesResult {
    private List<Game> games;
    private String message;

    public ListGamesResult(List<Game> games, String message) {
        this.games = (games != null) ? games : new ArrayList<Game>();
        this.message = message;

    }

    public List<Game> getGames() {
        return games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}