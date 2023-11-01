package dataAccess;
import java.sql.*;
import java.util.Objects;
import java.util.logging.Logger;

import chess.ChessGame;
import chessPkg.CGame;
import com.google.gson.Gson;
import model.Game;

import java.util.ArrayList;
import java.util.List;

import static server.Server.db;

/**
 * Data Access Object for Game operations.
 */
public class GameDao {

    private static GameDao instance; // Singleton instance
    private int nextGameID = 1;
    private final Gson gson = new Gson();

    private GameDao() {} // Private constructor to prevent direct instantiation

    // Public method to get the Singleton instance
    public static GameDao getInstance() {
        if (instance == null) {
            instance = new GameDao();
        }
        return instance;
    }
    /**
     * Find a game by its ID.
     * @param gameID The ID to search for.
     * @return The game if found; null otherwise.
     */
    public Game find(int gameID) throws DataAccessException {
        Connection conn = db.getConnection();
        String sql = "SELECT * FROM games WHERE game_id = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Game game = new Game(
                            rs.getInt("game_id"),
                            rs.getString("game_name"),
                            rs.getString("white_username"),
                            rs.getString("black_username"),
                            deserializeCGame(rs.getString("game_state"))
                    );
                    return game;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while finding the game");
        } finally {
            db.returnConnection(conn);
        }
        return null;
    }

    /**
     * Retrieve all games.
     * @return List of all games.
     */
    public List<Game> findAll() throws DataAccessException {
        List<Game> games = new ArrayList<>();
        Connection conn = db.getConnection();
        String sql = "SELECT * FROM games;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    games.add(new Game(
                            rs.getInt("game_id"),
                            rs.getString("game_name"),
                            rs.getString("white_username"),
                            rs.getString("black_username"),
                            deserializeCGame(rs.getString("game_state"))
                    ));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while finding all games");
        } finally {
            db.returnConnection(conn);
        }
        return games;
    }
    /**
     * Clear all games.
     */
    public void clear() throws DataAccessException {
        Connection conn = db.getConnection();
        String sql = "DELETE FROM games;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while clearing games");
        } finally {
            db.returnConnection(conn);
        }
    }


    /**
     * Claim a spot in a game.
     * @param gameID The game ID.
     * @param username The player's username.
     * @param color "WHITE", "BLACK", or null.
     * @throws DataAccessException if the game doesn't exist or the spot is taken.
     */
    public void claimSpot(int gameID, String username, String color) throws DataAccessException {
        Connection conn = db.getConnection();
        String columnToUpdate = color.equalsIgnoreCase("WHITE") ? "white_username" : "black_username";
        String sqlCheckGame = "SELECT game_id, white_username, black_username FROM games WHERE game_id = ?;";

        try (PreparedStatement stmtCheck = conn.prepareStatement(sqlCheckGame)) {
            stmtCheck.setInt(1, gameID);
            try (ResultSet rs = stmtCheck.executeQuery()) {
                if (rs.next()) {
                    String whiteUser = rs.getString("white_username");
                    String blackUser = rs.getString("black_username");

                    if ("WHITE".equalsIgnoreCase(color) && whiteUser != null) {
                        throw new DataAccessException("White spot is already taken.");
                    } else if ("BLACK".equalsIgnoreCase(color) && blackUser != null) {
                        throw new DataAccessException("Black spot is already taken.");
                    }
                    // If the spot is not taken, proceed to claim it
                    String sqlUpdateGame = "UPDATE games SET " + columnToUpdate + " = ? WHERE game_id = ?;";
                    try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdateGame)) {
                        stmtUpdate.setString(1, username);
                        stmtUpdate.setInt(2, gameID);
                        int rowsAffected = stmtUpdate.executeUpdate();

                        if (rowsAffected == 0) {
                            throw new DataAccessException("Failed to claim the spot, please try again.");
                        }
                    }
                } else {
                    throw new DataAccessException("Game with ID " + gameID + " not found.");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while claiming a spot: " + e.getMessage());
        } finally {
            db.returnConnection(conn);
        }
    }


    /**
     * Check if a game with the specified name already exists.
     * @param gameName The name of the game.
     * @return true if the game exists, false otherwise.
     */
    public boolean gameNameExists(String gameName) throws DataAccessException {
        Connection conn = db.getConnection();
        String sql = "SELECT game_id FROM games WHERE game_name = ? LIMIT 1;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, gameName);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while checking if game name exists");
        } finally {
            db.returnConnection(conn);
        }
    }


    public String getPlayerColor(int gameID, String colorColumn) throws DataAccessException {
        Connection conn = db.getConnection();
        String sql = "SELECT " + colorColumn + " FROM games WHERE game_id = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(colorColumn);
                } else {
                    throw new DataAccessException("Game with ID " + gameID + " not found.");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while getting player color");
        } finally {
            db.returnConnection(conn);
        }
    }
    /**
     * Get the username of the player who claimed the white spot in a game.
     * @param gameID The ID of the game.
     * @return The username of the white player if found; null otherwise.
     * @throws DataAccessException if the game doesn't exist.
     */
    public String getWhitePlayer(int gameID) throws DataAccessException {
        return getPlayerColor(gameID, "white_username");
    }
    /**
     * Get the username of the player who claimed the black spot in a game.
     * @param gameID The ID of the game.
     * @return The username of the black player if found; null otherwise.
     * @throws DataAccessException if the game doesn't exist.
     */
    public String getBlackPlayer(int gameID) throws DataAccessException {
        return getPlayerColor(gameID, "black_username");
    }


    private String serializeCGame(CGame cGame) {
        return gson.toJson(cGame);
    }

    private CGame deserializeCGame(String json) {
        return gson.fromJson(json, CGame.class);
    }

}
