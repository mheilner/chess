package unitTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import services.CreateGameService;
import requests.CreateGameRequest;
import results.CreateGameResult;

import static org.junit.jupiter.api.Assertions.*;

public class CreateGameTest {

    @BeforeEach
    public void setUp() {
        // Clear any existing data and set up a test environment
        // Example: Clear the database if using persistent storage.
        // db.clear();
    }

    @Test
    @DisplayName("Test successful game creation")
    public void testCreateGameSuccess() {
        CreateGameService createGameService = new CreateGameService();
        CreateGameRequest request = new CreateGameRequest("ChessMatch1");
        CreateGameResult result = createGameService.createGame(request);

        assertNotEquals(0, result.getGameID());  // Assuming gameID starts from 1 and increments.
        assertNull(result.getMessage());
    }

    @Test
    @DisplayName("Test game creation failure due to existing game name")
    public void testCreateGameFailGameNameExists() {
        CreateGameService createGameService = new CreateGameService();

        // Create a game first
        CreateGameRequest request1 = new CreateGameRequest("ChessMatch2");
        createGameService.createGame(request1);

        // Try to create again with the same game name
        CreateGameRequest request2 = new CreateGameRequest("ChessMatch2");
        CreateGameResult result = createGameService.createGame(request2);

        assertEquals(0, result.getGameID());  // Assuming 0 indicates no game was created.
        assertEquals("A game with this name already exists. Please choose another name.", result.getMessage());
    }
}
