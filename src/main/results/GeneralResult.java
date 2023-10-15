package results;

/**
 * General result object for APIs with simple success/failure response.
 */
public class GeneralResult {
    private String message;

    /**
     * Constructor for successful GeneralResult.
     */
    public GeneralResult() { }

    /**
     * Constructor for error GeneralResult.
     * @param message Error message.
     */
    public GeneralResult(String message) {
        this.message = message;
    }

    // Getter and Setter methods for message...
}
