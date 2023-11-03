package model;


import chessPkg.CGame;

/**
 * Represents a Game in the Chess server application.
 */
public class Game {
    private int gameID;
    private String whiteUsername;
    private String blackUsername;
    private String gameName;
    private CGame game;
    public Game(int gameID, String gameName, String whiteUsername, String blackUsername, CGame game) {
        this.gameID = gameID;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
        this.game = game;
    }

    public int getGameID() {return gameID;}
    public void setGameID(int gameID) {this.gameID = gameID;}
    public String getWhiteUsername() {return whiteUsername;}
    public void setWhiteUsername(String whiteUsername) {this.whiteUsername = whiteUsername;}
    public String getBlackUsername() {return blackUsername;}
    public void setBlackUsername(String blackUsername) {this.blackUsername = blackUsername;}
    public String getGameName() {return gameName;}
    public void setGameName(String gameName) {this.gameName = gameName;}
    public CGame getGame() {return game;}
    public void setGame(CGame game) {this.game = game;}
}
