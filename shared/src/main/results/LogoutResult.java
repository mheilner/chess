package results;

public class LogoutResult {
    private boolean success;
    private String message;

    public LogoutResult(boolean success) {
        this.success = success;
    }

    public LogoutResult(String message) {
        this.success = false;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
