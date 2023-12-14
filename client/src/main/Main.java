import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;
import com.google.gson.Gson;
import requests.LoginRequest;
import requests.RegisterRequest;
import results.LoginResult;
import results.RegisterResult;
import serverFacade.PostLogin;

import static ui.EscapeSequences.*;

public class Main {
    // ANSI color code constants
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String BORDER = ANSI_BLUE + "----------------------------------------------" + ANSI_RESET;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;

        while (isRunning) {
            printMenuHeader("MARK HEILNER CHESS GAME LAUNCH MENU");
            System.out.print(ANSI_YELLOW + "Enter command: " + ANSI_RESET);
            String command = scanner.nextLine();

            switch (command.toLowerCase()) {
                case "help":
                    printHelp();
                    break;
                case "quit":
                    isRunning = false;
                    System.out.println(ANSI_GREEN + "\nThank you for playing! Goodbye." + ANSI_RESET);
                    break;
                case "login":
                    handleLogin(scanner);
                    break;
                case "register":
                    handleRegister(scanner);
                    break;
                default:
                    System.out.println(ANSI_RED + "\nUnknown command. Type 'help' for a list of commands.\n" + ANSI_RESET);
            }
        }
        scanner.close();
    }

    private static void printMenuHeader(String title) {
        clearScreen();
        System.out.println(BORDER);
        System.out.println(ANSI_BLUE + SET_TEXT_BOLD + title + ANSI_RESET);
        System.out.println(BORDER + "\n");
    }

    private static void clearScreen() {
        System.out.print(ERASE_SCREEN);
        System.out.flush();
    }

    public static void printHelp() {
        System.out.println(ANSI_YELLOW + "\nAvailable commands:\n" + ANSI_RESET);
        System.out.println(ANSI_GREEN + "help" + ANSI_RESET + " - Show this help message");
        System.out.println(ANSI_GREEN + "quit" + ANSI_RESET + " - Exit the program");
        System.out.println(ANSI_GREEN + "login" + ANSI_RESET + " - Login to your account");
        System.out.println(ANSI_GREEN + "register" + ANSI_RESET + " - Register a new account\n");
    }

    public static void handleRegister(Scanner scanner) {
        System.out.print(ANSI_GREEN + "Username: " + ANSI_RESET);
        String username = scanner.nextLine();
        System.out.print(ANSI_GREEN + "Password: " + ANSI_RESET);
        String password = scanner.nextLine();
        System.out.print(ANSI_GREEN + "Email: " + ANSI_RESET);
        String email = scanner.nextLine();

        // Create RegisterRequest object
        RegisterRequest registerRequest = new RegisterRequest(username, password, email);
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(registerRequest);

        try {
            URL url = new URL("http://localhost:8080/user");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonRequest.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            int responseCode = conn.getResponseCode();
            InputStream inputStream = responseCode == 200 ? conn.getInputStream() : conn.getErrorStream();

            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "utf-8"))) {
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }
            // Deserialize the response
            RegisterResult registerResult = gson.fromJson(response.toString(), RegisterResult.class);
            // Process the registration result
            if (registerResult.getMessage() != null) {
                // Registration failed
                System.out.println(ANSI_RED + "Registration failed: " + registerResult.getMessage() + ANSI_RESET);
            } else {
                // Registration successful
                System.out.println(ANSI_GREEN + "Registration successful for user: " + registerResult.getUsername() + ANSI_RESET);
                // Optionally, automatically log the user in here and transition to Postlogin UI
                // Store the authToken for future requests
            }

        } catch (Exception e) {
            System.out.println(ANSI_RED + "Error: " + e.getMessage() + ANSI_RESET);
        }
    }

    public static void handleLogin(Scanner scanner) {
        System.out.print(ANSI_GREEN + "Username: " + ANSI_RESET);
        String username = scanner.nextLine();
        System.out.print(ANSI_GREEN + "Password: " + ANSI_RESET);
        String password = scanner.nextLine();

        // Create LoginRequest object
        LoginRequest loginRequest = new LoginRequest(username, password);
        Gson gson = new Gson();
        String jsonRequest = gson.toJson(loginRequest);

        try {
            URL url = new URL("http://localhost:8080/session");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonRequest.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            int responseCode = conn.getResponseCode();
            InputStream inputStream = responseCode == 200 ? conn.getInputStream() : conn.getErrorStream();

            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "utf-8"))) {
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
                System.out.println(ANSI_RED + "Login failed: " + loginResult.getMessage() + ANSI_RESET);
            } else {
                // Login successful
                System.out.println(ANSI_GREEN + "Login successful for user: " + loginResult.getUsername() + ANSI_RESET);
                // Transition to Postlogin UI
                PostLogin postLogin = new PostLogin(scanner, loginResult.getAuthToken());
                postLogin.displayMenu();
            }

        } catch (Exception e) {
            System.out.println(ANSI_RED + "Error: " + e.getMessage() + ANSI_RESET);
        }
    }
}
