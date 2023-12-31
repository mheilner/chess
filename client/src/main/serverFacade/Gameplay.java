package serverFacade;

import java.util.Collection;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import chessPkg.CBoard;
import chessPkg.CGame;
import chessPkg.CMove;
import chessPkg.CPosition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataAccess.DataAccessException;
import dataAccess.GameDao;
import handler.WSHandler;
import model.Game;
import ui.ChessBoardDisplay;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;
import websocket.WSClient;

public class Gameplay {
    private final String authToken;
    private final int gameId;
    private final String playerColor;
    private final Scanner scanner;
    private boolean isInGame;
    private WSClient webSocketClient;

    private Gson gson;

    public Gameplay(Scanner scanner, String authToken, int gameId, String playerColor) {
        this.scanner = scanner;
        this.authToken = authToken;
        this.gameId = gameId;
        this.playerColor = playerColor;
        this.isInGame = true;
        // Initialize WebSocket client and other necessary components
        initializeWebSocket();
    }

    public void startGameplay() throws DataAccessException {
        // Connect to WebSocket and initialize game state
        while (isInGame) {
            System.out.println("Enter command ('help', 'move', 'redraw', 'leave', 'resign', 'highlight'): ");
            String command = scanner.nextLine();
            switch (command.toLowerCase()) {
                case "help":
                    displayHelp();
                    break;
                case "move":
                    handleMove();
                    break;
                case "redraw":
                    redrawBoard();
                    break;
                case "leave":
                    handleLeave();
                    break;
                case "resign":
                    handleResign();
                    break;
                case "highlight":
                    highlightMoves();
                    break;
                default:
                    System.out.println("Invalid command. Please try again.");
                    break;
            }
        }
    }

    private void initializeWebSocket() {
        try {
            webSocketClient = new WSClient(this);

            gson = new GsonBuilder()
                    .registerTypeAdapter(ChessPiece.class, new GameDao.ChessPieceDeserializer())
                    .registerTypeAdapter(ChessPiece.class, new GameDao.ChessPieceSerializer())
                    .registerTypeAdapter(ChessPosition.class, new WSHandler.CPositionDeserializer()) // Use CPositionDeserializer for ChessPosition
                    .registerTypeAdapter(CPosition.class, new WSHandler.CPositionSerializer())
                    .registerTypeAdapter(ChessGame.class, new GameDao.ChessGameDeserializer())
                    .create();



            if (!playerColor.equalsIgnoreCase("OBSERVER")) {
                ChessGame.TeamColor teamColor = ChessGame.TeamColor.valueOf(playerColor);
                JoinPlayerCommand joinPlayerCommand = new JoinPlayerCommand(authToken, gameId, teamColor);
                String joinPlayerJson = gson.toJson(joinPlayerCommand);
                webSocketClient.sendMessage(joinPlayerJson);
            } else {
                JoinObserverCommand joinObserverCommand = new JoinObserverCommand(authToken, gameId);
                String joinObserverJson = gson.toJson(joinObserverCommand);
                webSocketClient.sendMessage(joinObserverJson);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions
        }
    }

    public void handleWebSocketMessage(String message, Gson gson) throws DataAccessException {
        ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);

        switch (serverMessage.getServerMessageType()) {
            case LOAD_GAME:
                LoadGameMessage loadGameMessage = gson.fromJson(message, LoadGameMessage.class);
                handleLoadGame(loadGameMessage);
                break;
            case ERROR:
                ErrorMessage errorMessage = gson.fromJson(message, ErrorMessage.class);
                handleError(errorMessage);
                break;
            case NOTIFICATION:
                NotificationMessage notificationMessage = gson.fromJson(message, NotificationMessage.class);
                handleNotification(notificationMessage);
                break;
            default:
                System.out.println("Received unknown message type: " + message);
                break;
        }
    }

    private void handleLoadGame(LoadGameMessage loadGameMessage) throws DataAccessException {
        // Update your game state and redraw the board
        CGame game = loadGameMessage.getGame();
        // Update your game state here with the game object
        redrawBoard();
        System.out.println("Game state updated.");
    }

    private void handleError(ErrorMessage errorMessage) {
        // Print this in red
        System.out.println(errorMessage.getErrorMessage());
    }

    private void handleNotification(NotificationMessage notificationMessage) {
        System.out.println("Notification: " + notificationMessage.getMessage());
    }


