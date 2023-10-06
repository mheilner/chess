package chessPkg;

import chess.ChessPiece;
import chess.ChessPosition;

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
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    //It is helpful but not necessary to implement this
    @Override
    public String toString() {
        return super.toString();
    }
}
