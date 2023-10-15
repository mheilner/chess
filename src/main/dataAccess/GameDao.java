package dataAccess;

import model.Game;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Game operations.
 */
public class GameDao {

    private List<Game> games = new ArrayList<>();
    private int nextGameID = 1;

    /**
     * Insert a new game into the database.
     * @param game The game to insert.
     * @return The assigned gameID.
     */
    public int insert(Game game) {
        game.setGameID(nextGameID++);
        games.add(game);
        return game.getGameID();
    }

    /**
     * Find a game by its ID.
     * @param gameID The ID to search for.
     * @return The game if found; null otherwise.
     */
    public Game find(int gameID) {
        for (Game game : games) {
            if (game.getGameID() == gameID) {
                return game;
            }
        }
        return null;
    }

    /**
     * Retrieve all games.
     * @return List of all games.
     */
    public List<Game> findAll() {
        return new ArrayList<>(games);
    }

    /**
     * Clear all games.
     */
    public void clear() {
        games.clear();
        nextGameID = 1;
    }
}
