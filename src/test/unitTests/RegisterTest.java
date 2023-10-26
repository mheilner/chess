package unitTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import services.RegisterService;
import requests.RegisterRequest;
import results.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterTest {

    @BeforeEach
    public void setUp() {
        // Clear any existing data and set up a test environment
        // Example: Clear the database if using persistent storage.
        // db.clear();
    }

    @Test
    @DisplayName("Test successful registration")
    public void testRegisterSuccess() {
        RegisterService registerService = new RegisterService();
        RegisterRequest request = new RegisterRequest("testuser", "testpassword", "testuser@email.com");
        RegisterResult result = registerService.register(request);

        assertNotNull(result.getAuthToken());
        assertEquals("testuser", result.getUsername());
        assertNull(result.getMessage());
    }

    @Test
    @DisplayName("Test registration failure due to existing username")
    public void testRegisterFailUsernameExists() {
        RegisterService registerService = new RegisterService();

        // Register a user first
        RegisterRequest request1 = new RegisterRequest("testuser1", "testpassword", "testuser1@email.com");
        registerService.register(request1);

        // Try to register again with the same username
        RegisterRequest request2 = new RegisterRequest("testuser1", "testpassword", "testuser1@email.com");
        RegisterResult result = registerService.register(request2);

        assertNull(result.getAuthToken());
        assertEquals("Username already exists.", result.getMessage());
    }
}