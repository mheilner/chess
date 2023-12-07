package services;

import chessPkg.CGame;
import dataAccess.GameDao;
import dataAccess.AuthTokenDao;
import dataAccess.DataAccessException;
import model.Game;
import webSocketMessages.userCommands.JoinObserverCommand;
import webSocketMessages.userCommands.JoinPlayerCommand;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;

public class WSJoinPlayerService {

    private GameDao gameDao = GameDao.getInstance();
    private AuthTokenDao authTokenDao = AuthTokenDao.getInstance();

    public ServerMessage joinPlayer(JoinPlayerCommand command) {
        try {
            // Validate authentication and game existence
            if (!authTokenDao.tokenExists(command.getAuthString())) {
                return new ErrorMessage("Error: Invalid authentication token.");
            }
            Game game = gameDao.find(command.getGameID());
            if (game == null) {
                return new ErrorMessage("Invalid GameID Error");
            }
            // Claim the spot in the game
            gameDao.claimSpot(command.getGameID(), authTokenDao.findUserByToken(command.getAuthString()),
                    command.getPlayerColor().toString());
            // Retrieve the updated game state
            Game updatedGame = gameDao.find(command.getGameID());
            if (updatedGame == null) {
                return new ErrorMessage("Error: Game not found after updating.");
            }
            // Return the updated game state
            return new LoadGameMessage(updatedGame.getGame());

        } catch (DataAccessException e) {
            return new ErrorMessage("Error: " + e.getMessage());
        }
    }




}
