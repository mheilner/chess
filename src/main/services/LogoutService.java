package services;

import dataAccess.AuthTokenDao;

public class LogoutService {

    private AuthTokenDao authTokenDao = AuthTokenDao.getInstance();

    public boolean logout(String authToken) {
        try {
            // Check if the authToken exists
            if (!authTokenDao.tokenExists(authToken)) {
                return false;
            }

            // Remove the authToken
            authTokenDao.removeToken(authToken);
            return true;
        } catch (Exception e) {
            // Log the error if needed
            return false;
        }
    }
}
