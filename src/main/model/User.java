package model;

/**
 * Represents a User in the Chess server application.
 */
public class User {
    private String username;
    private String password;
    private String email;

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    // Getters and setters...

    public String getUsername() {
        return username;
    }
}
