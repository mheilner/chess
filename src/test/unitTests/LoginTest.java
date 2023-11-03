package unitTests;

import dataAccess.AuthTokenDao;
import dataAccess.DataAccessException;
import dataAccess.GameDao;
import dataAccess.UserDao;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import services.LoginService;
import services.RegisterService;
import requests.LoginRequest;
import requests.RegisterRequest;
import results.LoginResult;

import static org.junit.jupiter.api.Assertions.*;

public class LoginTest {

    @BeforeEach
    public void setUp() {
        // Clear any existing data and set up a test environment
        // Example: Clear the database if using persistent storage.
        // db.clear();
    }
    @AfterEach
    public void tearDown() throws DataAccessException {
        // Clear game data after each test
        GameDao.getInstance().clear();
        AuthTokenDao.getInstance().clear();
        UserDao.getInstance().clear();
    }

    @Test
    @DisplayName("Test successful login")
    public void testLoginSuccess() {
        // Register a user first to ensure we have a user to login with
        RegisterService registerService = new RegisterService();
        RegisterRequest registerRequest = new RegisterRequest("testuser", "testpassword", "testuser@email.com");
        registerService.register(registerRequest);

        // Now try to login with that user
        LoginService loginService = new LoginService();
        LoginRequest request = new LoginRequest("testuser", "testpassword");
        LoginResult result = loginService.login(request);

        assertNotNull(result.getAuthToken());
        assertEquals("testuser", result.getUsername());
        assertNull(result.getMessage());
    }

    @Test
    @DisplayName("Test login failure due to invalid username")
    public void testLoginFailInvalidUsername() {
        LoginService loginService = new LoginService();
        LoginRequest request = new LoginRequest("nonexistentuser", "password");
        LoginResult result = loginService.login(request);

        assertNull(result.getAuthToken());
        assertEquals("Error: Invalid username or password.", result.getMessage());
    }

    @Test
    @DisplayName("Test login failure due to invalid password")
    public void testLoginFailInvalidPassword() {
        // Register a user first to ensure we have a user to try an incorrect password with
        RegisterService registerService = new RegisterService();
        RegisterRequest registerRequest = new RegisterRequest("testuser2", "testpassword", "testuser2@email.com");
        registerService.register(registerRequest);

        // Now try to login with an incorrect password
        LoginService loginService = new LoginService();
        LoginRequest request = new LoginRequest("testuser2", "wrongpassword");
        LoginResult result = loginService.login(request);

        assertNull(result.getAuthToken());
        assertEquals("Error: Invalid username or password.", result.getMessage());
    }
}
