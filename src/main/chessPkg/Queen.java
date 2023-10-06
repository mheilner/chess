package chessPkg;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Queen implements ChessPiece {
    private ChessGame.TeamColor teamColor;

    public Queen(ChessGame.TeamColor teamColor){this.teamColor = teamColor;}
    @Override
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    @Override
    public PieceType getPieceType() {
        return PieceType.QUEEN;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> validMoves = new ArrayList<>();

        // Define the possible directions a queen can move (horizontal, vertical, and diagonal)
        int[][] directions = {
                {-1, 0}, {1, 0}, {0, -1}, {0, 1},  // Horizontal and vertical
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1} // Diagonal
        };

        int myRow = myPosition.getRow();
        int myColumn = myPosition.getColumn();

        for (int[] dir : directions) {
            for (int step = 1; step <= 7; step++) { // chessPkg.Queen can move up to 7 squares in any direction
                int newRow = myRow + dir[0] * step;
                int newColumn = myColumn + dir[1] * step;

                // Check if the new position is within the bounds of the board
                if (isValidPosition(newRow, newColumn)) {
                    ChessPosition newPosition = new CPosition(newRow, newColumn);
                    ChessPiece destinationPiece = board.getPiece(newPosition);

                    // If the destination is empty or has an opponent's piece, it's a valid move
                    if (destinationPiece == null || destinationPiece.getTeamColor() != teamColor) {
                        validMoves.add(new CMove(myPosition, newPosition));
                    }

                    // If there's a piece blocking the path, stop checking in this direction
                    if (destinationPiece != null) {
                        break;
                    }
                } else {
                    break; // Stop checking in this direction if out of bounds
                }
            }
        }
        return validMoves;
    }
    private boolean isValidPosition(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }

}
