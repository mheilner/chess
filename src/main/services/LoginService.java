package services;

import dataAccess.AuthTokenDao;
import dataAccess.UserDao;
import model.AuthToken;
import model.User;
import requests.LoginRequest;
import results.LoginResult;

import java.util.UUID;

public class LoginService {

    private UserDao userDao = new UserDao();
    private AuthTokenDao authTokenDao = new AuthTokenDao();

    public LoginResult login(LoginRequest request) {
        try {
            User user = userDao.find(request.getUsername());

            if (user == null || !user.getPassword().equals(request.getPassword())) {
                return new LoginResult("Invalid username or password.");
            }

            // Generate a new authentication token
            String authTokenString = UUID.randomUUID().toString();
            AuthToken authToken = new AuthToken(authTokenString, request.getUsername());

            // Store the new authentication token
            authTokenDao.insert(authToken);

            return new LoginResult(authTokenString, request.getUsername());
        } catch (Exception e) {
            // Log the error if needed
            return new LoginResult(e.getMessage());
        }
    }
}
