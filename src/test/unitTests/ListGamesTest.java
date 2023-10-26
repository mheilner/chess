package unitTests;

import dataAccess.GameDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import requests.RegisterRequest;
import results.RegisterResult;
import services.ListGamesService;
import results.ListGamesResult;
import services.RegisterService;

import static org.junit.jupiter.api.Assertions.*;

public class ListGamesTest {

    @BeforeEach
    public void setUp() {
        GameDao.getInstance().clear(); // Clear any existing data

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
    @DisplayName("Test successful games list retrieval")
    public void testListGamesSuccess() {
        String validAuthToken = registerUserAndGetAuthToken("testUser", "password", "email");

        ListGamesService listGamesService = new ListGamesService();
        ListGamesResult result = listGamesService.listGames(validAuthToken);
        if (result.getGames() == null || result.getGames().isEmpty()) {
            assertEquals("No games available.", result.getMessage());
        } else {
            assertNotNull(result.getGames());
            assertTrue(result.getGames().size() > 0);
        }

    }

    @Test
    @DisplayName("Test games list retrieval failure due to invalid authToken")
    public void testListGamesFailInvalidAuthToken() {
        ListGamesService listGamesService = new ListGamesService();
        String invalidAuthToken = "invalidToken";
        ListGamesResult result = listGamesService.listGames(invalidAuthToken);

        assertEquals("Invalid authentication token.", result.getMessage());
    }
}
