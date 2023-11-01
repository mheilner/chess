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
    public boolean clear() throws DataAccessException {
        UserDao userDao = UserDao.getInstance();
        GameDao gameDao = GameDao.getInstance();
        AuthTokenDao authTokenDao = AuthTokenDao.getInstance();

        userDao.clear();
        gameDao.clear();
        authTokenDao.clear();

        return true;
    }
}
