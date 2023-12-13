import chessPkg.CBoard;
import chessPkg.CPosition;
import chess.*;
import ui.EscapeSequences;

import java.util.Set;

public class ChessBoardDisplay {

    public static void displayChessBoard(CBoard board) {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + "Chessboard with White at Bottom:");
        printChessBoard(board, true);

        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + "\nChessboard with Black at Bottom:");
        printChessBoard(board, false);
    }

    public static void printChessBoard(CBoard board, boolean whiteAtBottom) {
        // Display numbers on the side of the board
        for (int row = 8; row >= 1; row--) {
            int adjustedRow = whiteAtBottom ? row : 9 - row;
            System.out.print(adjustedRow + " "); // Add row number to the left side

            for (int col = 1; col <= 8; col++) {
                int adjustedCol = whiteAtBottom ? col : 9 - col;
                String bgColor = (row + col) % 2 == 0 ? EscapeSequences.BG_COLOR_LIGHT_SQUARE : EscapeSequences.BG_COLOR_DARK_SQUARE;
                System.out.print(bgColor);
                ChessPiece piece = board.getPiece(new CPosition(adjustedRow, adjustedCol));
                printChessPiece(piece);
                System.out.print(EscapeSequences.RESET_BG_COLOR); // Reset background color
            }
            System.out.println(EscapeSequences.RESET_BG_COLOR); // New line after each row
        }

        // Display letters at the bottom of the board
        System.out.print("  "); // Align with the board
        for (int col = 1; col <= 8; col++) {
            char letter = (char) (whiteAtBottom ? 'a' + col - 1 : 'h' - col + 1);
            System.out.print(" " + letter + " ");
        }
        System.out.println();
    }


        public static void printChessBoardWithHighlights(CBoard board, boolean whiteAtBottom, Set<CPosition> highlightPositions) {
            // similar structure to printChessBoard method
            for (int row = 8; row >= 1; row--) {
                int adjustedRow = whiteAtBottom ? row : 9 - row;
                System.out.print(adjustedRow + " ");
                for (int col = 1; col <= 8; col++) {
                    String bgColor = determineBackgroundColor(row, col, highlightPositions, whiteAtBottom);
                    System.out.print(bgColor);
                    ChessPiece piece = board.getPiece(new CPosition(adjustedRow, col));
                    printChessPiece(piece);
                    System.out.print(EscapeSequences.RESET_BG_COLOR); // Reset background color
                }
                System.out.println(EscapeSequences.RESET_BG_COLOR); // New line after each row
            }
            System.out.print("  "); // Align with the board
            for (int col = 1; col <= 8; col++) {
                char letter = (char) (whiteAtBottom ? 'a' + col - 1 : 'h' - col + 1);
                System.out.print(" " + letter + " ");
            }
            System.out.println();
        }

        private static String determineBackgroundColor(int row, int col, Set<CPosition> highlightPositions, boolean whiteAtBottom) {
            CPosition currentPos = new CPosition(whiteAtBottom ? row : 9 - row, whiteAtBottom ? col : 9 - col);
            if (highlightPositions.contains(currentPos)) {
                return EscapeSequences.SET_BG_COLOR_YELLOW; // BG color for highlighted positions
            } else {
                return (row + col) % 2 == 0 ? EscapeSequences.BG_COLOR_LIGHT_SQUARE : EscapeSequences.BG_COLOR_DARK_SQUARE;
            }
        }


    private static void printChessPiece(ChessPiece piece) {
        String pieceSymbol = EscapeSequences.EMPTY; // Default empty symbol
        if (piece != null) {
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
        }
        System.out.print(pieceSymbol);
    }

}
