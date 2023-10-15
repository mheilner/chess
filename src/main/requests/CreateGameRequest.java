package requests;

/**
 * Request object for creating a new game.
 */
public class CreateGameRequest {
    private String gameName;

    /**
     * Constructor for CreateGameRequest.
     * @param gameName Name of the new game to be created.
     */
    public CreateGameRequest(String gameName) {
        this.gameName = gameName;
    }

    // Getter and Setter methods for gameName...
}
