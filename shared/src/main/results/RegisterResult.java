package results;

/**
 * Result object for the registration response.
 */
public class RegisterResult {
    private String message;
    private String authToken;
    private String username;

    /**
     * Constructor for successful RegisterResult.
     * @param authToken The authentication token.
     * @param username The username of the new user.
     */
    public RegisterResult(String authToken, String username) {
        this.authToken = authToken;
        this.username = username;
    }

    /**
     * Constructor for error RegisterResult.
     * @param message Error message.
     */
    public RegisterResult(String message) {
        this.message = message;
    }

    // Getter and Setter methods for message, authToken, and username...

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
