package dataAccess;

import model.User;
import java.util.ArrayList;
import java.util.List;
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
        for (User u : users) {
            if (u.getUsername().equals(user.getUsername())) {
                throw new DataAccessException("User already exists");
            }
        }
        users.add(user);
    }
    /**
     * Find a user by their username.
     * @param username The username to search for.
     * @return The user if found; null otherwise.
     */
    public User find(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }
    /**
     * Clear all users.
     */
    public void clear() {users.clear();}
}
