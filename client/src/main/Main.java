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
            System.out.println(ANSI_BLUE + "WELCOME TO THE CHESS GAME LAUNCH MENU" + ANSI_RESET);
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
                    handleRegister(scanner);
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


    private static void handleRegister(Scanner scanner) {
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

    private static void handleLogin(Scanner scanner) {
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
