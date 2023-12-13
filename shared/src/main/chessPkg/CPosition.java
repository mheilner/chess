package chessPkg;

import chess.ChessPosition;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CPosition cPosition = (CPosition) o;
        return row == cPosition.row && col == cPosition.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        return "CPosition{" +
                "row=" + row +
                ", col=" + col +
                '}';
    }

    public String getBoardPosition() {
        char columnLetter = (char) ('a' + col - 1); // Convert column number to corresponding letter
        return "" + columnLetter + row;
    }

}
