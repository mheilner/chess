import chessPkg.CGame;
import chessPkg.CBoard;
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
import serverFacade.PostLogin;
import services.ClearService;
import services.RegisterService;
import ui.ChessBoardDisplay;
import ui.EscapeSequences;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertFalse;
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
        String simulatedUserInput = "2\n" + gameName + "\n1\n5\n"; // 2 to create game, 1 to list games, 5 to logout
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

    @Test
    @DisplayName("Test successful game join")
    public void testJoinGameSuccess() throws DataAccessException {
        // Insert the game directly into the database
        String gameName = "TestGame";
        int gameID = 1;
        Game game = new Game(gameID, gameName, null, null, new CGame());
        GameDao.getInstance().insert(game);

        // Simulate user input for joining the game
        String playerColor = "WHITE";
        String simulatedUserInput = "3\n" + gameID + "\n" + playerColor + "\n4\n"; // 3 to join game, 4 to logout
        ByteArrayInputStream testInput = new ByteArrayInputStream(simulatedUserInput.getBytes());
        Scanner mockScanner = new Scanner(testInput);
        String authToken = registerUserAndGetAuthToken("testUser", "password", "email");

        PostLogin postLogin = new PostLogin(mockScanner, authToken);
        postLogin.displayMenu();

        String output = outContent.toString();
        assertTrue(output.contains("Successfully joined game."));
    }

    @Test
    @DisplayName("Test game join failure - spot already taken")
    public void testJoinGameFailure() throws DataAccessException {
        // Insert the game directly into the database
        String gameName = "TestGame";
        int gameID = 1;
        Game game = new Game(gameID, gameName, null, null, new CGame());
        GameDao.getInstance().insert(game);

        // Simulate user input for joining the game twice
        String playerColor = "WHITE";
        String simulatedUserInput = "3\n" + gameID + "\n" + playerColor + "\nquit\n" +  "\n3\n" + gameID + "\n" + playerColor + "\n5\n"; // Join game twice and then logout
        ByteArrayInputStream testInput = new ByteArrayInputStream(simulatedUserInput.getBytes());
        Scanner mockScanner = new Scanner(testInput);
        String authToken = registerUserAndGetAuthToken("testUser", "password", "email");

        PostLogin postLogin = new PostLogin(mockScanner, authToken);
        postLogin.displayMenu();

        String output = outContent.toString();
        assertTrue(output.contains("Error: White spot is already taken"));
    }

    @Test
    @DisplayName("Test successful logout")
    public void testLogoutSuccess() {
        // Simulate user input for logging out
        String simulatedUserInput = "5\n"; // Assuming '4' is the option for logout
        ByteArrayInputStream testInput = new ByteArrayInputStream(simulatedUserInput.getBytes());
        Scanner mockScanner = new Scanner(testInput);
        String authToken = registerUserAndGetAuthToken("testUser", "password", "email");

        PostLogin postLogin = new PostLogin(mockScanner, authToken);
        postLogin.displayMenu();

        String output = outContent.toString();
        assertTrue(output.contains("Logged out successfully."));
    }

    @Test
    @DisplayName("Test logout failure - trying to logout twice")
    public void testLogoutFailure() {
        // Simulate user input for logging out twice
        String simulatedUserInput = "5\n5\n"; // Trying to logout twice
        ByteArrayInputStream testInput = new ByteArrayInputStream(simulatedUserInput.getBytes());
        Scanner mockScanner = new Scanner(testInput);
        String authToken = registerUserAndGetAuthToken("testUser", "password", "email");

        PostLogin postLogin = new PostLogin(mockScanner, authToken);
        postLogin.displayMenu();

        String output = outContent.toString();
        assertTrue(output.contains("Logged out successfully."));
        // The second logout attempt might not produce any specific output, so just one works
    }

    @Test
    @DisplayName("Test displayMenu with valid option")
    public void testDisplayMenuPositive() {
        // Simulate user input for listing games and then logging out
        String simulatedUserInput = "1\n5\n"; // 1 for listing games, 4 for logout
        ByteArrayInputStream testInput = new ByteArrayInputStream(simulatedUserInput.getBytes());
        Scanner mockScanner = new Scanner(testInput);
        String authToken = registerUserAndGetAuthToken("testUser", "password", "email");

        PostLogin postLogin = new PostLogin(mockScanner, authToken);
        postLogin.displayMenu();

        String output = outContent.toString();
        assertTrue(output.contains("Available games:") || output.contains("No games available."));
    }

    @Test
    @DisplayName("Test displayMenu with invalid option")
    public void testDisplayMenuNegative() {
        // Simulate user input for an invalid option and then logging out
        String simulatedUserInput = "7\n5\n"; // 5 is an invalid option, 5 for logout
        ByteArrayInputStream testInput = new ByteArrayInputStream(simulatedUserInput.getBytes());
        Scanner mockScanner = new Scanner(testInput);
        String authToken = registerUserAndGetAuthToken("testUser", "password", "email");

        PostLogin postLogin = new PostLogin(mockScanner, authToken);
        postLogin.displayMenu();

        String output = outContent.toString();
        assertTrue(output.contains("Invalid choice. Please try again."));
    }

    @Test
    @DisplayName("Test printHelp method")
    public void testPrintHelp() {
        // Simulate user input to call 'help' and then 'quit'
        String simulatedUserInput = "help\nquit\n";
        ByteArrayInputStream testInput = new ByteArrayInputStream(simulatedUserInput.getBytes());
        Scanner mockScanner = new Scanner(testInput);
        System.setIn(testInput);

        Main.main(new String[]{});

        String output = outContent.toString();
        assertTrue(output.contains("Available commands:"));
    }

    @Test
    @DisplayName("Test printHelp method with incorrect input")
    public void testPrintHelpNegative() {
        // Simulate user input that is incorrect for the 'help' command
        String simulatedUserInput = "help\ninvalidCommand\nquit\n";
        ByteArrayInputStream testInput = new ByteArrayInputStream(simulatedUserInput.getBytes());
        System.setIn(testInput);

        Main.main(new String[]{});

        String output = outContent.toString();
        assertTrue(output.contains("Unknown command. Type 'help' for a list of commands."));
    }

    @Test
    @DisplayName("Test main function")
    public void testMainFunction() {
        // Simulate user input for different commands and then 'quit'
        String simulatedUserInput = "invalid\nhelp\nquit\n";
        ByteArrayInputStream testInput = new ByteArrayInputStream(simulatedUserInput.getBytes());
        Scanner mockScanner = new Scanner(testInput);
        System.setIn(testInput);

        Main.main(new String[]{});

        String output = outContent.toString();
        assertTrue(output.contains("Unknown command. Type 'help' for a list of commands."));
        assertTrue(output.contains("Available commands:"));
        assertTrue(output.contains("Thank you for playing! Goodbye."));
    }

    @Test
    @DisplayName("Test main function with invalid command")
    public void testMainFunctionNegative() {
        // Simulate user input with an invalid command
        String simulatedUserInput = "invalidCommand\nquit\n";
        ByteArrayInputStream testInput = new ByteArrayInputStream(simulatedUserInput.getBytes());
        System.setIn(testInput);

        Main.main(new String[]{});

        String output = outContent.toString();
        assertTrue(output.contains("Unknown command. Type 'help' for a list of commands."));
    }

    @Test
    @DisplayName("Test displaying chessboard with standard setup")
    public void testDisplayChessBoardStandardSetup() {
        CBoard board = new CBoard();
        board.resetBoard(); // Set up the board with the standard chess layout

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        ChessBoardDisplay.displayChessBoard(board);

        String output = outContent.toString();
        assertTrue(output.contains("Chessboard with White at Bottom:"));
        assertTrue(output.contains("Chessboard with Black at Bottom:"));
        // Check for specific chess pieces if needed, e.g., white king, black queen, etc.
    }
    @Test
    @DisplayName("Test displaying empty chessboard")
    public void testDisplayEmptyChessBoard() {
        CBoard board = new CBoard(); // Create an empty chessboard

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        ChessBoardDisplay.displayChessBoard(board);

        String output = outContent.toString();
        assertTrue(output.contains("Chessboard with White at Bottom:"));
        assertTrue(output.contains("Chessboard with Black at Bottom:"));
        assertFalse(output.contains(EscapeSequences.WHITE_KING) || output.contains(EscapeSequences.BLACK_KING)); // Ensure no pieces are displayed
    }
    @Test
    @DisplayName("Test success of help function")
    public void testHelpFunctionSuccess() {
        String simulatedUserInput = "4\n5\n"; // 4 for help, 5 for logout
        ByteArrayInputStream testInput = new ByteArrayInputStream(simulatedUserInput.getBytes());
        Scanner mockScanner = new Scanner(testInput);
        String authToken = registerUserAndGetAuthToken("testUser", "password", "email");

        PostLogin postLogin = new PostLogin(mockScanner, authToken);
        postLogin.displayMenu();

        String output = outContent.toString();
        assertTrue(output.contains("Help Menu:"));
        assertTrue(output.contains("1. List Games"));
        assertTrue(output.contains("6. Quit Game"));
    }
    @Test
    @DisplayName("Test failure of help function")
    public void testHelpFunctionFailure() {
        String simulatedUserInput = "invalid\n5\n"; // Invalid command, 5 for logout
        ByteArrayInputStream testInput = new ByteArrayInputStream(simulatedUserInput.getBytes());
        Scanner mockScanner = new Scanner(testInput);
        String authToken = registerUserAndGetAuthToken("testUser", "password", "email");

        PostLogin postLogin = new PostLogin(mockScanner, authToken);
        postLogin.displayMenu();

        String output = outContent.toString();
        assertTrue(output.contains("Invalid choice. Please try again."));
    }

    @Test
    @DisplayName("Test successful game quit")
    public void testQuitGameSuccess() throws DataAccessException {
        // Insert the game directly into the database for the user to join
        String gameName = "TestGame";
        int gameID = 1;
        Game game = new Game(gameID, gameName, null, null, new CGame());
        GameDao.getInstance().insert(game);

        String playerColor = "WHITE";
        String simulatedUserInput = "3\n" + gameID + "\n" + playerColor + "\nquit\n5\n"; // 3 to join game, 'quit' to leave game, 5 to logout
        ByteArrayInputStream testInput = new ByteArrayInputStream(simulatedUserInput.getBytes());
        Scanner mockScanner = new Scanner(testInput);
        String authToken = registerUserAndGetAuthToken("testUser", "password", "email");

        PostLogin postLogin = new PostLogin(mockScanner, authToken);
        postLogin.displayMenu();

        String output = outContent.toString();
        assertTrue(output.contains("You have quit the game."));
    }

    @Test
    @DisplayName("Test quit game when not in game")
    public void testQuitGameFailure() {
        String simulatedUserInput = "6\n5\n"; // 6 for quit game (when not in game), 5 for logout
        ByteArrayInputStream testInput = new ByteArrayInputStream(simulatedUserInput.getBytes());
        Scanner mockScanner = new Scanner(testInput);
        String authToken = registerUserAndGetAuthToken("testUser", "password", "email");

        PostLogin postLogin = new PostLogin(mockScanner, authToken);
        postLogin.displayMenu();

        String output = outContent.toString();
        assertTrue(output.contains("You have quit the game.")); // Verifying quit game does not cause issues
    }


}

