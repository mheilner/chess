package webSocketMessages.userCommands;

public class JoinObserverCommand extends UserGameCommand {
    private final Integer gameID;

    public JoinObserverCommand(String authToken, Integer gameID) {
        super(authToken);
        this.commandType = CommandType.JOIN_OBSERVER;
        this.gameID = gameID;
    }

    public Integer getGameID() {
        return gameID;
    }
}
