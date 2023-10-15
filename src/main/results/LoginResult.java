package results;

/**
 * Result object for the login response.
 */
public class LoginResult {
    private String message;
    private String authToken;
    private String username;

    /**
     * Constructor for successful LoginResult.
     * @param authToken The authentication token.
     * @param username The username of the user.
     */
    public LoginResult(String authToken, String username) {
        this.authToken = authToken;
        this.username = username;
    }

    /**
     * Constructor for error LoginResult.
     * @param message Error message.
     */
    public LoginResult(String message) {
        this.message = message;
    }

    // Getter and Setter methods for message, authToken, and username...
}
