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
            if (gameDao.find(request.getGameID())==null){
                return  new JoinGameResult("Invalid GameID Error");
            }
            String username = authTokenDao.findUserByToken(authToken);
            if (username == null) {
                return new JoinGameResult("Error: Invalid authentication token.");
            }
//            // Check if user is already part of the game
//            if (username.equals(gameDao.getWhitePlayer(request.getGameID())) ||
//                    username.equals(gameDao.getBlackPlayer(request.getGameID()))) {
//                return new JoinGameResult("User is already part of the game.");
//            }

            // Check if the spot user wants to claim is already taken
            if ("WHITE".equalsIgnoreCase(request.getPlayerColor()) &&
                    gameDao.getWhitePlayer(request.getGameID()) != null) {
                return new JoinGameResult("Error: White spot is already taken.");
            }
            if ("BLACK".equalsIgnoreCase(request.getPlayerColor()) &&
                    gameDao.getBlackPlayer(request.getGameID()) != null) {
                return new JoinGameResult("Error: Black spot is already taken.");
            }

            gameDao.claimSpot(request.getGameID(), username, request.getPlayerColor());
            return new JoinGameResult(); // Success
        } catch (DataAccessException e) {
            // Depending on the error message you can return different results
            return new JoinGameResult("Error: " + e.getMessage());
        }
    }
}
