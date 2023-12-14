package handler;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import chess.InvalidMoveException;
import chessPkg.CGame;
import chessPkg.CMove;
import chessPkg.CPosition;
import com.google.gson.*;
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
import java.lang.reflect.Type;

@WebSocket
public class WSHandler {
    private GameDao gameDao = GameDao.getInstance();
    private AuthTokenDao authTokenDao = AuthTokenDao.getInstance();
    private final GameSessionManager sessionManager = GameSessionManager.getInstance();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(ChessPiece.class, new GameDao.ChessPieceDeserializer())
            .registerTypeAdapter(ChessPiece.class, new GameDao.ChessPieceSerializer())
            .registerTypeAdapter(ChessPosition.class, new WSHandler.CPositionDeserializer()) // Use CPositionDeserializer for ChessPosition
            .registerTypeAdapter(CPosition.class, new WSHandler.CPositionSerializer())
            .registerTypeAdapter(ChessGame.class, new GameDao.ChessGameDeserializer())
            .create();

    public WSHandler() throws DataAccessException {
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        CommandTypeWrapper commandTypeWrapper = gson.fromJson(message, CommandTypeWrapper.class);
        UserGameCommand command = deserializeCommand(commandTypeWrapper, message);
        processCommand(command, session);
    }

    private UserGameCommand deserializeCommand(CommandTypeWrapper commandTypeWrapper, String json) {
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

    private void processCommand(UserGameCommand command, Session session) throws IOException, NullPointerException {
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
            case LEAVE:
                leaveGame((LeaveCommand) command, session);
                break;
            case RESIGN:
                resignGame((ResignCommand) command, session);
                break;
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

            ChessGame.TeamColor pColor = command.getPlayerColor();
            String playerName = authTokenDao.findUserByToken(command.getAuthString());
            if ((game.getBlackUsername() != null && !(game.getBlackUsername().equals(playerName)) && pColor==ChessGame.TeamColor.BLACK)||
                    (game.getWhiteUsername() != null  && !(game.getWhiteUsername().equals(playerName)) && pColor==ChessGame.TeamColor.WHITE) ) {
                ErrorMessage errorMsg = new ErrorMessage("Spot already taken");
                processErrorMessage(errorMsg, session); // session here is the root client's session
                return;
            }

            //Throw error for join empty game
            if (game.getBlackUsername() == null && game.getWhiteUsername() == null){
                ErrorMessage errorMsg = new ErrorMessage("Spot already taken");
                processErrorMessage(errorMsg, session); // session here is the root client's session
                return;
            }

            // Add player to the session manager
            sessionManager.addPlayerToGame(command.getGameID(), playerName, session);

            // Send the updated game state to the player
            Game updatedGame = gameDao.find(command.getGameID());
            session.getRemote().sendString(gson.toJson(new LoadGameMessage(updatedGame.getGame())));

            // Broadcast to all participants in the game, except joining player
            sessionManager.broadcastToGame(command.getGameID(), session, new NotificationMessage(playerName + " joined as " + command.getPlayerColor()));

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
            String playerName = authTokenDao.findUserByToken(command.getAuthString());
            sessionManager.addObserverToGame(command.getGameID(), playerName, session); // Replace "observer-name" with actual identifier
            // Send the updated game state to the player
            Game updatedGame = gameDao.find(command.getGameID());

            session.getRemote().sendString(gson.toJson(new LoadGameMessage(updatedGame.getGame())));

            // Broadcast to all participants in the game, except joining observer
            sessionManager.broadcastToGame(command.getGameID(), session, new NotificationMessage(playerName + " joined as observer"));

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

            // Player attempting move
            String playerName = authTokenDao.findUserByToken(command.getAuthString());
            ChessGame.TeamColor teamTurn = chessGame.getTeamTurn();

            // Check if it's the turn of the white team and the player is not the white player
            boolean isWhiteTurnButNotWhitePlayer = chessGame.getTeamTurn() == ChessGame.TeamColor.WHITE &&
                    (game.getWhiteUsername() == null || !game.getWhiteUsername().equals(playerName));
            // Check if it's the turn of the black team and the player is not the black player
            boolean isBlackTurnButNotBlackPlayer = chessGame.getTeamTurn() == ChessGame.TeamColor.BLACK &&
                    (game.getBlackUsername() == null || !game.getBlackUsername().equals(playerName));
            if (isWhiteTurnButNotWhitePlayer || isBlackTurnButNotBlackPlayer) {
                session.getRemote().sendString(gson.toJson(new ErrorMessage("Error: " + "It is " +teamTurn + "'s Team turn. " + playerName + " is not allowed to play for " + teamTurn + ".")));
                return;
            }

            if(chessGame.isFinished()){
                session.getRemote().sendString(gson.toJson(new ErrorMessage("Error: Can't Make the move, the game is over")));
                return;
            }

            if (chessGame.getTeamTurn() != chessGame.getBoard().getPiece(move.getStartPosition()).getTeamColor()) {
                session.getRemote().sendString(gson.toJson(new ErrorMessage("Error: It's not your turn")));
                return;
            }
            chessGame.makeMove(move);
            teamTurn = chessGame.getTeamTurn();

            //Check if the game is in checkmate
            if(chessGame.isInCheckmate(teamTurn)){
                sessionManager.broadcastToGame(command.getGameID(), null, new NotificationMessage(teamTurn +" is in Checkmate. GAME OVER!"));
                chessGame.markGameAsOver();
            } else if (chessGame.isInCheck(teamTurn)){ //Check if the game is in check
                sessionManager.broadcastToGame(command.getGameID(), null, new NotificationMessage(teamTurn +" is in Check."));
            }

            gameDao.updateGameState(game.getGameID(), chessGame);


            // Send the updated game state to all participants
            sessionManager.broadcastToGame(command.getGameID(), null, new LoadGameMessage(chessGame));

            //Get start and end position
            CPosition start = (CPosition)move.getStartPosition();
            CPosition end = (CPosition)move.getEndPosition();

            // Broadcast the move to all participants in the game
            sessionManager.broadcastToGame(command.getGameID(), session, new NotificationMessage(playerName + " moved their piece from " +
                    start.getBoardPosition() + " to " + end.getBoardPosition()));


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

            boolean isPlayer = false;
            if (game.getWhiteUsername() != null && game.getWhiteUsername().equals(participantName)) {
                isPlayer = true;
            }
            if (game.getBlackUsername() != null && game.getBlackUsername().equals(participantName)) {
                isPlayer = true;
            }
            if (isPlayer) {
                // Update the relevant player field in the game to null
                if (game.getWhiteUsername().equals(participantName)) {
                    game.setWhiteUsername(null);
                } else if (game.getBlackUsername().equals(participantName)) {
                    game.setBlackUsername(null);
                }
                gameDao.updateGamePlayers(command.getGameID(), game.getWhiteUsername(),game.getBlackUsername());
            }
            // Remove the participant from the game session
            sessionManager.removeParticipantFromGame(command.getGameID(), participantName);

            // Broadcast the departure to all participants in the game
            sessionManager.broadcastToGame(command.getGameID(), session, new NotificationMessage(participantName + " left the game"));

        } catch (DataAccessException e) {
            session.getRemote().sendString(gson.toJson(new ErrorMessage("Error: " + e.getMessage())));
        }
    }

