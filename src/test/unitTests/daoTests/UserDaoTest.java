package unitTests.daoTests;

import dataAccess.DataAccessException;
import dataAccess.UserDao;
import model.User;
import org.junit.jupiter.api.*;
import requests.RegisterRequest;
import results.RegisterResult;
import services.RegisterService;

import static org.junit.jupiter.api.Assertions.*;

public class UserDaoTest {

    private UserDao userDao; // The DAO we're testing
    private User testUser; // A test user for use in the tests

    @BeforeEach
    public void setUp() throws DataAccessException {
        userDao = UserDao.getInstance(); // Obtain the instance of the DAO
        testUser = new User("testUser", "testPassword", "testEmail"); // Initialize a test user
        userDao.insert(testUser); // Insert the test user into the DAO
    }

    @AfterEach
    public void tearDown() throws DataAccessException {
        userDao.clear(); // Clear all users after each test to ensure a clean state
    }

    @Test
    @DisplayName("Positive: Insert and retrieve User")
    public void testInsertUser() throws DataAccessException {
        // Attempt to find the user by their username
        User foundUser = userDao.find(testUser.getUsername());
        // The found user should match the test user
        assertEquals(testUser.getUsername(), foundUser.getUsername());
        assertEquals(testUser.getPassword(), foundUser.getPassword());
        assertEquals(testUser.getEmail(), foundUser.getEmail());
    }

    @Test
    @DisplayName("Positive: Clear Users")
    public void testClearUsers() throws DataAccessException {
        // Clear all users
        userDao.clear();
        // After clearing, the test user should not be found
        assertNull(userDao.find(testUser.getUsername()));
    }

    @Test
    @DisplayName("Negative: Insert a user with a username that already exists using RegisterService")
    public void testRegisterServiceDuplicateUsername() throws DataAccessException {
        UserDao userDao = UserDao.getInstance();
        userDao.clear();
        RegisterService registerService = new RegisterService();

        // Create a user first
        RegisterRequest request1 = new RegisterRequest("testUser", "testPassword", "testEmail");
        RegisterResult result1 = registerService.register(request1);

        // The first registration should be successful
        assertNull(result1.getMessage());

        // Try to register another user with the same username, which should fail
        RegisterRequest request2 = new RegisterRequest("testUser", "newPassword", "newEmail");
        RegisterResult result2 = registerService.register(request2);

        // The second attempt should fail and result should contain the specific error message
        assertEquals("Username already exists.", result2.getMessage());
    }



    @Test
    @DisplayName("Negative: Find non-existent User")
    public void testFindNonExistentUser() throws DataAccessException {
        // Attempting to find a non-existent user should return null
        assertNull(userDao.find("fakeUser"));
    }

    @Test
    @DisplayName("Negative: Consistency check after clear")
    public void testConsistencyAfterClear() throws DataAccessException {
        // Clear all users
        userDao.clear();
        // Attempting to find a user after clearing should return null
        assertNull(userDao.find(testUser.getUsername()));
    }
}
