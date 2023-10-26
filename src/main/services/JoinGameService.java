package services;

import dataAccess.GameDao;
import dataAccess.AuthTokenDao;
import dataAccess.DataAccessException;
import requests.JoinGameRequest;

public class JoinGameService {

    private GameDao gameDao = new GameDao();
    private AuthTokenDao authTokenDao = AuthTokenDao.getInstance();

    public boolean joinGame(JoinGameRequest request, String authToken) {
        try {
            String username = authTokenDao.findUserByToken(authToken);
            if(username == null) {
                throw new DataAccessException("Invalid authentication token.");
            }
            gameDao.claimSpot(request.getGameID(), username, request.getPlayerColor());
            return true;
        } catch (DataAccessException e) {
            // Log the error if needed
            return false;
        }
    }
}
