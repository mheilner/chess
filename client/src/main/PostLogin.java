import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import requests.CreateGameRequest;
import results.CreateGameResult;

import java.io.OutputStream;
import java.io.InputStreamReader;
import com.google.gson.Gson;
import requests.LogoutRequest;
import results.LogoutResult;

public class PostLogin {
    private final String authToken;
    private Scanner scanner;
    private boolean isLoggedIn = true; // Flag to maintain login state

    public PostLogin(Scanner scanner, String authToken) {
        this.scanner = scanner;
        this.authToken = authToken;
    }

    public void displayMenu() {
        while (isLoggedIn) {
            System.out.println("Post-login menu:");
            System.out.println("1. List Games");
            System.out.println("2. Create Game");
            System.out.println("3. Join Game");
            System.out.println("4. Logout");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    listGames();
                    break;
                case 2:
                    createGame();
                    break;
                case 3:
                    joinGame();
                    break;
                case 4:
                    logout();
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

    private void listGames() {
        // Implementation to list games
        System.out.println("Listing games...");
    }

    private void createGame() {
        System.out.print("Enter game name: ");
        String gameName = scanner.nextLine();

        CreateGameRequest createGameRequest = new CreateGameRequest(gameName);
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(createGameRequest);

        try {
            URL url = new URL("http://localhost:8080/game"); // Replace with your server's create game URL
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", authToken); // Include authToken in request header
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonRequest.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            CreateGameResult createGameResult = gson.fromJson(new InputStreamReader(conn.getInputStream()), CreateGameResult.class);

            if (responseCode == 200) {
                System.out.println("Game created successfully. Game ID: " + createGameResult.getGameID());
            } else {
                System.out.println("Failed to create game. " + createGameResult.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void joinGame() {
        // Implementation to join a game
        System.out.println("Joining a game...");
    }
    private void logout() {
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
