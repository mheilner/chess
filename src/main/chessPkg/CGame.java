package chessPkg;

import chess.*;

import java.util.Collection;

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
//        // Implement the logic to make a move on the game board
//        ChessPiece piece = board.getPiece(move.getStartPosition());
//
//        if (piece != null && piece.getTeamColor() == currentTurn) {
//            Collection<ChessMove> validMoves = pieceMoves(move.getStartPosition());
//            if (validMoves != null && validMoves.contains(move)) {
//                // The move is valid, update the board
//                board.addPiece(move.getEndPosition(), piece);
//                board.addPiece(move.getStartPosition(), null);
//                // Switch the turn to the other team
//                currentTurn = (currentTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
//            } else {
//                throw new InvalidMoveException("Invalid move");
//            }
//        } else {
//            throw new InvalidMoveException("Invalid move");
//        }
    }


//    @Override
//    public void makeMove(ChessMove move) throws InvalidMoveException {
//        ChessPosition startPosition = move.getStartPosition();
//        ChessPosition endPosition = move.getEndPosition();
//        ChessPiece.PieceType promotionPiece = move.getPromotionPiece();
//
//        ChessPiece piece = board.getPiece(startPosition);
//
//        if (piece != null && piece.getTeamColor() == currentTurn) {
//            Collection<ChessMove> validMoves = validMoves(startPosition);
//            ChessMove validMove = new chessPkg.CMove(startPosition, endPosition, promotionPiece);
//
//            if (validMoves != null && validMoves.contains(validMove)) {
//                // The move is valid, update the board
//                board.addPiece(endPosition, piece);
//                board.addPiece(startPosition, null);
//
//                // Handle pawn promotion if needed
//                if (promotionPiece != null && piece.getPieceType() == ChessPiece.PieceType.PAWN) {
//                    // Implement logic for pawn promotion (e.g., replace the pawn with the promoted piece)
//                    // You'll need to check the endPosition and the promotionPiece type
//                }
//
//                // Switch the turn to the other team
//                currentTurn = (currentTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
//            } else {
//                throw new InvalidMoveException("Invalid move");
//            }
//        } else {
//            throw new InvalidMoveException("Invalid move");
//        }
//    }


    @Override
    public boolean isInCheck(TeamColor teamColor) {
        return false;
    }

    @Override
    public boolean isInCheckmate(TeamColor teamColor) {
        return false;
    }

    @Override
    public boolean isInStalemate(TeamColor teamColor) {
        return false;
    }

    @Override
    public void setBoard(ChessBoard board) {

    }

    @Override
    public ChessBoard getBoard() {
        return null;
    }
}
