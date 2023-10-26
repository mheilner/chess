package services;

import dataAccess.GameDao;
import dataAccess.AuthTokenDao;
import dataAccess.DataAccessException;
import requests.JoinGameRequest;
import results.JoinGameResult;

public class JoinGameService {

    private GameDao gameDao = GameDao.getInstance();
    private AuthTokenDao authTokenDao = AuthTokenDao.getInstance();

    public JoinGameResult joinGame(JoinGameRequest request, String authToken) {
        try {
            String username = authTokenDao.findUserByToken(authToken);
            if (username == null) {
                return new JoinGameResult("Invalid authentication token.");
            }
            gameDao.claimSpot(request.getGameID(), username, request.getPlayerColor());
            return new JoinGameResult(); // Success
        } catch (DataAccessException e) {
            // Depending on the error message you can return different results
            if ("Invalid authentication token.".equals(e.getMessage())) {
                return new JoinGameResult("Error: unauthorized");
            } else if ("White spot is already taken.".equals(e.getMessage())
                    || "Black spot is already taken.".equals(e.getMessage())) {
                return new JoinGameResult("Error: already taken");
            } else {
                return new JoinGameResult("Error: " + e.getMessage());
            }
        }
    }
}
