package chessPkg;

import chess.ChessPosition;

public class CPosition implements ChessPosition {
    //Instance Variables
    private int row;
    private int col;

    public CPosition(int r, int c){
        row = r;
        col = c;
    }
    @Override
    public int getRow() {
        return row;
    }
    @Override
    public int getColumn() {
        return col;
    }
}
