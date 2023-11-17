import java.util.Scanner;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import com.google.gson.Gson;
import requests.LoginRequest;
import results.LoginResult;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {
    // Define ANSI color code constants
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;

        while (isRunning) {
            System.out.print("Enter command: ");
            String command = scanner.nextLine();

            switch (command.toLowerCase()) {
                case "help":
                    printHelp();
                    break;
                case "quit":
                    isRunning = false;
                    break;
                case "login":
                    // Call the login function
                    handleLogin(scanner);
                    break;
                case "register":
                    // Implement register logic
                    break;
                default:
                    System.out.println(ANSI_RED + "Unknown command. Type 'help' for a list of commands." + ANSI_RESET);
            }
        }

        scanner.close();
    }

    private static void printHelp() {
        System.out.println(ANSI_YELLOW + "Available commands:" + ANSI_RESET);
        System.out.println("help - Show this help message");
        System.out.println("quit - Exit the program");
        System.out.println("login - Login to your account");
        System.out.println("register - Register a new account");
    }

    private static void handleLogin(Scanner scanner) {
        System.out.print(ANSI_GREEN + "Username: " + ANSI_RESET);
        String username = scanner.nextLine();
        System.out.print(ANSI_GREEN + "Password: " + ANSI_RESET);
        String password = scanner.nextLine();

        // Create LoginRequest object
        LoginRequest loginRequest = new LoginRequest(username, password);
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(loginRequest);

        // Send POST request to server
        try {
            URL url = new URL("http://localhost:8080/session");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // ... [Set up the connection and send the request]

            // Read the response
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }

            // Deserialize the response
            LoginResult loginResult = gson.fromJson(response.toString(), LoginResult.class);

            // Process the login result
            if (loginResult.getMessage() != null) {
                // Login failed
                System.out.println(ANSI_RED + loginResult.getMessage() + ANSI_RESET);
            } else {
                // Login successful
                System.out.println(ANSI_GREEN + "Login successful for user: " + loginResult.getUsername() + ANSI_RESET);
                // Transition to Postlogin UI
                // ... [Code to switch to Postlogin UI]
            }

        } catch (Exception e) {
            System.out.println(ANSI_RED + "Error: " + e.getMessage() + ANSI_RESET);
        }





    }

}
