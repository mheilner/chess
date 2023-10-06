package chessPkg;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class King implements ChessPiece {

    private ChessGame.TeamColor teamColor;
    public King(ChessGame.TeamColor teamColor){
        this.teamColor = teamColor;
    }

    @Override
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    @Override
    public PieceType getPieceType() {
        return PieceType.KING;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> validMoves = new ArrayList<>();

        //Define the kings possible directions
        int[][] directions = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1},           {0, 1},
                {1, -1}, {1, 0}, {1, 1}
        };

        int myRow = myPosition.getRow();
        int myColumn = myPosition.getColumn();

        for (int[] dir : directions) {
            int newRow = myRow + dir[0];
            int newColumn = myColumn + dir[1];

            // Check if the new position is within the bounds of the board
            if (isValidPosition(newRow, newColumn)) {
                ChessPosition newPosition = new CPosition(newRow, newColumn);
                ChessPiece destinationPiece = board.getPiece(newPosition);

                // If the destination is empty or has an opponent's piece, it's a valid move
                if (destinationPiece == null || destinationPiece.getTeamColor() != teamColor) {
                    validMoves.add(new CMove(myPosition, newPosition));
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


