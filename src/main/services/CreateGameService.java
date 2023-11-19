package services;

import dataAccess.DataAccessException;
import dataAccess.GameDao;
import model.Game;
import requests.CreateGameRequest;
import results.CreateGameResult;
import chessPkg.CGame;

public class CreateGameService {

    /**
     * Creates a new game.
     * @param request Contains details for the game to be created.
     * @return CreateGameResult containing the gameID of the created game.
     */
    public CreateGameResult createGame(CreateGameRequest request) throws DataAccessException {
        GameDao gameDao = GameDao.getInstance();

        // Check if a game with the given name already exists
        if (gameDao.gameNameExists(request.getGameName())) {
            return new CreateGameResult("A game with this name already exists. Please choose another name.");
        }

        Game newGame = new Game(0, request.getGameName(),null, null,  new CGame());  // gameID and players will be set later
        int gameID = gameDao.insert(newGame);

        return new CreateGameResult(gameID);
    }

}
