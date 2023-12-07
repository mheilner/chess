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
import services.WSJoinObserverService;
import services.WSJoinPlayerService;
import services.WSMakeMoveService;
import webSocketMessages.userCommands.*;
import webSocketMessages.serverMessages.*;

@WebSocket
public class WSHandler {
    private GameDao gameDao = GameDao.getInstance();
    private AuthTokenDao authTokenDao = AuthTokenDao.getInstance();
    private final Gson gson = new Gson();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        // Determine the command type and deserialize accordingly
        CommandTypeWrapper commandTypeWrapper = gson.fromJson(message, CommandTypeWrapper.class);
        UserGameCommand command = deserializeCommand(commandTypeWrapper, message);

        // Process the command
        ServerMessage response = processCommand(command);

        // Serialize and send the response
        session.getRemote().sendString(gson.toJson(response));
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

    private ServerMessage processCommand(UserGameCommand command) {

        switch (command.getCommandType()) {
            case JOIN_PLAYER:
                return new WSJoinPlayerService().joinPlayer((JoinPlayerCommand) command);
            case JOIN_OBSERVER:
                return new WSJoinObserverService().joinObserver((JoinObserverCommand) command);
            case MAKE_MOVE:
                return new WSMakeMoveService().makeMove((MakeMoveCommand) command);
//            case LEAVE:
//                ...
//            case RESIGN:
//                ...
            default:
                return new ErrorMessage("Unknown command type");
        }
    }

    //////////////////////////////////////////////////////////////////////////
    //-------------- PROCESS COMMAND FUNCTIONS ------------------------------
    //////////////////////////////////////////////////////////////////////////

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
    public ServerMessage makeMove(MakeMoveCommand command) {
        try {
            Game game = gameDao.find(command.getGameID());
            if (game == null) {
                return new ErrorMessage("Invalid GameID Error");
            }

            CGame chessGame = game.getGame();
            CMove move = command.getMove();

            // Check if it's the correct player's turn
            if (chessGame.getTeamTurn() != chessGame.getBoard().getPiece(move.getStartPosition()).getTeamColor()) {
                return new ErrorMessage("It's not your turn");
            }

            try {
                chessGame.makeMove(move);
            } catch (InvalidMoveException e) {
                return new ErrorMessage("Invalid move: " + e.getMessage());
            }

            // Update the game state in the database
            gameDao.updateGameState(game.getGameID(), chessGame);

            // Return the updated game state
            return new LoadGameMessage(chessGame);

        } catch (DataAccessException e) {
            return new ErrorMessage("Error: " + e.getMessage());
        }
    }




    private static class CommandTypeWrapper {
        private UserGameCommand.CommandType commandType;

        public UserGameCommand.CommandType getCommandType() {
            return commandType;
        }
    }
}
