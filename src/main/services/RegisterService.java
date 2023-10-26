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
            // Check if user with given username already exists
            User existingUser = userDao.find(request.getUsername());
            if (existingUser != null) {
                return new RegisterResult("Username already exists.");
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
