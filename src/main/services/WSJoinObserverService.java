package services;

import dataAccess.GameDao;
import dataAccess.DataAccessException;
import webSocketMessages.userCommands.JoinObserverCommand;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import model.Game;

public class WSJoinObserverService {
    private GameDao gameDao = GameDao.getInstance();

    public ServerMessage joinObserver(JoinObserverCommand command) {
        try {
            Game game = gameDao.find(command.getGameID());
            if (game == null) {
                return new ErrorMessage("Invalid GameID Error");
            }
            // Observers don't affect game state, so just return the current state
            return new LoadGameMessage(game.getGame());
        } catch (DataAccessException e) {
            return new ErrorMessage("Error: " + e.getMessage());
        }
    }
}
