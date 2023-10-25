package results;

/**
 * Represents the result of joining a game.
 */
public class JoinGameResult {
    private String message;

    /**
     * Default constructor.
     */
    public JoinGameResult() {}

    /**
     * Constructor that initializes the message field.
     * @param message Error or success message.
     */
    public JoinGameResult(String message) {
        this.message = message;
    }

    /**
     * Gets the message.
     * @return The message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the message.
     * @param message The message to set.
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
