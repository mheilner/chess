package unitTests.daoTests;

import dataAccess.AuthTokenDao;
import dataAccess.DataAccessException;
import model.AuthToken;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class AuthTokenDaoTest {

    private AuthTokenDao authTokenDao; // The DAO we're testing
    private AuthToken testToken; // A test token for use in the tests

    @BeforeEach
    public void setUp() throws DataAccessException {
        authTokenDao = AuthTokenDao.getInstance(); // Obtain the instance of the DAO
        testToken = new AuthToken("testToken", "testUser"); // Initialize a test token
        authTokenDao.insert(testToken); // Insert the test token into the DAO
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        authTokenDao.clear(); // Clear all auth tokens after each test to ensure a clean state
    }

    @Test
    @DisplayName("Positive: Insert and retrieve AuthToken")
    public void testInsertAuthToken() throws DataAccessException {
        // Attempt to find the user by their auth token
        String username = authTokenDao.findUserByToken(testToken.getAuthToken());
        // The username associated with the token should match the test token's username
        assertEquals(testToken.getUsername(), username);
    }

    @Test
    @DisplayName("Positive: Check token exists")
    public void testTokenExists() throws DataAccessException {
        // Check the existence of the test token
        assertTrue(authTokenDao.tokenExists(testToken.getAuthToken()));
    }

    @Test
    @DisplayName("Positive: Remove AuthToken")
    public void testRemoveAuthToken() throws DataAccessException {
        // Remove the test token
        authTokenDao.removeToken(testToken.getAuthToken());
        // The token should no longer exist
        assertFalse(authTokenDao.tokenExists(testToken.getAuthToken()));
    }

    @Test
    @DisplayName("Positive: Clear AuthTokens")
    public void testClearAuthTokens() throws DataAccessException {
        // Clear all auth tokens
        authTokenDao.clear();
        // After clearing, the test token should not exist
        assertFalse(authTokenDao.tokenExists(testToken.getAuthToken()));
    }

    @Test
    @DisplayName("Negative: Insert duplicate AuthToken")
    public void testInsertDuplicateAuthToken() {
        // Attempting to insert a duplicate token should throw a DataAccessException
        AuthToken fake = new AuthToken("fake", "freank");
        assertThrows(NullPointerException.class, () -> authTokenDao.insert(null));
    }

    @Test
    @DisplayName("Negative: Find non-existent user by token")
    public void testFindNonExistentUserByToken() throws DataAccessException {
        // Attempting to find a user by a non-existent token should return null
        assertNull(authTokenDao.findUserByToken("fakeToken"));
    }

    @Test
    @DisplayName("Negative: Check non-existent token")
    public void testNonExistentToken() throws DataAccessException {
        // Checking the existence of a non-existent token should return false
        assertFalse(authTokenDao.tokenExists("fakeToken"));
    }

    @Test
    @DisplayName("Negative: Remove non-existent AuthToken")
    public void testRemoveNonExistentAuthToken() throws DataAccessException {
        // Attempting to remove a non-existent token should not throw an exception
        assertDoesNotThrow(() -> authTokenDao.removeToken("fakeToken"));
    }

    @Test
    @DisplayName("Negative: Consistency check after clear")
    public void testConsistencyAfterClear() throws DataAccessException {
        // Clear all tokens
        authTokenDao.clear();
        // Attempting to remove a token after clearing should not throw an exception
        // because the token should not exist
        assertDoesNotThrow(() -> authTokenDao.removeToken(testToken.getAuthToken()));
    }
}
