package requests;

/**
 * Request object for logging in an existing user.
 */
public class LoginRequest {
    private String username;
    private String password;

    /**
     * Constructor for LoginRequest.
     * @param username Username of the user.
     * @param password Password of the user.
     */
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getter and Setter methods for username and password...
}
