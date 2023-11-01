package services;

import dataAccess.AuthTokenDao;
import dataAccess.UserDao;
import dataAccess.DataAccessException;
import model.AuthToken;
import model.User;
import requests.RegisterRequest;
import results.RegisterResult;

import java.util.UUID;

public class RegisterService {
    private UserDao userDao = UserDao.getInstance();
    private AuthTokenDao authTokenDao = AuthTokenDao.getInstance();

    /**
     * The {@code RegisterService} class is responsible for handling the registration process.
     * It communicates with the {@code UserDao} and {@code AuthTokenDao} to perform user registration,
     * including user creation and authentication token generation.
     */
    public RegisterResult register(RegisterRequest request) {
        try {
            // Check if user with given username already exists
            User existingUser = userDao.find(request.getUsername());
            if (existingUser != null) {
                return new RegisterResult("Username already exists.");
            } else if (request.getPassword() == null) {
                return new RegisterResult("No password entered.");
            }
            // Register new user
            User newUser = new User(request.getUsername(), request.getPassword(), request.getEmail());
            userDao.insert(newUser);
            // Generate authToken and save it
            String authToken = UUID.randomUUID().toString();

            AuthToken token = new AuthToken(authToken, request.getUsername());
            authTokenDao.insert(token);
            return new RegisterResult(authToken, request.getUsername());
        } catch (DataAccessException e) {
            return new RegisterResult("Error while registering user: " + e.getMessage());
        }
    }
}
