package requests;

/**
 * Request object for joining a game.
 */
public class JoinGameRequest {
    private String playerColor;
    private int gameID;

    /**
     * Constructor for JoinGameRequest.
     * @param playerColor Desired color for the player (WHITE/BLACK).
     * @param gameID ID of the game to join.
     */
    public JoinGameRequest(String playerColor, int gameID) {
        this.playerColor = playerColor;
        this.gameID = gameID;
    }

    public String getPlayerColor() {return playerColor;}
    public void setPlayerColor(String playerColor) {this.playerColor = playerColor;}
    public int getGameID() {return gameID;}
    public void setGameID(int gameID) {this.gameID = gameID;}
}
