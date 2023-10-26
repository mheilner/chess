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
    /**
     * Update an existing game's details.
     * @param updatedGame The game with updated details.
     * @throws DataAccessException if the game doesn't exist.
     */
    public void updateGame(Game updatedGame) throws DataAccessException {
        int index = -1;
        for (int i = 0; i < games.size(); i++) {
            if (games.get(i).getGameID() == updatedGame.getGameID()) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            throw new DataAccessException("Game with ID " + updatedGame.getGameID() + " not found.");
        }
        games.set(index, updatedGame);
    }
    /**
     * Remove a game by its ID.
     * @param gameID The gameID to remove.
     * @throws DataAccessException if the game doesn't exist.
     */
    public void remove(int gameID) throws DataAccessException {
        Game game = find(gameID);
        if (game == null) {
            throw new DataAccessException("Game with ID " + gameID + " not found.");
        }
        games.remove(game);
    }
    /**
     * Claim a spot in a game.
     * @param gameID The game ID.
     * @param username The player's username.
     * @param color "WHITE" or "BLACK".
     * @throws DataAccessException if the game doesn't exist or the spot is taken.
     */
    public void claimSpot(int gameID, String username, String color) throws DataAccessException {
        Game game = find(gameID);
        if (game == null) {
            throw new DataAccessException("Game with ID " + gameID + " not found.");
        }

        if ("WHITE".equalsIgnoreCase(color)) {
            if (game.getWhiteUsername() != null) {
                throw new DataAccessException("White spot is already taken.");
            }
            game.setWhiteUsername(username);
        } else if ("BLACK".equalsIgnoreCase(color)) {
            if (game.getBlackUsername() != null) {
                throw new DataAccessException("Black spot is already taken.");
            }
            game.setBlackUsername(username);
        } else {
            throw new DataAccessException("Invalid color specified.");
        }
    }
}
