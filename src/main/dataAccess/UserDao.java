package dataAccess;

import model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
/**
 * Data Access Object for User operations.
 */
public class UserDao {
    private static UserDao instance; // Singleton instance
    private List<User> users = new ArrayList<>();

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
        Database db = new Database();
        try (Connection conn = db.getConnection()) {
            String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?);";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, user.getUsername());
                stmt.setString(2, user.getPassword());
                stmt.setString(3, user.getEmail());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            if (e instanceof SQLIntegrityConstraintViolationException) {
                // Handle the integrity constraint violation
            } else {
                // Handle other SQL exceptions
            }
            // You can also check the SQL state or error code if needed
            String sqlState = e.getSQLState();
            if ("23000".equals(sqlState)) {
                // Handle integrity constraint violation
            }
            // Rethrow or handle the exception as necessary
            throw new DataAccessException("Database access error");
        }
    }

    /**
     * Find a user by their username.
     * @param username The username to search for.
     * @return The user if found; null otherwise.
     */
    public User find(String username) throws DataAccessException {
        Database db = new Database();
        User user = null;
        try (Connection conn = db.getConnection()) {
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
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error encountered while finding the user", e);
        }
        return user;
    }

    /**
     * Clear all users.
     */
    public void clear() {users.clear();}
}