    public void resignGame(ResignCommand command, Session session) throws IOException {
        try {
            String playerName = authTokenDao.findUserByToken(command.getAuthString());
            Game game = gameDao.find(command.getGameID());
            if (game == null) {
                session.getRemote().sendString(gson.toJson(new ErrorMessage("Invalid GameID Error")));
                return;
            }

            if(!(playerName.equals(game.getBlackUsername()))&&!(playerName.equals(game.getWhiteUsername()))){
                session.getRemote().sendString(gson.toJson(new ErrorMessage("Observers cannot resign")));
                return;
            }

            // Update game state to reflect resignation
            CGame chessGame = game.getGame();

            if(chessGame.isFinished()){
                session.getRemote().sendString(gson.toJson(new ErrorMessage("Game is already over")));
                return;
            }
            chessGame.markGameAsOver();
            gameDao.updateGameState(game.getGameID(), chessGame);

            // Broadcast resignation to all participants
            sessionManager.broadcastToGame(command.getGameID(), null,
                    new NotificationMessage(playerName + " resigned from the game"));

        } catch (DataAccessException e) {
            session.getRemote().sendString(gson.toJson(new ErrorMessage("Error: " + e.getMessage())));
        }
    }

    //////////////////////////////////////////////////////////////////////////
    //-------------- Server Message Functions ------------------------------
    //////////////////////////////////////////////////////////////////////////

    private void processErrorMessage(ErrorMessage errorMessage, Session rootClientSession) throws IOException {
        if (rootClientSession != null && rootClientSession.isOpen()) {
            rootClientSession.getRemote().sendString(gson.toJson(errorMessage));
        }
    }

    public static class CPositionSerializer implements JsonSerializer<CPosition> {
        @Override
        public JsonElement serialize(CPosition src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("row", src.getRow());
            jsonObject.addProperty("col", src.getColumn());
            return jsonObject;
        }
    }

    public static class CPositionDeserializer implements JsonDeserializer<ChessPosition> {
        @Override
        public ChessPosition deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            int row = jsonObject.get("row").getAsInt();
            int col = jsonObject.get("col").getAsInt(); // Use "col" instead of "column"
            return new CPosition(row, col);
        }
    }


    private static class CommandTypeWrapper {
        private UserGameCommand.CommandType commandType;
        public UserGameCommand.CommandType getCommandType() {
            return commandType;
        }
    }



}
