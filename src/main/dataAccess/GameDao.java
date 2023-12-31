package dataAccess;
import java.lang.reflect.Type;
import java.sql.*;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import chessPkg.*;
import com.google.gson.*;
import handler.WSHandler;
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

    private GameDao() throws DataAccessException {
        initializeNextGameID();
    } // Private constructor to prevent direct instantiation

    // Public method to get the Singleton instance
    public static GameDao getInstance() throws DataAccessException {
        if (instance == null) {
            instance = new GameDao();
        }
        return instance;
    }

    // Method to initialize nextGameID
    private void initializeNextGameID() throws DataAccessException {
        Connection conn = db.getConnection();
        String sql = "SELECT MAX(game_id) AS max_id FROM games;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    nextGameID = rs.getInt("max_id") + 1;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while initializing nextGameID: " + e.getMessage());
        } finally {
            db.returnConnection(conn);
        }
    }

    /**
     * Insert a new game into the database.
     * @param game The game to insert.
     * @return The assigned gameID.
     */
    public int insert(Game game) throws DataAccessException {
        Connection conn = db.getConnection();
        String gameStateJSON = serializeCGame(game.getGame()); // Serialize the CGame object
        String sql = "INSERT INTO games (game_id, game_name, white_username, black_username, game_state) VALUES (?, ?, ?, ?, ?);";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            game.setGameID(nextGameID++);
            if(game.getGameName()==null){
                game.setGameName("game"+ game.getGameID());
            }
            stmt.setInt(1, game.getGameID());
            stmt.setString(2, game.getGameName());
            stmt.setString(3, game.getWhiteUsername());
            stmt.setString(4, game.getBlackUsername());
            stmt.setString(5, gameStateJSON); // Use the serialized game state
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting into the database: " + e.getMessage());
        } finally {
            db.returnConnection(conn);
        }
        return game.getGameID(); // Return the new game ID
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
        try (Connection conn = db.getConnection()){
            String sql = "DELETE FROM games;";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.executeUpdate();
                nextGameID = 1;
            }
        }
         catch (SQLException e) {
             System.out.println(e);
            throw new DataAccessException("Error encountered while clearing games");
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
     * Update the game state of a specific game.
     * @param gameID The ID of the game to update.
     * @param cGame The new state of the game.
     * @throws DataAccessException If there is an issue accessing the data.
     */
    public void updateGameState(int gameID, CGame cGame) throws DataAccessException {
        Connection conn = db.getConnection();
        String sql = "UPDATE games SET game_state = ? WHERE game_id = ?;";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            String gameStateJSON = serializeCGame(cGame); // Serialize the CGame object
            stmt.setString(1, gameStateJSON);
            stmt.setInt(2, gameID);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DataAccessException("Update failed: no rows affected.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while updating game state: " + e.getMessage());
        } finally {
            db.returnConnection(conn);
        }
    }

    /**
     * Update the player usernames for a specific game.
     * @param gameID The ID of the game to update.
     * @param whiteUsername The username of the white player (can be null if leaving or not assigned).
     * @param blackUsername The username of the black player (can be null if leaving or not assigned).
     * @throws DataAccessException If there is an issue accessing the data.
     */
    public void updateGamePlayers(int gameID, String whiteUsername, String blackUsername) throws DataAccessException {
        Connection conn = db.getConnection();
        String sql = "UPDATE games SET white_username = ?, black_username = ? WHERE game_id = ?;";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Set the parameters for the prepared statement
            stmt.setString(1, whiteUsername);
            stmt.setString(2, blackUsername);
            stmt.setInt(3, gameID);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DataAccessException("Update failed: no rows affected.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while updating game players: " + e.getMessage());
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


    /**
     * Serialize CGame object to JSON.
     * @param cGame The CGame object to serialize.
     * @return JSON representation of CGame.
     */
    private String serializeCGame(CGame cGame) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ChessPiece.class, new ChessPieceSerializer());
        Gson gson = builder.create();
        String json = gson.toJson(cGame);

        return json;
    }

    /**
     * Deserialize JSON to CGame object.
     * @param json The JSON string.
     * @return Deserialized CGame object.
     */
    private CGame deserializeCGame(String json) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ChessPiece.class, new ChessPieceDeserializer());
        Gson gson = builder.create();

        // Logging the JSON string being deserialized
        return gson.fromJson(json, CGame.class);
    }

    public static class ChessGameDeserializer implements JsonDeserializer<ChessGame>{
        @Override
        public ChessGame deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return jsonDeserializationContext.deserialize(jsonElement, CGame.class);
        }
    }


    public static class ChessPieceSerializer implements JsonSerializer<ChessPiece> {
        @Override
        public JsonElement serialize(ChessPiece src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            result.addProperty("type", src.getClass().getSimpleName());
            result.addProperty("teamColor", src.getTeamColor().toString());
            // Add other common properties if needed
            return result;
        }
    }

    // Custom deserializer for ChessPiece objects
    public static class ChessPieceDeserializer implements JsonDeserializer<ChessPiece> {
        @Override
        public ChessPiece deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();

            JsonElement typeElement = jsonObject.get("type");
            if (typeElement == null || typeElement.isJsonNull()) {
                throw new JsonParseException("Chess piece type is missing or null");
            }
            String type = typeElement.getAsString();

            JsonElement teamColorElement = jsonObject.get("teamColor");
            if (teamColorElement == null || teamColorElement.isJsonNull()) {
                throw new JsonParseException("Team color is missing or null");
            }
            ChessGame.TeamColor teamColor = ChessGame.TeamColor.valueOf(teamColorElement.getAsString());

            switch (type) {
                case "Rook":
                    return new Rook(teamColor);
                case "Knight":
                    return new Knight(teamColor);
                case "Bishop":
                    return new Bishop(teamColor);
                case "Queen":
                    return new Queen(teamColor);
                case "King":
                    return new King(teamColor);
                case "Pawn":
                    return new Pawn(teamColor);
                default:
                    throw new JsonParseException("Unknown chess piece type: " + type);
            }
        }
    }

    public class GsonUtil {
        public static Gson createGson() {
            return new GsonBuilder()
                    .registerTypeAdapter(ChessPiece.class, new GameDao.ChessPieceDeserializer())
                    .registerTypeAdapter(ChessPiece.class, new GameDao.ChessPieceSerializer())
                    .registerTypeAdapter(ChessPosition.class, new WSHandler.CPositionDeserializer()) // Use CPositionDeserializer for ChessPosition
                    .registerTypeAdapter(CPosition.class, new WSHandler.CPositionSerializer())
                    .registerTypeAdapter(ChessGame.class, new GameDao.ChessGameDeserializer())
                    .create();
        }
    }

}
