package handler;

import com.google.gson.Gson;
import dataAccess.DataAccessException;
import services.ClearService;
import results.ClearResult;
import spark.Request;
import spark.Response;
import spark.Route;

public class ClearHandler implements Route {

    private static ClearHandler instance;

    private final Gson gson;
    private final ClearService clearService;

    private ClearHandler() {
        gson = new Gson();
        clearService = new ClearService();
    }

    public static ClearHandler getInstance() {
        if (instance == null) {
            instance = new ClearHandler();
        }
        return instance;
    }

    @Override
    public Object handle(Request request, Response response) throws DataAccessException {
        // Clear the database
        boolean success = clearService.clear();

        // Set the response type to JSON
        response.type("application/json");

        if (success) {
            response.status(200);
            return gson.toJson(new ClearResult("Database cleared successfully."));
        } else {
            response.status(500);
            return gson.toJson(new ClearResult("Error clearing the database."));
        }
    }
}
