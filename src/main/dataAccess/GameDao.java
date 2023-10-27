package dataAccess;
import java.util.Objects;
import java.util.logging.Logger;

import model.Game;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Game operations.
 */
public class GameDao {
    private static final Logger LOGGER = Logger.getLogger(GameDao.class.getName());

    private static GameDao instance; // Singleton instance
    private List<Game> games = new ArrayList<>();
    private int nextGameID = 1;

    private GameDao() {} // Private constructor to prevent direct instantiation

    // Public method to get the Singleton instance
    public static GameDao getInstance() {
        if (instance == null) {
            instance = new GameDao();
        }
        return instance;
    }
    /**
     * Insert a new game into the database.
     * @param game The game to insert.
     * @return The assigned gameID.
     */
    public int insert(Game game) {
        game.setGameID(nextGameID++);
        games.add(game);
        LOGGER.info("Game inserted with ID: " + game.getGameID());
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
        LOGGER.info("Fetching all games. Current count: " + games.size());
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
        LOGGER.info("Game with ID: " + updatedGame.getGameID() + " updated.");
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
        LOGGER.info("Game with ID: " + gameID + " removed.");
    }
    /**
     * Claim a spot in a game.
     * @param gameID The game ID.
     * @param username The player's username.
     * @param color "WHITE", "BLACK", or null.
     * @throws DataAccessException if the game doesn't exist or the spot is taken.
     */
    public void claimSpot(int gameID, String username, String color) throws DataAccessException {
        LOGGER.info("Attempting to claim a spot in game with ID: " + gameID + " for user: " + username + " with color: " + color);

        Game game = find(gameID);
        if (game == null) {
            throw new DataAccessException("Game with ID " + gameID + " not found.");
        }

        if (color == null) {
            // User joins as an observer, so we don't need to do anything further
            return;
        }

        if ("WHITE".equalsIgnoreCase(color)) {
            if (game.getWhiteUsername() != null) {
                throw new DataAccessException("White spot is already taken.");
            }
            game.setWhiteUsername(username);
            LOGGER.info("White spot claimed in game with ID: " + gameID);
        } else if ("BLACK".equalsIgnoreCase(color)) {
            if (game.getBlackUsername() != null) {
                throw new DataAccessException("Black spot is already taken.");
            }
            game.setBlackUsername(username);
            LOGGER.info("Black spot claimed in game with ID: " + gameID);

        } else {
            throw new DataAccessException("Invalid color specified.");
        }
    }

    /**
     * Check if a game with the specified name already exists.
     * @param gameName The name of the game.
     * @return true if the game exists, false otherwise.
     */
    public boolean gameNameExists(String gameName) {
        for (Game game : games) {
            if (game.getGameName().equals(gameName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the username of the player who claimed the white spot in a game.
     * @param gameID The ID of the game.
     * @return The username of the white player if found; null otherwise.
     * @throws DataAccessException if the game doesn't exist.
     */
    public String getWhitePlayer(int gameID) throws DataAccessException {
        Game game = find(gameID);
        if (game == null) {
            throw new DataAccessException("Game with ID " + gameID + " not found.");
        }
        return game.getWhiteUsername();
    }

    /**
     * Get the username of the player who claimed the black spot in a game.
     * @param gameID The ID of the game.
     * @return The username of the black player if found; null otherwise.
     * @throws DataAccessException if the game doesn't exist.
     */
    public String getBlackPlayer(int gameID) throws DataAccessException {
        Game game = find(gameID);
        if (game == null) {
            throw new DataAccessException("Game with ID " + gameID + " not found.");
        }
        return game.getBlackUsername();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameDao gameDao = (GameDao) o;
        return nextGameID == gameDao.nextGameID && Objects.equals(games, gameDao.games);
    }

    @Override
    public int hashCode() {
        return Objects.hash(games);
    }

}