    private void displayHelp() {
        System.out.println("Available Commands:");
        System.out.println("  help       - Displays this help information.");
        System.out.println("  redraw     - Redraws the chess board.");
        System.out.println("  leave      - Removes you from the game and returns to the Post-Login UI.");
        System.out.println("  move       - Allows you to make a move. Follow the prompt to input your move.");
        System.out.println("  resign     - Allows you to resign from the game after confirmation.");
        System.out.println("  highlight  - Highlights legal moves for a selected piece. Input the piece to see legal moves.");
    }



    private void handleMove() {
        System.out.print("Enter start position (e.g., e2): ");
        String startPosition = scanner.nextLine();
        System.out.print("Enter end position (e.g., e4): ");
        String endPosition = scanner.nextLine();

        // Parse positions
        CPosition startCPosition = parseChessPosition(startPosition);
        CPosition endCPosition = parseChessPosition(endPosition);

        // Construct move command
        CMove move = new CMove(startCPosition, endCPosition);
        MakeMoveCommand makeMoveCommand = new MakeMoveCommand(authToken, gameId, move);

        try {
            String moveJson = gson.toJson(makeMoveCommand);
            webSocketClient.sendMessage(moveJson);
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions
        }
    }

    private CPosition parseChessPosition(String position) {
        if (position.length() != 2) {
//            throw new IllegalArgumentException("Invalid position format");
            System.out.println("Invalid position format");
            return null;
        }
        //Make Row and Col Calculations
        int row = Integer.parseInt(position.substring(1)); // Convert the second character to an integer
        int col = position.charAt(0) - 'a';

        return new CPosition(row, col + 1); // Adjusting to 1-based indexing
    }





    private void redrawBoard() throws DataAccessException {
        // Implement board redraw logic
        GameDao gameDao = GameDao.getInstance();
        Game joinedGame = gameDao.find(gameId);

        ChessBoardDisplay.printChessBoard((CBoard)joinedGame.getGame().getBoard(), !(playerColor.equals("BLACK")));
    }

    private void handleLeave() {

        LeaveCommand leaveCommand = new LeaveCommand(authToken, gameId);

        try {
            String leaveJson = gson.toJson(leaveCommand);
            webSocketClient.sendMessage(leaveJson);
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions
        }
        isInGame = false;
    }

    private void handleResign() {
        ResignCommand resignCommand = new ResignCommand(authToken, gameId);
        try{
            String resignJson = gson.toJson(resignCommand);
            webSocketClient.sendMessage(resignJson);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void highlightMoves() {
        System.out.print("Enter the position of the piece to highlight (e.g., e2): ");
        String piecePosition = scanner.nextLine();
        try {
            CPosition position = parseChessPosition(piecePosition);
            // Retrieve valid moves for the selected piece
            Collection<ChessMove> validMoves = getValidMoves(position);
            Set<CPosition> highlightPositions = new HashSet<>();
            highlightPositions.add(adjustPositionForPerspective(position));
            for (ChessMove move : validMoves) {
                highlightPositions.add(adjustPositionForPerspective((CPosition) move.getEndPosition()));
            }
            // Redraw the board with highlighted positions
            redrawBoardWithHighlights(highlightPositions);
        } catch (Exception e) {
            System.out.println("Invalid input. Please try again.");
        }
    }

    private CPosition adjustPositionForPerspective(CPosition position) {
        int adjustedRow = playerColor.equalsIgnoreCase("WHITE") ? position.getRow() : 9 - position.getRow();
        int adjustedCol = playerColor.equalsIgnoreCase("WHITE") ? position.getColumn() : 9 - position.getColumn();
        return new CPosition(adjustedRow, adjustedCol);
    }


    private Collection<ChessMove> getValidMoves(CPosition position) throws DataAccessException {
        GameDao gameDao = GameDao.getInstance();
        Game game = gameDao.find(gameId);
        return game.getGame().validMoves(position);
    }

    private void redrawBoardWithHighlights(Set<CPosition> highlightPositions) throws DataAccessException {
        GameDao gameDao = GameDao.getInstance();
        Game game = gameDao.find(gameId);
        ChessBoardDisplay.printChessBoardWithHighlights((CBoard)game.getGame().getBoard(), !(playerColor.equals("BLACK")), highlightPositions);
    }



}
