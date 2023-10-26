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

    private UserDao userDao = new UserDao();
    private AuthTokenDao authTokenDao = new AuthTokenDao();

    public RegisterResult register(RegisterRequest request) {
        try {
            System.out.println("Starting registration for user: " + request.getUsername());

            // Check if user with given username already exists
            User existingUser = userDao.find(request.getUsername());
            if (existingUser != null) {
                System.out.println("User with username " + request.getUsername() + " already exists.");
                return new RegisterResult("Username already exists.");
            }
            System.out.println("No existing user found with username " + request.getUsername() + ". Proceeding with registration.");


            // Register new user
            User newUser = new User(request.getUsername(), request.getPassword(), request.getEmail());
            userDao.insert(newUser);
            System.out.println("Inserted new user with username: " + newUser.getUsername());

            // Generate authToken and save it
            String authToken = UUID.randomUUID().toString();
            System.out.println("Generated new authToken: " + authToken);

            AuthToken token = new AuthToken(authToken, request.getUsername());
            authTokenDao.insert(token);
            System.out.println("Inserted new authToken for user: " + request.getUsername());
            System.out.println(authTokenDao.findUserByToken(token.getAuthToken()));

            return new RegisterResult(authToken, request.getUsername());
        } catch (DataAccessException e) {
            return new RegisterResult("Error while registering user: " + e.getMessage());
        }
    }
}
