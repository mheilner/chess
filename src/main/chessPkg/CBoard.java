package chessPkg;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;

public class CBoard implements ChessBoard {
    //Instance Variables
    private ChessPiece[][] pArr;

    public CBoard(){
        pArr = new ChessPiece[8][8];
    }

    @Override
    public void addPiece(ChessPosition position, ChessPiece piece) {
        pArr[position.getRow()][position.getColumn()] = piece;
    }

    @Override
    public ChessPiece getPiece(ChessPosition position) {
        int row = position.getRow();
        int column = position.getColumn();

        // Check if the position is within the bounds of the board
        if (row >= 0 && row < 8 && column >= 0 && column < 8) {
            return pArr[row][column];
        } else {
            // Position is outside the board, return null
            return null;
        }
    }

    @Override
    public void resetBoard() {

    }
}
