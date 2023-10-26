package unitTests;

import dataAccess.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import services.LogoutService;
import requests.RegisterRequest;
import results.RegisterResult;
import services.RegisterService;
import dataAccess.AuthTokenDao;

import static org.junit.jupiter.api.Assertions.*;

public class LogoutTest {

    private final String validAuthToken = "validToken";

    @BeforeEach
    public void setUp() {
        AuthTokenDao.getInstance().clear(); // Clear any existing data
        UserDao.getInstance().clear();

        // Optionally, setup a user and its authToken for testing
        // Example: Insert a user and its authToken into the database
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
    @DisplayName("Test successful logout")
    public void testLogoutSuccess() {
        String authToken = registerUserAndGetAuthToken("testUser", "password", "email");
        LogoutService logoutService = new LogoutService();
        assertTrue(logoutService.logout(authToken));
    }

    @Test
    @DisplayName("Test logout failure due to nonexistent authToken")
    public void testLogoutNonexistentToken() {
        LogoutService logoutService = new LogoutService();
        assertFalse(logoutService.logout("nonexistentToken"));
    }

    @Test
    @DisplayName("Test logout failure due to logging out twice with the same token")
    public void testLogoutTwice() {
        String authToken = registerUserAndGetAuthToken("testUser", "password", "email");
        LogoutService logoutService = new LogoutService();
        assertTrue(logoutService.logout(authToken));
        assertFalse(logoutService.logout(authToken));
    }
}
