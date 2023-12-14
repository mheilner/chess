package chessPkg;

import chess.*;

import java.util.*;

public class CGame implements ChessGame {

    private TeamColor currentTurn;
    private CBoard board;
    private boolean isOver = false;

    public CGame() {
        currentTurn = TeamColor.WHITE; // Start with White's turn
        board = new CBoard(); // Initialize the game board
        // You can set up the initial chessboard configuration here if needed
        board.resetBoard();
    }

    @Override
    public TeamColor getTeamTurn() {
        return currentTurn;
    }

    @Override
    public void setTeamTurn(TeamColor team) {
        currentTurn = team;
    }

    @Override
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        // Implement the logic to calculate valid moves for the piece at the given position
        ChessPiece piece = board.getPiece(startPosition);
        if (piece != null) { // Removed && piece.getTeamColor() == currentTurn
            Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
            List<ChessMove> valids = new ArrayList<>();
            for(ChessMove move : moves){
                ChessPiece potentiallyCapturedPiece = board.getPiece(move.getEndPosition());

                board.addPiece(move.getEndPosition(), piece);
                board.addPiece(move.getStartPosition(), null);
                //After making the move, is it still in check, if it is revert
                if(isInCheck(piece.getTeamColor())){
                    board.addPiece(move.getStartPosition(), piece);
                    board.addPiece(move.getEndPosition(), potentiallyCapturedPiece);
                }else{
                    board.addPiece(move.getStartPosition(), piece);
                    board.addPiece(move.getEndPosition(), potentiallyCapturedPiece);

                    valids.add(move);
                }
            }


            return valids;
        }
        return null;
    }


    @Override
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());

        if (piece != null && piece.getTeamColor() == currentTurn) {
            Collection<ChessMove> validMoves = validMoves(move.getStartPosition());


            if (validMoves != null && validMoves.contains(move)) {
                // The move is valid, update the board

                //If it is a pawn and promotion
                if(piece.getPieceType()== ChessPiece.PieceType.PAWN &&
                    ((move.getEndPosition().getRow()==8 && currentTurn == TeamColor.WHITE)||
                            (move.getEndPosition().getRow()==1 && currentTurn == TeamColor.BLACK))){
                    updatePositionPromotion(move);
                }else{
                    board.addPiece(move.getEndPosition(), piece);
                    board.addPiece(move.getStartPosition(), null);
                }

                //After making the move, is it still in check, if it is revert
                if(isInCheck(currentTurn)){
                    board.addPiece(move.getStartPosition(), piece);
                    board.addPiece(move.getEndPosition(), null);
                    throw new InvalidMoveException("Invalid moves");
                }

                // Switch the turn to the other team
                currentTurn = (currentTurn == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
            } else {
                throw new InvalidMoveException("Invalid move");
            }
        } else {
            throw new InvalidMoveException("Invalid move");
        }
    }



    @Override
    public boolean isInCheck(TeamColor teamColor) {
        //Iterate over the board and find the location of the current king
        CPosition kingPosition = null;
        HashMap<ChessPiece, CPosition> otherTeam = new HashMap<ChessPiece, CPosition>();
        //Iterate over the board and add the king and the other team piece positions to
        for(int r=1; r < 9; r++){
            for(int c=1; c < 9; c++){
                if(board.getPiece(new CPosition(r,c))!=null){
                    if (board.getPiece(new CPosition(r,c)).getTeamColor() != teamColor){
                        otherTeam.put(board.getPiece(new CPosition(r,c)), new CPosition(r,c));
                    } else{
                        if(board.getPiece(new CPosition(r,c)).getPieceType() == ChessPiece.PieceType.KING){
                            kingPosition = new CPosition(r,c);
        }}}}}

        // Iterate over the opponent pieces
        for (Map.Entry<ChessPiece, CPosition> entry : otherTeam.entrySet()) {
            ChessPiece OpponentPiece = entry.getKey();
            CPosition OpponentPosition = entry.getValue();

            Collection<ChessMove> validMoves = OpponentPiece.pieceMoves(board, OpponentPosition);

            for (ChessMove move : validMoves) {
                if (move.getEndPosition().equals(kingPosition)) {
                    return true;
                }
            }
        }
        return false;
    }



    @Override
    public boolean isInCheckmate(TeamColor teamColor) {
        // Check if the current player is in check
        if (isInCheck(teamColor)) {
            // Iterate through all the player's pieces
            for (int r = 1; r <= 8; r++) {
                for (int c = 1; c <= 8; c++) {
                    ChessPosition startPosition = new CPosition(r, c);
                    ChessPiece piece = board.getPiece(startPosition);

                    // Check if the piece belongs to the current player
                    if (piece != null && piece.getTeamColor() == teamColor) {
                        // Iterate through the valid moves of the piece
                        Collection<ChessMove> validMoves = validMoves(startPosition);
                        for (ChessMove move : validMoves) {
                            ChessPiece potentiallyCapturedPiece = board.getPiece(move.getEndPosition());

                            board.addPiece(move.getEndPosition(), piece);
                            board.addPiece(move.getStartPosition(), null);
                            //After making the move, is it still in check, if it is revert
                            if(isInCheck(teamColor)){
                                board.addPiece(move.getStartPosition(), piece);
                                board.addPiece(move.getEndPosition(), potentiallyCapturedPiece);
                                isOver = true;
                                return true;
                            }else{
                                board.addPiece(move.getStartPosition(), piece);
                                board.addPiece(move.getEndPosition(), potentiallyCapturedPiece);
                            }
                        }
                    }
                }
            }
            // If no move gets the player out of check, it's checkmate
            isOver = true;
            return true;
        }
        return false;
    }



    @Override
    public boolean isInStalemate(TeamColor teamColor) {
        // Check if the current player is not in check
        if (!isInCheck(teamColor)) {
            // Iterate through all the player's pieces
            for (int r = 1; r <= 8; r++) {
                for (int c = 1; c <= 8; c++) {
                    ChessPosition startPosition = new CPosition(r, c);
                    ChessPiece piece = board.getPiece(startPosition);

                    // Check if the piece belongs to the current player
                    if (piece != null && piece.getTeamColor() == teamColor) {
                        // Iterate through the valid moves of the piece
                        Collection<ChessMove> validMoves = validMoves(startPosition);
                        if (validMoves.isEmpty()) {
                            return true; //If no valid moves, then return false
                        }
                        for(ChessMove move : validMoves){
                            ChessPiece potentiallyCapturedPiece = board.getPiece(move.getEndPosition());

                            board.addPiece(move.getEndPosition(), piece);
                            board.addPiece(move.getStartPosition(), null);
                            //After making the move, is it still in check, if it is revert
                            if(isInCheck(teamColor)){
                                board.addPiece(move.getStartPosition(), piece);
                                board.addPiece(move.getEndPosition(), potentiallyCapturedPiece);
                            }else {
                                board.addPiece(move.getStartPosition(), piece);
                                board.addPiece(move.getEndPosition(), potentiallyCapturedPiece);
                                return false;
                            }
                        }
                    }
                }
            }
            return true; // No valid moves are available (stalemate)
        }
        return false; // The player is in check, cannot be in stalemate
    }


    private void updatePositionPromotion(ChessMove move){
        ChessPiece.PieceType prom = move.getPromotionPiece();
        ChessPiece piece;
        switch (prom) {
            case ROOK -> piece = new Rook(currentTurn);
            case KNIGHT -> piece = new Knight(currentTurn);
            case BISHOP -> piece = new Bishop(currentTurn);
            case QUEEN -> piece = new Queen(currentTurn);
            default ->
                    throw new UnsupportedOperationException("Unsupported piece type");
        }
        board.addPiece(move.getEndPosition(), piece);
        board.addPiece(move.getStartPosition(), null);
    }


    @Override
    public void setBoard(ChessBoard board) {
        this.board = (CBoard) board;
    }

    @Override
    public ChessBoard getBoard() {
        return board;
    }

    public boolean isFinished(){return isOver;}
    public void markGameAsOver() {this.isOver=true;}
}
