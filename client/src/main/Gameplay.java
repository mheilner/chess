import java.util.Scanner;

import chess.ChessGame;
import chessPkg.CBoard;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.GameDao;
import model.Game;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.JoinObserverCommand;
import webSocketMessages.userCommands.JoinPlayerCommand;

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

    public void handleWebSocketMessage(String message) {
        // Deserialize the message into a ServerMessage object
        ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);

        switch (serverMessage.getServerMessageType()) {
            case LOAD_GAME:
                // Handle LOAD_GAME message
                break;
            case ERROR:
                // Handle ERROR message
                break;
            case NOTIFICATION:
                // Handle NOTIFICATION message
                break;
            // Handle other types of messages
        }
    }


    private void displayHelp() {
        // Implement help display logic
    }

    private void handleMove() {
        // Implement move handling logic
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
