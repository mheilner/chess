package chessPkg;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Knight implements ChessPiece {

    private ChessGame.TeamColor teamColor;

    public Knight(ChessGame.TeamColor teamColor) {
        this.teamColor = teamColor;
    }

    @Override
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    @Override
    public PieceType getPieceType() {
        return PieceType.KNIGHT;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> validMoves = new ArrayList<>();

        int[][] knightMoves = {
                {-2, -1}, {-2, 1},
                {-1, -2}, {-1, 2},
                {1, -2}, {1, 2},
                {2, -1}, {2, 1}
        };

        int myRow = myPosition.getRow();
        int myColumn = myPosition.getColumn();

        for (int[] move : knightMoves) {
            int newRow = myRow + move[0];
            int newColumn = myColumn + move[1];

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

