package unitTests.daoTests;

import chessPkg.CGame;
import dataAccess.AuthTokenDao;
import dataAccess.DataAccessException;
import dataAccess.GameDao;
import dataAccess.UserDao;
import model.Game;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import requests.CreateGameRequest;
import results.CreateGameResult;
import services.ClearService;
import services.CreateGameService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameDaoTest {

    @BeforeEach
    public void setUp() throws DataAccessException {
        // Clear any existing data and set up a test environment
        ClearService clearService = new ClearService();
        clearService.clear();
    }
    @AfterEach
    public void tearDown() throws DataAccessException {
        // Clear game data after each test
        GameDao.getInstance().clear();
        AuthTokenDao.getInstance().clear();
        UserDao.getInstance().clear();
    }
    @Test
    @DisplayName("Positive: Insert a game into the database")
    public void testInsertGamePositive() throws DataAccessException {
        GameDao gameDao = GameDao.getInstance();
        // Creating a game with unique gameID
        Game game = new Game(0, "ChessGame", "whitePlayer", "blackPlayer", null);
        int gameId = gameDao.insert(game);

        // The game should be inserted successfully and return a valid gameID
        assertNotEquals(0, gameId);
        // Now retrieve the game to confirm it was inserted
        Game retrievedGame = gameDao.find(gameId);
        assertNotNull(retrievedGame);
        assertEquals("ChessGame", retrievedGame.getGameName());
        assertEquals("whitePlayer", retrievedGame.getWhiteUsername());
        assertEquals("blackPlayer", retrievedGame.getBlackUsername());
    }
    @Test
    @DisplayName("Negative: Insert a game with a name that already exists using CreateGameService")
    public void testCreateGameServiceDuplicateName() throws DataAccessException {
        GameDao gameDao = GameDao.getInstance();
        CreateGameService createGameService = new CreateGameService();

        // Create a game first
        CreateGameRequest request1 = new CreateGameRequest("ChessMatch2");
        CreateGameResult result1 = createGameService.createGame(request1);

        // The first creation should be successful and return a valid gameID
        assertNotEquals(0, result1.getGameID());

        // Try to create another game with the same name, which should fail
        CreateGameRequest request2 = new CreateGameRequest("ChessMatch2");
        CreateGameResult result2 = createGameService.createGame(request2);

        // The second attempt should fail and result should contain an error message
        assertNull(result2.getGameID()); // Assuming null or 0 indicates no game was created.
        assertEquals("A game with this name already exists. Please choose another name.", result2.getMessage());
    }

    @Test
    @DisplayName("Positive: Find a game by ID")
    public void testFindGamePositive() throws DataAccessException {
        GameDao gameDao = GameDao.getInstance();
        // Assume a game has been inserted already
        Game game = new Game(0, "FindGameTest", "whitePlayer", "blackPlayer", new CGame());
        int gameId = gameDao.insert(game);

        // Retrieve the game by ID
        Game foundGame = gameDao.find(gameId);
        assertNotNull(foundGame);
        assertEquals(gameId, foundGame.getGameID());
        assertEquals("FindGameTest", foundGame.getGameName());
    }

    @Test
    @DisplayName("Negative: Find a game by non-existing ID")
    public void testFindGameNegative() throws DataAccessException {
        GameDao gameDao = GameDao.getInstance();
        // Try to find a game with an ID that does not exist
        Game foundGame = gameDao.find(Integer.MAX_VALUE);
        assertNull(foundGame); // Assuming that a non-existing game returns null
    }

    @Test
    @DisplayName("Positive: Find all games")
    public void testFindAllGamesPositive() throws DataAccessException {
        GameDao gameDao = GameDao.getInstance();
        // Insert multiple games
        gameDao.insert(new Game(0, "Game1", "white1", "black1", new CGame()));
        gameDao.insert(new Game(0, "Game2", "white2", "black2", new CGame()));

        // Retrieve all games
        List<Game> games = gameDao.findAll();
        assertNotNull(games);
        assertTrue(games.size() >= 2); // Check that at least two games were found
    }

    @Test
    @DisplayName("Negative: Retrieve all games when none exist")
    public void testFindAllGamesWhenNoneExist() throws DataAccessException {
        GameDao gameDao = GameDao.getInstance();
        // Attempt to retrieve all games
        List<Game> gamesList = gameDao.findAll();
        // Verify that the list is empty
        assertTrue(gamesList.isEmpty(), "The list of games should be empty when no games have been created.");
    }

    @Test
    @DisplayName("Positive: Clear all games")
    public void testClearGamesPositive() throws DataAccessException {
        GameDao gameDao = GameDao.getInstance();
        // Insert a game to ensure there's something to clear
        gameDao.insert(new Game(0, "ClearTest", "whiteClear", "blackClear", new CGame()));

        // Clear all games
        gameDao.clear();

        // Try to find all games, expecting an empty list
        List<Game> games = gameDao.findAll();
        assertTrue(games.isEmpty()); // Check that no games are found
    }

    @Test
    @DisplayName("Positive: Successfully claim a spot in a game")
    public void testClaimSpotPositive() throws DataAccessException {
        GameDao gameDao = GameDao.getInstance();

        // Insert a game into the database
        Game game = new Game(0, "NewGameForClaim", null, null, new CGame());
        int gameId = gameDao.insert(game);

        // Claim the white spot for a player
        gameDao.claimSpot(gameId, "whitePlayer", "WHITE");

        // Retrieve the game and check if the white spot is claimed
        Game updatedGame = gameDao.find(gameId);
        assertNotNull(updatedGame);
        assertEquals("whitePlayer", updatedGame.getWhiteUsername());

        // Now claim the black spot for another player
        gameDao.claimSpot(gameId, "blackPlayer", "BLACK");

        // Retrieve the game again and check if the black spot is claimed
        updatedGame = gameDao.find(gameId);
        assertNotNull(updatedGame);
        assertEquals("blackPlayer", updatedGame.getBlackUsername());
    }

    @Test
    @DisplayName("Negative: Claim a spot that's already taken")
    public void testClaimSpotNegative_SpotTaken() throws DataAccessException {
        GameDao gameDao = GameDao.getInstance();
        // Assume a game has been inserted and a spot claimed
        Game game = new Game(0, "ClaimSpotTest", "whitePlayer", "blackPlayer", new CGame());
        int gameId = gameDao.insert(game);

        // Try to claim the same spot again, which should fail
        Exception exception = assertThrows(DataAccessException.class, () -> gameDao.claimSpot(gameId, "anotherPlayer", "WHITE"));
        String expectedMessage = "White spot is already taken.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    @DisplayName("Positive: Check for an existing game name")
    public void testGameNameExistsPositive() throws DataAccessException {
        GameDao gameDao = GameDao.getInstance();
        // Insert a game to ensure the game name exists in the database
        Game game = new Game(0, "ExistingGame", "whitePlayer", "blackPlayer", new CGame());
        gameDao.insert(game);

        // Check if the game name exists
        boolean exists = gameDao.gameNameExists("ExistingGame");
        assertTrue(exists); // Expecting true because the game name should exist
    }

    @Test
    @DisplayName("Negative: Check for a non-existing game name")
    public void testGameNameExistsNegative() throws DataAccessException {
        GameDao gameDao = GameDao.getInstance();
        // Check for a game name that doesn't exist
        boolean exists = gameDao.gameNameExists("NonExistingGame");
        assertFalse(exists); // Expecting false because the game name should not exist
    }

    @Test
    @DisplayName("Positive: Get white player for an existing game ID")
    public void testGetWhitePlayerPositive() throws DataAccessException {
        GameDao gameDao = GameDao.getInstance();
        // Insert a game and claim the white spot
        Game game = new Game(0, "WhitePlayerGame", "whitePlayer", null, new CGame());
        int gameId = gameDao.insert(game);

        // Get the white player's username for the game ID
        String whitePlayer = gameDao.getWhitePlayer(gameId);
        assertEquals("whitePlayer", whitePlayer);
    }

    @Test
    @DisplayName("Positive: Get black player for an existing game ID")
    public void testGetBlackPlayerPositive() throws DataAccessException {
        GameDao gameDao = GameDao.getInstance();
        // Insert a game and claim the black spot
        Game game = new Game(0, "BlackPlayerGame", null, "blackPlayer", new CGame());
        int gameId = gameDao.insert(game);

        // Get the black player's username for the game ID
        String blackPlayer = gameDao.getBlackPlayer(gameId);
        assertEquals("blackPlayer", blackPlayer);
    }

    @Test
    @DisplayName("Negative: Get white player for a non-existing game ID")
    public void testGetWhitePlayerNegative_NonExistingGameID() {
        GameDao gameDao = GameDao.getInstance();
        // Try to get a white player for a game ID that does not exist
        Exception exception = assertThrows(DataAccessException.class, () -> gameDao.getWhitePlayer(Integer.MAX_VALUE));
        String expectedMessage = "Game with ID " + Integer.MAX_VALUE + " not found.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    @DisplayName("Negative: Get black player for a non-existing game ID")
    public void testGetBlackPlayerNegative_NonExistingGameID() {
        GameDao gameDao = GameDao.getInstance();
        // Try to get a black player for a game ID that does not exist
        Exception exception = assertThrows(DataAccessException.class, () -> gameDao.getBlackPlayer(Integer.MAX_VALUE));
        String expectedMessage = "Game with ID " + Integer.MAX_VALUE + " not found.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

}
