package chessPkg;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Pawn implements ChessPiece {

    private final ChessGame.TeamColor teamColor;

    public Pawn(ChessGame.TeamColor teamColor) {
        this.teamColor = teamColor;
    }

    @Override
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    @Override
    public PieceType getPieceType() {
        return PieceType.PAWN;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        List<ChessMove> validMoves = new ArrayList<>();

        int myRow = myPosition.getRow();
        int myColumn = myPosition.getColumn();
        int forwardDirection = (teamColor == ChessGame.TeamColor.WHITE) ? 1 : -1; // White pawns move up, black pawns move down

        // Check one square forward
        int newRow = myRow + forwardDirection;
        if (isValidPosition(newRow, myColumn) && board.getPiece(new CPosition(newRow, myColumn)) == null) {
            validMoves.add(new CMove(myPosition, new CPosition(newRow, myColumn)));
        }

        // Check two squares forward if it's the pawn's first move
        if ((teamColor == ChessGame.TeamColor.WHITE && myRow == 2) || (teamColor == ChessGame.TeamColor.BLACK && myRow == 7)) {
            newRow = myRow + 2 * forwardDirection;
            if (isValidPosition(newRow, myColumn) && board.getPiece(new CPosition(newRow, myColumn)) == null) {
                validMoves.add(new CMove(myPosition, new CPosition(newRow, myColumn)));
            }
        }

        // Check diagonal captures
        int[] captureColumns = {myColumn - 1, myColumn + 1};
        for (int col : captureColumns) {
            if (isValidPosition(newRow, col)) {
                ChessPosition capturePosition = new CPosition(newRow, col);
                ChessPiece capturedPiece = board.getPiece(capturePosition);
                if (capturedPiece != null && capturedPiece.getTeamColor() != teamColor) {
                    validMoves.add(new CMove(myPosition, capturePosition));
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
