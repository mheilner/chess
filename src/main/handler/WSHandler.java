package handler;

import chess.InvalidMoveException;
import chessPkg.CGame;
import chessPkg.CMove;
import com.google.gson.Gson;
import dataAccess.AuthTokenDao;
import dataAccess.DataAccessException;
import dataAccess.GameDao;
import model.Game;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import server.GameSessionManager;
import webSocketMessages.userCommands.*;
import webSocketMessages.serverMessages.*;

import java.io.IOException;

@WebSocket
public class WSHandler {
    private GameDao gameDao = GameDao.getInstance();
    private AuthTokenDao authTokenDao = AuthTokenDao.getInstance();
    private final Gson gson = new Gson();
    private final GameSessionManager sessionManager = GameSessionManager.getInstance();


    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        // Determine the command type and deserialize accordingly
        CommandTypeWrapper commandTypeWrapper = gson.fromJson(message, CommandTypeWrapper.class);
        UserGameCommand command = deserializeCommand(commandTypeWrapper, message);

        // Process the command and handle communication within the method
        processCommand(command, session);
    }

    private UserGameCommand deserializeCommand(CommandTypeWrapper commandTypeWrapper, String json) {
        // Based on the commandType, deserialize to the specific subclass
        switch (commandTypeWrapper.getCommandType()) {
            case JOIN_PLAYER:
                return gson.fromJson(json, JoinPlayerCommand.class);
            case JOIN_OBSERVER:
                return gson.fromJson(json, JoinObserverCommand.class);
            case LEAVE:
                return gson.fromJson(json, LeaveCommand.class);
            case MAKE_MOVE:
                return gson.fromJson(json, MakeMoveCommand.class);
            case RESIGN:
                return gson.fromJson(json, ResignCommand.class);
            default:
                throw new IllegalArgumentException("Unknown command type");
        }
    }

    private void processCommand(UserGameCommand command, Session session) throws IOException {
        switch (command.getCommandType()) {
            case JOIN_PLAYER:
                joinPlayer((JoinPlayerCommand) command, session);
                break;
            case JOIN_OBSERVER:
                joinObserver((JoinObserverCommand) command, session);
                break;
            case MAKE_MOVE:
                makeMove((MakeMoveCommand) command, session);
                break;

//            case LEAVE:
//                ...
//            case RESIGN:
//                ...

            default:
                session.getRemote().sendString(gson.toJson(new ErrorMessage("Unknown command type")));
                break;
        }
    }

    //////////////////////////////////////////////////////////////////////////
    //-------------- PROCESS COMMAND FUNCTIONS ------------------------------
    //////////////////////////////////////////////////////////////////////////

    public void joinPlayer(JoinPlayerCommand command, Session session) {
        try {
            if (!authTokenDao.tokenExists(command.getAuthString())) {
                session.getRemote().sendString(gson.toJson(new ErrorMessage("Error: Invalid authentication token.")));
                return;
            }

            Game game = gameDao.find(command.getGameID());
            if (game == null) {
                session.getRemote().sendString(gson.toJson(new ErrorMessage("Invalid GameID Error")));
                return;
            }

            String playerName = authTokenDao.findUserByToken(command.getAuthString());
            gameDao.claimSpot(command.getGameID(), playerName, command.getPlayerColor().toString());

            // Add player to the session manager
            sessionManager.addPlayerToGame(command.getGameID(), playerName, session);

            // Broadcast to all participants in the game
            sessionManager.broadcastToGame(command.getGameID(), session, new NotificationMessage(playerName + " joined as " + command.getPlayerColor()));

            // Send the updated game state to the player
            Game updatedGame = gameDao.find(command.getGameID());
            session.getRemote().sendString(gson.toJson(new LoadGameMessage(updatedGame.getGame())));

        } catch (DataAccessException | IOException e) {
            // Handle exceptions and send error message to the session
        }
    }


    public void joinObserver(JoinObserverCommand command, Session session) throws IOException {
        try {
            Game game = gameDao.find(command.getGameID());
            if (game == null) {
                // Send error message directly to the session
                session.getRemote().sendString(gson.toJson(new ErrorMessage("Invalid GameID Error")));
                return;
            }
            sessionManager.addObserverToGame(command.getGameID(), "observer-name", session); // Replace "observer-name" with actual identifier
            // Broadcast to all participants in the game
            sessionManager.broadcastToGame(command.getGameID(), session, new NotificationMessage("Observer joined"));

            // Return the current game state to the observer
            new LoadGameMessage(game.getGame());

        } catch (DataAccessException e) {
            // Send error message directly to the session
            session.getRemote().sendString(gson.toJson(new ErrorMessage("Error: " + e.getMessage())));
        }
    }

    public void makeMove(MakeMoveCommand command, Session session) throws IOException {
        try {
            Game game = gameDao.find(command.getGameID());
            if (game == null) {
                session.getRemote().sendString(gson.toJson(new ErrorMessage("Invalid GameID Error")));
                return;
            }

            CGame chessGame = game.getGame();
            CMove move = command.getMove();

            if (chessGame.getTeamTurn() != chessGame.getBoard().getPiece(move.getStartPosition()).getTeamColor()) {
                session.getRemote().sendString(gson.toJson(new ErrorMessage("It's not your turn")));
                return;
            }

            chessGame.makeMove(move);
            gameDao.updateGameState(game.getGameID(), chessGame);

            // Broadcast the move to all participants in the game
            String playerName = authTokenDao.findUserByToken(command.getAuthString());
            sessionManager.broadcastToGame(command.getGameID(), session, new NotificationMessage(playerName + " made a move"));

            // Send the updated game state to all participants
            sessionManager.broadcastToGame(command.getGameID(), null, new LoadGameMessage(chessGame));

        } catch (InvalidMoveException e) {
            session.getRemote().sendString(gson.toJson(new ErrorMessage("Invalid move: " + e.getMessage())));
        } catch (DataAccessException | IOException e) {
            // Handle exceptions and send error message to the session
        }
    }

    public void leaveGame(LeaveCommand command, Session session) throws IOException {
        try {
            String participantName = authTokenDao.findUserByToken(command.getAuthString());
            if (participantName == null) {
                session.getRemote().sendString(gson.toJson(new ErrorMessage("Error: Invalid authentication token.")));
                return;
            }

            Game game = gameDao.find(command.getGameID());
            if (game == null) {
                session.getRemote().sendString(gson.toJson(new ErrorMessage("Invalid GameID Error")));
                return;
            }

            // Remove the participant from the game session
            sessionManager.removeParticipantFromGame(command.getGameID(), participantName);

            // Broadcast the departure to all participants in the game
            // Note: As the participant has left, no session is excluded from the broadcast
            sessionManager.broadcastToGame(command.getGameID(), null, new NotificationMessage(participantName + " left the game"));

        } catch (DataAccessException e) {
            session.getRemote().sendString(gson.toJson(new ErrorMessage("Error: " + e.getMessage())));
        }
    }




//    public void resignGame(ResignCommand command, Session session) throws IOException {
//        try {
//            String playerName = authTokenDao.findUserByToken(command.getAuthString());
//            Game game = gameDao.find(command.getGameID());
//            if (game == null) {
//                session.getRemote().sendString(gson.toJson(new ErrorMessage("Invalid GameID Error")));
//                return;
//            }
//
//            // Update game state to reflect resignation
//            CGame chessGame = game.getGame();
//            chessGame.resign(playerName); // You might need to implement this method in your game logic
//            gameDao.updateGameState(game.getGameID(), chessGame);
//
//            // Broadcast resignation to all participants
//            sessionManager.broadcastToGame(command.getGameID(), null,
//                    new NotificationMessage(playerName + " resigned from the game"));
//
//            // Additional logic for handling end of the game if necessary
//
//        } catch (DataAccessException | InvalidMoveException e) {
//            session.getRemote().sendString(gson.toJson(new ErrorMessage("Error: " + e.getMessage())));
//        }
//    }



    private static class CommandTypeWrapper {
        private UserGameCommand.CommandType commandType;

        public UserGameCommand.CommandType getCommandType() {
            return commandType;
        }
    }
}
