package unitTests;

import dataAccess.AuthTokenDao;
import dataAccess.DataAccessException;
import dataAccess.UserDao;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import services.JoinGameService;
import requests.JoinGameRequest;
import requests.RegisterRequest;
import results.JoinGameResult;
import results.RegisterResult;
import dataAccess.GameDao;
import model.Game;
import services.RegisterService;


import static org.junit.jupiter.api.Assertions.*;

public class JoinGameTest {

    private final String validAuthToken = "validToken";
    private final String invalidAuthToken = "invalidToken";

    @BeforeEach
    public void setUp() throws DataAccessException {
        GameDao.getInstance().clear(); // Clear any existing data

        // Setup a test game for use in the tests
        Game game = new Game(1,  null, null, "Test Game",null);
        game.setGameID(1); // Set a valid game ID for testing
        GameDao.getInstance().insert(game);

    }
    @AfterEach
    public void tearDown() throws DataAccessException {
        // Clear game data after each test
        GameDao.getInstance().clear();
        AuthTokenDao.getInstance().clear();
        UserDao.getInstance().clear();

        // Optionally, clear other DAOs if they are being modified by the tests
        // Example: UserDao.getInstance().clear();
        // Example: AuthTokenDao.getInstance().clear();
    }

    // Add this method to your JoinGameTest class
    private String registerUserAndGetAuthToken(String user, String pw, String email) {
        // Use your registration service to register a user and get an auth token
        // Replace the following lines with your actual registration code
        RegisterService registrationService = new RegisterService();
        RegisterRequest registerRequest = new RegisterRequest(user, pw, email);
        RegisterResult registerResult = registrationService.register(registerRequest);
        return registerResult.getAuthToken(); // Get the auth token from the result
    }

    @Test
    @DisplayName("Test successful game join")
    public void testJoinGameSuccess() {
        JoinGameService joinGameService = new JoinGameService();
        JoinGameRequest request = new JoinGameRequest("WHITE", 1);

        // Register a user and get a valid auth token
        String validAuthToken = registerUserAndGetAuthToken("testUser", "password", "email");

        JoinGameResult result = joinGameService.joinGame(request, validAuthToken);
        assertNull(result.getMessage());  // No error message indicates success.
    }

    @Test
    @DisplayName("Test game join failure due to invalid authToken")
    public void testJoinGameFailInvalidAuthToken() {
        JoinGameService joinGameService = new JoinGameService();
        JoinGameRequest request = new JoinGameRequest("WHITE", 1);

        JoinGameResult result = joinGameService.joinGame(request, invalidAuthToken);
        assertEquals("Error: Invalid authentication token.", result.getMessage());
    }

    @Test
    @DisplayName("Test game join failure due to spot already taken")
    public void testJoinGameFailSpotTaken() {
        JoinGameService joinGameService = new JoinGameService();

        // Register two users and get valid auth tokens
        String firstAuthToken = registerUserAndGetAuthToken("testUser", "password", "email");
        String secondAuthToken = registerUserAndGetAuthToken("testUser2", "password", "email");

        // First player joins the WHITE spot
        JoinGameRequest firstRequest = new JoinGameRequest("WHITE", 1);
        joinGameService.joinGame(firstRequest, firstAuthToken);

        // Second player tries to join the already taken WHITE spot
        JoinGameRequest secondRequest = new JoinGameRequest("WHITE", 1);
        JoinGameResult result = joinGameService.joinGame(secondRequest, secondAuthToken);

        assertEquals("Error: White spot is already taken.", result.getMessage());
    }
}
