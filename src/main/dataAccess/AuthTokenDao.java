package dataAccess;

import model.AuthToken;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static server.Server.db;

/**
 * Data Access Object for AuthToken operations.
 */
public class AuthTokenDao {
    private static AuthTokenDao instance; // Singleton instance

    private AuthTokenDao() {} // Private constructor to prevent direct instantiation

    // Public method to get the Singleton instance
    public static AuthTokenDao getInstance() {
        if (instance == null) {
            instance = new AuthTokenDao();
        }
        return instance;
    }
    /**
     * Insert a new authToken into the database.
     * @param authToken The authToken to insert.
     */
    public void insert(AuthToken authToken) throws DataAccessException {
        Database db = new Database();
        Connection conn = db.getConnection();
        String sql = "INSERT INTO auth_tokens (auth_token, username) VALUES (?, ?);";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken.getAuthToken());
            stmt.setString(2, authToken.getUsername());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while inserting the auth token");
        }finally {
            db.returnConnection(conn);
        }
    }
    /**
     * Find a username by their authToken.
     * @param token The authToken to search for.
     * @return The username associated with the authToken if found; null otherwise.
     */
    public String findUserByToken(String token) throws DataAccessException {
        Database db = new Database();
        Connection conn = db.getConnection();
        String sql = "SELECT username FROM auth_tokens WHERE auth_token = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("username");
                }
            }
        }catch (SQLException e) {
            throw new DataAccessException("Error encountered while finding user by token");
        }finally {
            db.returnConnection(conn);
        }
        return null;
    }

    /**
     * Check if an authToken exists.
     * @param token The authToken to check.
     * @return true if the token exists, false otherwise.
     */
    public boolean tokenExists(String token) throws DataAccessException {
        Database db = new Database();
        Connection conn = db.getConnection();
        String sql = "SELECT COUNT(*) FROM auth_tokens WHERE auth_token = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        catch (SQLException e) {
            throw new DataAccessException("Error encountered while checking if token exists");
        }finally {
            db.returnConnection(conn);
        }return false;
    }
    /**
     * Remove a specific authToken.
     * @param token The authToken to remove.
     */
    public void removeToken(String token) throws DataAccessException {
        Connection conn = db.getConnection();
        String sql = "DELETE FROM auth_tokens WHERE auth_token = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            stmt.executeUpdate();
        }
        catch (SQLException e) {
            throw new DataAccessException("Error encountered while removing the auth token");
        }finally {
            db.returnConnection(conn);
        }
    }
    /**
     * Clear all authTokens.
     */
    public void clear() throws DataAccessException {
        Database db = new Database();
        Connection conn = db.getConnection();
        String sql = "DELETE FROM auth_tokens;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        }catch (SQLException e) {
            throw new DataAccessException("Error encountered while clearing auth tokens");
        }finally {
            db.returnConnection(conn);
    }}
}
