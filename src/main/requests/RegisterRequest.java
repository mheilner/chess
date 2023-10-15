package requests;

/**
 * Request object for registering a new user.
 */
public class RegisterRequest {
    private String username;
    private String password;
    private String email;

    /**
     * Constructor for RegisterRequest.
     * @param username Username of the new user.
     * @param password Password for the new user.
     * @param email Email address of the new user.
     */
    public RegisterRequest(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    // Getter and Setter methods for username, password, and email...
}
