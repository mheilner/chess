package dataAccess;

import model.AuthToken;
import java.util.ArrayList;
import java.util.List;
/**
 * Data Access Object for AuthToken operations.
 */
public class AuthTokenDao {
    private List<AuthToken> authTokens = new ArrayList<>();
    /**
     * Insert a new authToken into the database.
     * @param authToken The authToken to insert.
     */
    public void insert(AuthToken authToken) {authTokens.add(authToken);}
    /**
     * Find a user by their authToken.
     * @param token The authToken to search for.
     * @return The username associated with the authToken if found; null otherwise.
     */
    public String findUserByToken(String token) {
        for (AuthToken authToken : authTokens) {
            if (authToken.getAuthToken().equals(token)) {
                return authToken.getUsername();
            }
        }
        return null;
    }

    /**
     * Check if an authToken exists.
     * @param token The authToken to check.
     * @return true if the token exists, false otherwise.
     */
    public boolean tokenExists(String token) {
        for (AuthToken authToken : authTokens) {
            if (authToken.getAuthToken().equals(token)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Remove a specific authToken.
     * @param token The authToken to remove.
     */
    public void removeToken(String token) {authTokens.removeIf(authToken -> authToken.getAuthToken().equals(token));}
    /**
     * Clear all authTokens.
     */
    public void clear() {
        authTokens.clear();
    }
}
