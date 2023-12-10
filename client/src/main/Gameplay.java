import java.util.Scanner;

import chess.ChessGame;
import chessPkg.CBoard;
import chessPkg.CGame;
import chessPkg.CMove;
import chessPkg.CPosition;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.GameDao;
import model.Game;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.JoinObserverCommand;
import webSocketMessages.userCommands.JoinPlayerCommand;
import webSocketMessages.userCommands.MakeMoveCommand;

public class Gameplay {
    private final String authToken;
    private final int gameId;
    private final String playerColor;
    private final Scanner scanner;
    private boolean isInGame;
    private WSClient webSocketClient;

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
        redrawBoard();

        while (isInGame) {
            System.out.print("Enter command ('help', 'move', 'redraw', 'leave', 'resign', 'highlight'): ");
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
            Gson gson = new Gson();

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
        System.out.println("Game state updated.");
        // Update your game state here with the game object
        redrawBoard();
    }

    private void handleError(ErrorMessage errorMessage) {
        System.out.println("Error: " + errorMessage.getErrorMessage());
    }

    private void handleNotification(NotificationMessage notificationMessage) {
        System.out.println("Notification: " + notificationMessage.getMessage());
    }



    private void displayHelp() {
        // Implement help display logic
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
            String moveJson = new Gson().toJson(makeMoveCommand);
            webSocketClient.sendMessage(moveJson);
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions
        }
    }

    private CPosition parseChessPosition(String position) {
        if (position.length() != 2) {
            throw new IllegalArgumentException("Invalid position format");
        }
        int col = position.charAt(0) - 'a' + 1; // Convert 'a' to 1, 'b' to 2, etc.
        int row = Integer.parseInt(position.substring(1)); // Convert the second character to an integer
        return new CPosition(row, col);
    }


    private void redrawBoard() throws DataAccessException {
        // Implement board redraw logic
        GameDao gameDao = GameDao.getInstance();
        Game joinedGame = gameDao.find(gameId);

        ChessBoardDisplay.printChessBoard((CBoard)joinedGame.getGame().getBoard(), !(playerColor.equals("BLACK")));
    }

    private void handleLeave() {
        // Implement leave game logic
        isInGame = false;
    }

    private void handleResign() {
        // Implement resign logic
    }

    private void highlightMoves() {
        // Implement move highlighting logic
    }

    // Additional methods for handling WebSocket messages, updating game state, etc.
}
