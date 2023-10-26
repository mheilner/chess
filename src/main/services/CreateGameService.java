package services;

import dataAccess.DataAccessException;
import dataAccess.GameDao;
import model.Game;
import requests.CreateGameRequest;
import results.CreateGameResult;

public class CreateGameService {

    /**
     * Creates a new game.
     * @param request Contains details for the game to be created.
     * @return CreateGameResult containing the gameID of the created game.
     */
    public CreateGameResult createGame(CreateGameRequest request) {
        GameDao gameDao = new GameDao();

        Game newGame = new Game(0, null, null, request.getGameName(), null);  // gameID and players will be set later
        int gameID = gameDao.insert(newGame);

        return new CreateGameResult(gameID);
    }
}
