import chessPkg.CBoard;
import chessPkg.CPosition;
import chess.*;
import ui.EscapeSequences;

public class ChessBoardDisplay {

    public static void displayChessBoard(CBoard board) {
        System.out.println("Chessboard with White at Bottom:");
        printChessBoard(board, true);

        System.out.println("\nChessboard with Black at Bottom:");
        printChessBoard(board, false);
    }

    private static void printChessBoard(CBoard board, boolean whiteAtBottom) {
        // Loop through each row
        for (int row = 1; row <= 8; row++) {
            // Adjust row index based on orientation
            int adjustedRow = whiteAtBottom ? row : 9 - row;
            for (int col = 1; col <= 8; col++) {
                ChessPiece piece = board.getPiece(new CPosition(adjustedRow, col));
                printChessPiece(piece);
            }
            System.out.println(); // New line after each row
        }
    }

    private static void printChessPiece(ChessPiece piece) {
        if (piece == null) {
            System.out.print(EscapeSequences.EMPTY);
        } else {
            String pieceSymbol;
            switch (piece.getPieceType()) {
                case KING:
                    pieceSymbol = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_KING : EscapeSequences.BLACK_KING;
                    break;
                case QUEEN:
                    pieceSymbol = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_QUEEN : EscapeSequences.BLACK_QUEEN;
                    break;
                case BISHOP:
                    pieceSymbol = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
                    break;
                case KNIGHT:
                    pieceSymbol = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
                    break;
                case ROOK:
                    pieceSymbol = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_ROOK : EscapeSequences.BLACK_ROOK;
                    break;
                case PAWN:
                    pieceSymbol = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? EscapeSequences.WHITE_PAWN : EscapeSequences.BLACK_PAWN;
                    break;
                default:
                    pieceSymbol = EscapeSequences.EMPTY;
            }
            System.out.print(pieceSymbol);
        }
    }

}
