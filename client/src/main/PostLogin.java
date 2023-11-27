import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.List;

import chessPkg.CBoard;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;

import model.Game;
import requests.CreateGameRequest;
import requests.JoinGameRequest;
import results.CreateGameResult;
import results.JoinGameResult;
import results.ListGamesResult;
import results.LogoutResult;

import dataAccess.GameDao;

import static ui.EscapeSequences.*;

public class PostLogin {
    private final String authToken;
    private Scanner scanner;
    private boolean isLoggedIn = true;

    public PostLogin(Scanner scanner, String authToken) {
        this.scanner = scanner;
        this.authToken = authToken;
    }

    public void displayMenu() {
        while (isLoggedIn) {
            System.out.println(SET_TEXT_BOLD + SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_BLUE + "Post-login Menu:");
            System.out.println("1. List Games");
            System.out.println("2. Create Game");
            System.out.println("3. Join Game");
            System.out.println("4. Help");
            System.out.println("5. Logout");
            System.out.println("6. Quit Game");
            System.out.print(SET_TEXT_COLOR_YELLOW + "Enter choice: " + RESET_TEXT_COLOR + RESET_TEXT_BOLD_FAINT);

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    listGames();
                    break;
                case "2":
                    createGame();
                    break;
                case "3":
                    joinGame();
                    break;
                case "4":
                    printHelp();
                    break;
                case "5":
                    logout();
                    break;
                case "6":
                    quitGame();
                    break;
                default:
                    System.out.println(SET_TEXT_COLOR_RED + "Invalid choice. Please try again." + RESET_TEXT_COLOR);
                    break;
            }
        }
    }

    private void printHelp() {
        System.out.println(SET_TEXT_COLOR_GREEN + "Help Menu:");
        System.out.println("1. List Games - Lists all available games");
        System.out.println("2. Create Game - Start a new game");
        System.out.println("3. Join Game - Join an existing game");
        System.out.println("4. Help - Display this help message");
        System.out.println("5. Logout - Log out from the application");
        System.out.println("6. Quit Game - Quit the current game" + RESET_TEXT_COLOR);
    }

    private void quitGame() {
        // Implement the logic to quit a joined game
        System.out.println(SET_TEXT_COLOR_GREEN + "You have quit the game." + RESET_TEXT_COLOR);
        // Additional logic can go here, if necessary
    }

    public void listGames() {
        Gson gson = GameDao.GsonUtil.createGson();

        try {
            URL url = new URL("http://localhost:8080/game");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", authToken);

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                ListGamesResult gamesResult = gson.fromJson(new InputStreamReader(conn.getInputStream()), new TypeToken<ListGamesResult>(){}.getType());
                displayGames(gamesResult.getGames());
            } else {
                System.out.println(SET_TEXT_COLOR_RED + "Failed to retrieve games list." + RESET_TEXT_COLOR);
            }
        } catch (Exception e) {
            System.out.println(SET_TEXT_COLOR_RED + "Error: " + e.getMessage() + RESET_TEXT_COLOR);
        }
    }

    public void displayGames(List<Game> games) {
        if (games == null || games.isEmpty()) {
            System.out.println(SET_TEXT_COLOR_YELLOW + "No games available." + RESET_TEXT_COLOR);
            return;
        }

        System.out.println(SET_TEXT_COLOR_GREEN + "Available games:" + SET_TEXT_COLOR_GREEN);
        for (int i = 0; i < games.size(); i++) {
            Game game = games.get(i);
            System.out.printf("%d. %s - White: %s, Black: %s\n", i + 1, game.getGameName(), game.getWhiteUsername(), game.getBlackUsername());
        }
    }

    public void createGame() {
        System.out.print(SET_TEXT_COLOR_WHITE + "Enter game name: ");
        String gameName = scanner.nextLine();
        CreateGameRequest createGameRequest = new CreateGameRequest(gameName);
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(createGameRequest);
        try {
            URL url = new URL("http://localhost:8080/game");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", authToken);
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonRequest.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            CreateGameResult createGameResult = gson.fromJson(new InputStreamReader(conn.getInputStream()), CreateGameResult.class);

            if (responseCode == 200) {
                System.out.println(SET_TEXT_COLOR_GREEN + "Game created successfully. Game ID: " + createGameResult.getGameID());
            } else {
                System.out.println(SET_TEXT_COLOR_RED + "Failed to create game. " + createGameResult.getMessage());
            }
        } catch (Exception e) {
            System.out.println(SET_TEXT_COLOR_RED+"Error: " + e.getMessage());
        }
    }

    private void joinGame() {
        System.out.print(SET_TEXT_COLOR_WHITE + "Enter game ID to join: ");
        int gameID = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.print("Enter player color (WHITE/BLACK) or OBSERVER: ");
        String playerColor = scanner.nextLine().toUpperCase();

        JoinGameRequest joinGameRequest = new JoinGameRequest(playerColor, gameID);
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(joinGameRequest);

        try {
            URL url = new URL("http://localhost:8080/game"); // Use your server's join game URL
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT"); // As per your server's expectation
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", authToken); // Include authToken in request header
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonRequest.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                System.out.println("Successfully joined game.");
                try {
                    GameDao gameDao = GameDao.getInstance();
                    Game joinedGame = gameDao.find(gameID);
                    if (joinedGame != null) {
                        ChessBoardDisplay.displayChessBoard((CBoard)joinedGame.getGame().getBoard());
                    } else {
                        System.out.println("Failed to retrieve the game details.");
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            } else {
                // Read the error message from the response body
                InputStreamReader isr = new InputStreamReader(conn.getErrorStream());
                JoinGameResult joinGameResult = gson.fromJson(isr, JoinGameResult.class);
                System.out.println(responseCode + ": " + joinGameResult.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }



    public void logout() {
        try {
            URL url = new URL("http://localhost:8080/session"); // Use your server's logout URL
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Authorization", authToken); // Send authToken in the header

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                System.out.println("Logged out successfully.");
                isLoggedIn = false; // Exit the post-login UI
            } else {
                System.out.println("Logout failed. Response Code: " + responseCode);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

}
