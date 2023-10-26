package services;

import dataAccess.GameDao;
import dataAccess.AuthTokenDao;
import model.Game;
import results.ListGamesResult;

import java.util.List;

public class ListGamesService {

    private GameDao gameDao = new GameDao();
    private AuthTokenDao authTokenDao = new AuthTokenDao();

    /**
     * Lists all games.
     * @param authToken Token to verify the user.
     * @return ListGamesResult containing a list of all games.
     */
    public ListGamesResult listGames(String authToken) {
        try {
            // Verify the authToken
            String username = authTokenDao.findUserByToken(authToken);
            if(username == null) {
                return new ListGamesResult(null, "Invalid authentication token.");
            }

            List<Game> allGames = gameDao.findAll();

            if(allGames.isEmpty()) {
                return new ListGamesResult(null, "No games available.");
            }

            return new ListGamesResult(allGames, null);
        } catch (Exception e) {
            // Log the error if needed
            return new ListGamesResult(null, e.getMessage());
        }
    }
}
