package passoffTests;
import chessPkg.*;


import chess.*;

/**
 * Used for testing your code
 * Add in code using your classes for each method for each FIXME
 */
public class TestFactory {

    //Chess Functions
    //------------------------------------------------------------------------------------------------------------------
    public static ChessBoard getNewBoard(){
        // FIXME
        return new CBoard();
    }

    public static ChessGame getNewGame(){
        // FIXME
		return new CGame();
    }

    public static ChessPiece getNewPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type){
        // FIXME
        switch (type) {
            case KING:
                return new King(pieceColor); // Create a new King piece
            case QUEEN:
                return new Queen(pieceColor); // Create a new Queen piece
            case BISHOP:
                return new Bishop(pieceColor); // Create a new Bishop piece
            case KNIGHT:
                return new Knight(pieceColor); // Create a new Knight piece
            case ROOK:
                return new Rook(pieceColor); // Create a new Rook piece
            case PAWN:
                return new Pawn(pieceColor); // Create a new Pawn piece
            default:
                throw new IllegalArgumentException("Invalid PieceType: " + type);
        }
    }

    public static ChessPosition getNewPosition(Integer row, Integer col){
        // FIXME
		return new CPosition(row, col);
    }

    public static ChessMove getNewMove(ChessPosition startPosition, ChessPosition endPosition, ChessPiece.PieceType promotionPiece){
        // FIXME
		return new CMove(startPosition, endPosition, promotionPiece);
    }
    //------------------------------------------------------------------------------------------------------------------


    //Server API's
    //------------------------------------------------------------------------------------------------------------------
    public static String getServerPort(){
        return "8080";
    }
    //------------------------------------------------------------------------------------------------------------------


    //Websocket Tests
    //------------------------------------------------------------------------------------------------------------------
    public static Long getMessageTime(){
        /*
        Changing this will change how long tests will wait for the server to send messages.
        3000 Milliseconds (3 seconds) will be enough for most computers. Feel free to change as you see fit,
        just know increasing it can make tests take longer to run.
        (On the flip side, if you've got a good computer feel free to decrease it)
         */
        return 3000L;
    }
    //------------------------------------------------------------------------------------------------------------------
}
