package webSocketMessages.userCommands;

import chess.ChessGame;
import chessPkg.CMove;

public class MakeMoveCommand extends UserGameCommand {
    private final Integer gameID;
    private final CMove move;

    public MakeMoveCommand(String authToken, Integer gameID, CMove move) {
        super(authToken);
        this.commandType = CommandType.MAKE_MOVE;
        this.gameID = gameID;
        this.move = move;
    }

    public Integer getGameID() {
        return gameID;
    }

    public CMove getMove() {
        return move;
    }

}
