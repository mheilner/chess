package dataAccess;

import model.User;

import java.sql.*;
import java.sql.SQLException;

import static server.Server.db;

/**
 * Data Access Object for User operations.
 */
public class UserDao {
    private static UserDao instance; // Singleton instance

    private UserDao() {} // Private constructor to prevent direct instantiation

    // Public method to get the Singleton instance
    public static UserDao getInstance() {
        if (instance == null) {
            instance = new UserDao();
        }
        return instance;
    }
    /**
     * Insert a new user into the database.
     * @param user The user to insert.
     */
    public void insert(User user) throws DataAccessException {
        Connection conn = db.getConnection();
        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?);";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
            throw new DataAccessException("Database access error");
        }finally {
            db.returnConnection(conn);
        }
    }

    /**
     * Find a user by their username.
     * @param username The username to search for.
     * @return The user if found; null otherwise.
     */
    public User find(String username) throws DataAccessException {
        User user = null;
        Connection conn = db.getConnection();
        String sql = "SELECT * FROM users WHERE username = ?;";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = new User(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println(e);
            throw new DataAccessException("Error encountered while finding the user");
        }finally {
            db.returnConnection(conn);
        }
        return user;
    }

    /**
     * Clear all users.
     */
    public void clear() throws DataAccessException {
        try (Connection conn = db.getConnection()) {
            String sql = "DELETE FROM users;";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.executeUpdate();
            }
        }catch (SQLException e) {
                System.out.println(e);
                throw new DataAccessException("Error encountered while clearing users");
            }
    }

}
