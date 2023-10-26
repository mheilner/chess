package model;

/**
 * Represents an authorization token in the Chess server application.
 */
public class AuthToken {
    private String authToken;
    private String username;
    public AuthToken(String authToken, String username) {
        this.authToken = authToken;
        this.username = username;
    }
    public String getUsername() {
        return username;
    }
    public String getAuthToken() {
        return authToken;
    }
}
