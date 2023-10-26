package services;

import dataAccess.AuthTokenDao;
import dataAccess.DataAccessException;
import dataAccess.GameDao;
import dataAccess.UserDao;

public class ClearService {

    /**
     * Clears all the data in the database.
     * @return True if the operation was successful, false otherwise.
     */
    public boolean clear() {
        UserDao userDao = new UserDao();
        GameDao gameDao = new GameDao();
        AuthTokenDao authTokenDao = new AuthTokenDao();

        userDao.clear();
        gameDao.clear();
        authTokenDao.clear();

        return true;
    }
}
