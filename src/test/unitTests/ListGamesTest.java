//package unitTests;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import services.ListGamesService;
//import results.ListGamesResult;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class ListGamesTest {
//
//    @BeforeEach
//    public void setUp() {
//        // Clear any existing data and set up a test environment
//        // Example: Clear the database if using persistent storage.
//        // db.clear();
//    }
//
//    @Test
//    @DisplayName("Test successful games list retrieval")
//    public void testListGamesSuccess() {
//        ListGamesResult result = ListGamesService.listGames(validAuthToken);
//        if (result.getGames() == null || result.getGames().isEmpty()) {
//            assertEquals("No games available.", result.getMessage());
//        } else {
//            assertNotNull(result.getGames());
//            assertTrue(result.getGames().size() > 0);
//        }
//
//    }
//
//    @Test
//    @DisplayName("Test games list retrieval failure due to invalid authToken")
//    public void testListGamesFailInvalidAuthToken() {
//        ListGamesService listGamesService = new ListGamesService();
//        String invalidAuthToken = "invalidToken";
//        ListGamesResult result = listGamesService.listGames(invalidAuthToken);
//
//        assertEquals("Invalid authentication token.", result.getMessage());
//    }
//}
