package services;

import dataAccess.GameDao;
import dataAccess.AuthTokenDao;
import dataAccess.DataAccessException;
import requests.JoinGameRequest;
import results.JoinGameResult;
/**
 * The {@code JoinGameService} class handles the operation of a user attempting to join a game.
 * It interacts with the {@code GameDao} to find game details and update them as well as
 * the {@code AuthTokenDao} to validate the user's authentication token.
 */
public class JoinGameService {

    private GameDao gameDao = GameDao.getInstance();
    private AuthTokenDao authTokenDao = AuthTokenDao.getInstance();

    /**
     * Attempts to add a user to a game based on the provided game ID and desired player color.
     * The user must have a valid authentication token to join a game.
     *
     * @param request The {@code JoinGameRequest} containing the game ID and player color that the user wishes to join as.
     * @param authToken The authentication token of the user attempting to join the game.
     * @return A {@code JoinGameResult} object containing the outcome of the join request, either success or a descriptive error message.
     * @throws DataAccessException If there is an error accessing the database during the join game operation.
     */
    public JoinGameResult joinGame(JoinGameRequest request, String authToken) {
        try {
            if (gameDao.find(request.getGameID())==null){
                return  new JoinGameResult("Invalid GameID Error");
            }
            String username = authTokenDao.findUserByToken(authToken);
            if (username == null) {
                return new JoinGameResult("Error: Invalid authentication token.");
            }

            // Check if the spot user wants to claim is already taken
            if ("WHITE".equalsIgnoreCase(request.getPlayerColor()) &&
                    gameDao.getWhitePlayer(request.getGameID()) != null) {
                return new JoinGameResult("Error: White spot is already taken.");
            }
            if ("BLACK".equalsIgnoreCase(request.getPlayerColor()) &&
                    gameDao.getBlackPlayer(request.getGameID()) != null) {
                return new JoinGameResult("Error: Black spot is already taken.");
            }
            if (request.getPlayerColor()==null){
                return new JoinGameResult();
            }

            gameDao.claimSpot(request.getGameID(), username, request.getPlayerColor());
            return new JoinGameResult(); // Success
        } catch (DataAccessException e) {
            // Depending on the error message you can return different results
            return new JoinGameResult("Error: " + e.getMessage());
        }
    }
}
