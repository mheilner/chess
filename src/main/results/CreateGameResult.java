package results;

/**
 * Represents the result of creating a game.
 */
public class CreateGameResult {
    private Integer gameID;
    private String message;

    /**
     * Constructor for a successful game creation.
     * @param gameID The ID of the created game.
     */
    public CreateGameResult(Integer gameID) {
        this.gameID = gameID;
    }

    /**
     * Constructor for an error in game creation.
     * @param message Error message.
     */
    public CreateGameResult(String message) {
        this.message = message;
    }

    /**
     * Gets the game ID.
     * @return The game ID.
     */
    public Integer getGameID() {
        return gameID;
    }

    /**
     * Sets the game ID.
     * @param gameID The game ID to set.
     */
    public void setGameID(Integer gameID) {
        this.gameID = gameID;
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
