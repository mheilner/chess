package webSocketMessages.serverMessages;

import chessPkg.CBoard;
import chessPkg.CGame;

public class LoadGameMessage extends ServerMessage {
    private final CGame game;

    public LoadGameMessage(CGame game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public CGame getGame() {
        return game;
    }
}
