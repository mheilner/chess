package services;

import chess.InvalidMoveException;
import chessPkg.CBoard;
import chessPkg.CGame;
import chessPkg.CMove;
import dataAccess.GameDao;
import dataAccess.DataAccessException;
import webSocketMessages.userCommands.MakeMoveCommand;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import model.Game;

public class WSMakeMoveService {

    private GameDao gameDao = GameDao.getInstance();

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


}
