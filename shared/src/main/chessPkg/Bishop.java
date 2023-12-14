package chessPkg;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Bishop implements ChessPiece {

    private ChessGame.TeamColor teamColor;

    public Bishop(ChessGame.TeamColor teamColor) {
        this.teamColor = teamColor;
    }

    @Override
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    @Override
    public PieceType getPieceType() {
        return PieceType.BISHOP;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> validMoves = new ArrayList<>();

        // Define the possible diagonal directions a bishop can move
        int[][] directions = {
                {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        };

        int myRow = myPosition.getRow();
        int myColumn = myPosition.getColumn();

        // Check each diagonal direction for valid moves
        for (int[] dir : directions) {
            for (int step = 1; step <= 7; step++) {
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
                    break;
                }
            }
        }

        return validMoves;
    }

    // Helper method to check if a position is within the bounds of the board
    private boolean isValidPosition(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }
}