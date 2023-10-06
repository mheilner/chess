package chessPkg;

import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Objects;

public class CMove implements chess.ChessMove {

    // Instance Variables
    private ChessPosition startPosition;
    private ChessPosition endPosition;
    private ChessPiece.PieceType promotionPiece;

    // Constructor
    public CMove(ChessPosition startPosition, ChessPosition endPosition) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = null; // Initialize to null; set it if pawn promotion is needed
    }

    // Constructor for moves that involve pawn promotion
    public CMove(ChessPosition startPosition, ChessPosition endPosition, ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }


    @Override
    public ChessPosition getStartPosition() {
        return startPosition;
    }

    @Override
    public ChessPosition getEndPosition() {
        return endPosition;
    }

    @Override
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }

    //TODO: Implement .equals() and .hashcode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CMove cMove = (CMove) o;
        return Objects.equals(startPosition, cMove.startPosition) && Objects.equals(endPosition, cMove.endPosition) && promotionPiece == cMove.promotionPiece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPosition, endPosition, promotionPiece);
    }
}
