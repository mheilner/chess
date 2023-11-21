package chessPkg;

import chess.*;

public class CBoard implements ChessBoard {
    //Instance Variables
    private ChessPiece[][] pArr;

    public CBoard(){
        pArr = new ChessPiece[8][8];
    }

    @Override
    public void addPiece(ChessPosition position, ChessPiece piece) {
        int r = position.getRow() - 1;
        int c = position.getColumn() - 1;
        pArr[r][c] = piece;
    }

    @Override
    public ChessPiece getPiece(ChessPosition position) {
        int row = position.getRow() - 1; //Make it 0 indexed
        int column = position.getColumn() - 1;

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
        // Initialize the board to its default state
        // Place white pieces
        addPiece(new CPosition(1, 1), new Rook(ChessGame.TeamColor.WHITE));
        addPiece(new CPosition(1, 2), new Knight(ChessGame.TeamColor.WHITE));
        addPiece(new CPosition(1, 3), new Bishop(ChessGame.TeamColor.WHITE));
        addPiece(new CPosition(1, 4), new Queen(ChessGame.TeamColor.WHITE));
        addPiece(new CPosition(1, 5), new King(ChessGame.TeamColor.WHITE));
        addPiece(new CPosition(1, 6), new Bishop(ChessGame.TeamColor.WHITE));
        addPiece(new CPosition(1, 7), new Knight(ChessGame.TeamColor.WHITE));
        addPiece(new CPosition(1, 8), new Rook(ChessGame.TeamColor.WHITE));
        // Place white pawns
        for (int column = 1; column <= 8; ++column) {
            addPiece(new CPosition(2, column), new Pawn(ChessGame.TeamColor.WHITE));
        }
        // Place black pieces
        addPiece(new CPosition(8, 1), new Rook(ChessGame.TeamColor.BLACK));
        addPiece(new CPosition(8, 2), new Knight(ChessGame.TeamColor.BLACK));
        addPiece(new CPosition(8, 3), new Bishop(ChessGame.TeamColor.BLACK));
        addPiece(new CPosition(8, 4), new Queen(ChessGame.TeamColor.BLACK));
        addPiece(new CPosition(8, 5), new King(ChessGame.TeamColor.BLACK));
        addPiece(new CPosition(8, 6), new Bishop(ChessGame.TeamColor.BLACK));
        addPiece(new CPosition(8, 7), new Knight(ChessGame.TeamColor.BLACK));
        addPiece(new CPosition(8, 8), new Rook(ChessGame.TeamColor.BLACK));

        // Place black pawns
        for (int column = 1; column <= 8; ++column) {
            addPiece(new CPosition(7, column), new Pawn(ChessGame.TeamColor.BLACK));
        }

        // Clear the rest of the board
        for (int row = 2; row < 6; ++row) {
            for (int column = 0; column <= 7; ++column) {
                pArr[row][column] = null;
            }
        }
    }

}
