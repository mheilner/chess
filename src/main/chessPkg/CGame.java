package chessPkg;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CGame implements ChessGame {

    private TeamColor currentTurn;
    private CBoard board;

    public CGame() {
        currentTurn = TeamColor.WHITE; // Start with White's turn
        board = new CBoard(); // Initialize the game board
        // You can set up the initial chessboard configuration here if needed

    }

    @Override
    public TeamColor getTeamTurn() {
        return currentTurn;
    }

    @Override
    public void setTeamTurn(TeamColor team) {
        currentTurn = team;
    }

    @Override
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        // Implement the logic to calculate valid moves for the piece at the given position
        ChessPiece piece = board.getPiece(startPosition);
        if (piece != null && piece.getTeamColor() == currentTurn) {
            return piece.pieceMoves(board, startPosition);
        }
        return null;
    }

    @Override
    public void makeMove(ChessMove move) throws InvalidMoveException {
        // Implement the logic to make a move on the game board
        ChessPiece piece = board.getPiece(move.getStartPosition());
        boolean check = false;


        if (piece != null && piece.getTeamColor() == currentTurn) {
            //Get the moves
            Collection<ChessMove> validMoves = validMoves(move.getStartPosition());

            //Adding Check Logic //TODO Ok so It has the logic of the king but not if the pawn removes the isincheck kinda thing
            if(isInCheck(currentTurn) && piece.getPieceType() != ChessPiece.PieceType.KING){
                throw new InvalidMoveException("Invalid moves");
            }
            if (validMoves != null && validMoves.contains(move)) {
                // The move is valid, update the board
                board.addPiece(move.getEndPosition(), piece);
                board.addPiece(move.getStartPosition(), null);
                // Switch the turn to the other team
                currentTurn = (currentTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
            } else {
                throw new InvalidMoveException("Invalid moves");
            }
        } else {
            throw new InvalidMoveException("Invalid move");
        }
    }


    @Override
    public boolean isInCheck(TeamColor teamColor) {
        //Iterate over the board and find the location of the current king
        CPosition kingPosition = null;
        HashMap<ChessPiece, CPosition> otherTeam = new HashMap<ChessPiece, CPosition>();
        //Iterate over the board and add the king and the other team piece positions to
        for(int r=1; r < 9; r++){
            for(int c=1; c < 9; c++){
                if(board.getPiece(new CPosition(r,c))!=null){
                    if (board.getPiece(new CPosition(r,c)).getTeamColor() != teamColor){
                        otherTeam.put(board.getPiece(new CPosition(r,c)), new CPosition(r,c));
                    } else{
                        if(board.getPiece(new CPosition(r,c)).getPieceType() == ChessPiece.PieceType.KING){
                            kingPosition = new CPosition(r,c);
        }}}}}

        // Iterate over the opponent pieces
        for (Map.Entry<ChessPiece, CPosition> entry : otherTeam.entrySet()) {
            ChessPiece OpponentPiece = entry.getKey();
            CPosition OpponentPosition = entry.getValue();

            Collection<ChessMove> validMoves = OpponentPiece.pieceMoves(board, OpponentPosition);

            for (ChessMove move : validMoves) {
                if (move.getEndPosition().equals(kingPosition)) {
                    return true; // King is in check
                }
            }

        }
        return false;
    } //TODO

    @Override
//    public boolean isInCheckmate(TeamColor teamColor) {
//        // Check if the current player is in check
//        if (isInCheck(teamColor)) {
//            // Iterate through all the player's pieces
//            for (int r = 1; r <= 8; r++) {
//                for (int c = 1; c <= 8; c++) {
//                    ChessPosition startPosition = new CPosition(r, c);
//                    ChessPiece piece = board.getPiece(startPosition);
//
//                    // Check if the piece belongs to the current player
//                    if (piece != null && piece.getTeamColor() == teamColor) {
//                        // Iterate through the valid moves of the piece
//                        Collection<ChessMove> validMoves = validMoves(startPosition);
//                        for (ChessMove move : validMoves) {
//                            // Simulate the move on a temporary board
//                            CBoard tempBoard = new CBoard();
//                            tempBoard.copyBoard(board); // Implement a method to copy the board state
//                            tempBoard.addPiece(move.getEndPosition(), piece);
//                            tempBoard.addPiece(move.getStartPosition(), null);
//
//                            // Check if the move removes the king from check
//                            if (!isInCheck(teamColor, tempBoard)) {
//                                return false; // The player is not in checkmate
//                            }
//                        }
//                    }
//                }
//            }
//            return true; // No valid moves can remove the king from check (checkmate)
//        }
//        return false; // The player is not in check, cannot be in checkmate
//    }

    @Override
    public boolean isInStalemate(TeamColor teamColor) {
        // Check if the current player is not in check
        if (!isInCheck(teamColor)) {
            // Iterate through all the player's pieces
            for (int r = 1; r <= 8; r++) {
                for (int c = 1; c <= 8; c++) {
                    ChessPosition startPosition = new CPosition(r, c);
                    ChessPiece piece = board.getPiece(startPosition);

                    // Check if the piece belongs to the current player
                    if (piece != null && piece.getTeamColor() == teamColor) {
                        // Iterate through the valid moves of the piece
                        Collection<ChessMove> validMoves = validMoves(startPosition);
                        if (!validMoves.isEmpty()) {
                            return false; // The player has at least one valid move
                        }
                    }
                }
            }
            return true; // No valid moves are available (stalemate)
        }
        return false; // The player is in check, cannot be in stalemate
    }

//    // Add this method to your CBoard class
//    public void copyBoard(CBoard destination) {
//        // Iterate over all positions on the board
//        for (int row = 1; row <= 8; row++) {
//            for (int col = 1; col <= 8; col++) {
//                ChessPosition position = new CPosition(row, col);
//                ChessPiece piece = board.getPiece(position);
//
//                // Copy the piece to the destination board
//                if (piece != null) {
//                    destination.addPiece(position, piece.copy()); // Assuming ChessPiece has a copy method
//                } else {
//                    destination.addPiece(position, null);
//                }
//            }
//        }
//    }

    @Override
    public void setBoard(ChessBoard board) {
        this.board = (CBoard) board;
    }

    @Override
    public ChessBoard getBoard() {
        return board;
    }
}
