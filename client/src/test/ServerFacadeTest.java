import dataAccess.AuthTokenDao;
import dataAccess.DataAccessException;
import dataAccess.GameDao;
import dataAccess.UserDao;
import model.Game;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import requests.RegisterRequest;
import results.RegisterResult;
import services.ClearService;
import services.RegisterService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServerFacadeTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUpStreams() throws DataAccessException {
        ClearService clearService = new ClearService();
        clearService.clear();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() throws DataAccessException {
        // Clear game data after each test
        GameDao.getInstance().clear();
        AuthTokenDao.getInstance().clear();
        UserDao.getInstance().clear();
        System.setOut(originalOut);
    }

    // Add this method to your LogoutTest class
    private String registerUserAndGetAuthToken(String user, String pw, String email) {
        // Use your registration service to register a user and get an auth token
        RegisterService registrationService = new RegisterService();
        RegisterRequest registerRequest = new RegisterRequest(user, pw, email);
        RegisterResult registerResult = registrationService.register(registerRequest);
        return registerResult.getAuthToken(); // Get the auth token from the result
    }
    @Test
    @DisplayName("Test successful registration")
    public void testHandleRegisterSuccess() {
        // Mock the Scanner to simulate user input
        String username = "newUser";
        String password = "password123";
        String email = "email@example.com";
        Scanner mockScanner = new Scanner(new ByteArrayInputStream((username + "\n" + password + "\n" + email).getBytes()));

        // Call the method under test
        Main.handleRegister(mockScanner);
        // Check if the console output contains the expected success message
        assertTrue(outContent.toString().contains("Registration successful for user: " + username));
    }

    @Test
    @DisplayName("Test registration failure")
    public void testHandleRegisterFailure() {


        // Mock the Scanner to simulate user input
        String username = "existingUser";
        String password = "password123";
        String email = "email@example.com";
        Scanner mockScanner = new Scanner(new ByteArrayInputStream((username + "\n" + password + "\n" + email).getBytes()));
        // Call the method under test
        Main.handleRegister(mockScanner);
        assertTrue(outContent.toString().contains("Registration successful for user: " + username));
        Scanner mockScanner2 = new Scanner(new ByteArrayInputStream((username + "\n" + password + "\n" + email).getBytes()));
        Main.handleRegister(mockScanner2);

        assertTrue(outContent.toString().contains("Error: already taken"));
    }

    @Test
    @DisplayName("Test successful login")
    public void testHandleLoginSuccess() {
        // Mock the Scanner to simulate user input
        String username = "validUser";
        String password = "validPassword";
        String email = "email@example.com";
        Scanner mockScanner = new Scanner(new ByteArrayInputStream((username + "\n" + password + "\n" + email).getBytes()));
        // Call the method under test
        Main.handleRegister(mockScanner);
        Scanner mockScanner2 = new Scanner(new ByteArrayInputStream((username + "\n" + password).getBytes()));

        Main.handleLogin(mockScanner2);
        assertTrue(outContent.toString().contains("Login successful for user: " + username));
    }

    @Test
    @DisplayName("Test login failure")
    public void testHandleLoginFailure() {
        String username = "invalidUser";
        String password = "wrongPassword";
        Scanner mockScanner = new Scanner(new ByteArrayInputStream((username + "\n" + password).getBytes()));

        Main.handleLogin(mockScanner);

        assertTrue(outContent.toString().contains("Login failed:"));
    }

    @Test
    @DisplayName("Test successful game creation")
    public void testCreateGameSuccess() {
        String gameName = "ChessGame1";
        String simulatedUserInput = gameName + "\n";
        ByteArrayInputStream testInput = new ByteArrayInputStream(simulatedUserInput.getBytes());
        Scanner mockScanner = new Scanner(testInput);
        String authToken = registerUserAndGetAuthToken("testUser", "password", "email");

        PostLogin postLogin = new PostLogin(mockScanner, authToken);
        postLogin.createGame();

        assertTrue(outContent.toString().contains("Game created successfully. Game ID:"));
    }

    @Test
    @DisplayName("Test game creation failure")
    public void testCreateGameFailure() {
        String gameName = "ChessGame1";
        String simulatedUserInput = gameName + "\n";
        ByteArrayInputStream testInput = new ByteArrayInputStream(simulatedUserInput.getBytes());
        Scanner mockScanner = new Scanner(testInput);
        String authToken = registerUserAndGetAuthToken("testUser", "password", "email");

        PostLogin postLogin = new PostLogin(mockScanner, authToken);
        postLogin.createGame();

        assertTrue(outContent.toString().contains("Game created successfully. Game ID:"));

        ByteArrayInputStream testInput2 = new ByteArrayInputStream(simulatedUserInput.getBytes());
        Scanner mockScanner2 = new Scanner(testInput2);
        String authToken2 = registerUserAndGetAuthToken("testUser", "password", "email");

        PostLogin postLogin2 = new PostLogin(mockScanner2, authToken2);
        postLogin2.createGame();

        assertTrue(outContent.toString().contains("Error"));
    }

    @Test
    @DisplayName("Test list games after game creation")
    public void testListGamesAfterGameCreation() {
        // Simulate user input for creating a game, listing games, and logging out
        String gameName = "ChessGame1";
        String simulatedUserInput = "2\n" + gameName + "\n1\n4\n"; // 2 to create game, 1 to list games, 4 to logout
        ByteArrayInputStream testInput = new ByteArrayInputStream(simulatedUserInput.getBytes());
        Scanner mockScanner = new Scanner(testInput);
        String authToken = registerUserAndGetAuthToken("testUser", "password", "email");

        PostLogin postLogin = new PostLogin(mockScanner, authToken);
        postLogin.displayMenu();

        // Check if the output contains the expected strings
        String output = outContent.toString();
        assertTrue(output.contains("Game created successfully. Game ID:"));
        assertTrue(output.contains("Available games:"));
        assertTrue(output.contains(gameName)); // Check if the created game name is listed
    }

    @Test
    @DisplayName("Test listing games failure")
    public void testListGamesFail() {
        // Simulate user input: '1' to list games, then '4' to exit the loop
        String simulatedUserInput = "1\n4\n";
        ByteArrayInputStream testInput = new ByteArrayInputStream(simulatedUserInput.getBytes());
        Scanner mockScanner = new Scanner(testInput);
        String authToken = registerUserAndGetAuthToken("testUser", "password", "email");

        PostLogin postLogin = new PostLogin(mockScanner, authToken);
        postLogin.listGames();

        assertTrue(outContent.toString().contains("No games available."));
    }

//    @Test
//    @DisplayName("Test successful game join")
//    public void testJoinGameSuccess() {
//        String gameName = "frock";
//        String gameID = "28"; // Assuming game ID is known
//        String playerColor = "WHITE";
//        String simulatedUserInput = "2\n" + gameName + "\n3\n" + gameID + "\n" + playerColor + "\n4\n";
//        ByteArrayInputStream testInput = new ByteArrayInputStream(simulatedUserInput.getBytes());
//        Scanner mockScanner = new Scanner(testInput);
//        String authToken = registerUserAndGetAuthToken("testUser", "password", "email");
//
//        PostLogin postLogin = new PostLogin(mockScanner, authToken);
//        postLogin.displayMenu();
//
//        String output = outContent.toString();
//        assertTrue(output.contains("Successfully joined game."));
//    }





}
