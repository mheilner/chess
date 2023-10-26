package services;

import dataAccess.AuthTokenDao;
import dataAccess.UserDao;
import model.AuthToken;
import model.User;
import requests.LoginRequest;
import results.LoginResult;

import java.util.UUID;

public class LoginService {

    private UserDao userDao = UserDao.getInstance();
    private AuthTokenDao authTokenDao = AuthTokenDao.getInstance();


    public LoginResult login(LoginRequest request) {
        try {
            System.out.println("Starting login for user: " + request.getUsername());

            User user = userDao.find(request.getUsername());

            if (user == null) {
                System.out.println("No user found with username: " + request.getUsername());
                return new LoginResult("Error: Invalid username or password.");
            } else {
                System.out.println("User found with username: " + user.getUsername());
            }

            if (!user.getPassword().equals(request.getPassword())) {
                System.out.println("Provided password does not match stored password for user: " + request.getUsername());
                return new LoginResult("Error: Invalid username or password.");
            } else {
                System.out.println("Password matches for user: " + request.getUsername());
            }

            // Generate a new authentication token
            String authTokenString = UUID.randomUUID().toString();
            System.out.println("Generated new authToken: " + authTokenString);
            AuthToken authToken = new AuthToken(authTokenString, request.getUsername());

            // Store the new authentication token
            authTokenDao.insert(authToken);
            System.out.println("Inserted new authToken for user: " + request.getUsername());

            return new LoginResult(authTokenString, request.getUsername());
        } catch (Exception e) {
            // Log the error
            System.out.println("Error during login: " + e.getMessage());
            return new LoginResult(e.getMessage());
        }
    }
}
