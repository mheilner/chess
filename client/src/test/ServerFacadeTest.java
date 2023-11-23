import dataAccess.AuthTokenDao;
import dataAccess.DataAccessException;
import dataAccess.GameDao;
import dataAccess.UserDao;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import services.ClearService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServerFacadeTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUpStreams() throws DataAccessException {
        ClearService clearService = new ClearService();
        clearService.clear();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() throws DataAccessException {
        // Clear game data after each test
        GameDao.getInstance().clear();
        AuthTokenDao.getInstance().clear();
        UserDao.getInstance().clear();
        System.setOut(originalOut);
    }
    @Test
    @DisplayName("Test successful registration")
    public void testHandleRegisterSuccess() {
        // Mock the Scanner to simulate user input
        String username = "newUser";
        String password = "password123";
        String email = "email@example.com";
        Scanner mockScanner = new Scanner(new ByteArrayInputStream((username + "\n" + password + "\n" + email).getBytes()));

        // Call the method under test
        Main.handleRegister(mockScanner);
        // Check if the console output contains the expected success message
        assertTrue(outContent.toString().contains("Registration successful for user: " + username));
    }

    @Test
    @DisplayName("Test registration failure")
    public void testHandleRegisterFailure() {


        // Mock the Scanner to simulate user input
        String username = "existingUser";
        String password = "password123";
        String email = "email@example.com";
        Scanner mockScanner = new Scanner(new ByteArrayInputStream((username + "\n" + password + "\n" + email).getBytes()));
        // Call the method under test
        Main.handleRegister(mockScanner);
        assertTrue(outContent.toString().contains("Registration successful for user: " + username));
        Scanner mockScanner2 = new Scanner(new ByteArrayInputStream((username + "\n" + password + "\n" + email).getBytes()));
        Main.handleRegister(mockScanner2);

        assertTrue(outContent.toString().contains("Error: already taken"));
    }

    @Test
    @DisplayName("Test successful login")
    public void testHandleLoginSuccess() {
        // Mock the Scanner to simulate user input
        String username = "validUser";
        String password = "validPassword";
        String email = "email@example.com";
        Scanner mockScanner = new Scanner(new ByteArrayInputStream((username + "\n" + password + "\n" + email).getBytes()));
        // Call the method under test
        Main.handleRegister(mockScanner);
        Scanner mockScanner2 = new Scanner(new ByteArrayInputStream((username + "\n" + password).getBytes()));

        Main.handleLogin(mockScanner2);
        assertTrue(outContent.toString().contains("Login successful for user: " + username));
    }

    @Test
    @DisplayName("Test login failure")
    public void testHandleLoginFailure() {
        String username = "invalidUser";
        String password = "wrongPassword";
        Scanner mockScanner = new Scanner(new ByteArrayInputStream((username + "\n" + password).getBytes()));

        Main.handleLogin(mockScanner);

        assertTrue(outContent.toString().contains("Login failed:"));
    }

//    @Test
//    @DisplayName("Test listing games")
//    public void testListGames() {
//        // Assuming there's a way to simulate this from the Main class
//        PostLogin.listGames();
//
//        assertTrue(outContent.toString().contains("Available games:"));
//    }



}
