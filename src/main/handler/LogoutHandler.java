package handler;

import com.google.gson.Gson;
import requests.LogoutRequest;
import results.LogoutResult;
import services.LogoutService;
import spark.Request;
import spark.Response;
import spark.Route;

public class LogoutHandler implements Route {

    private static LogoutHandler instance;

    private final Gson gson;
    private final LogoutService logoutService;

    private LogoutHandler() {
        gson = new Gson();
        logoutService = new LogoutService();
    }

    public static LogoutHandler getInstance() {
        if (instance == null) {
            instance = new LogoutHandler();
        }
        return instance;
    }

    @Override
    public Object handle(Request request, Response response) {
        // Extract authToken from headers
        String authToken = request.headers("authorization");

        // If authToken is not provided, return an error
        if (authToken == null || authToken.isEmpty()) {
            response.status(401);
            return gson.toJson(new LogoutResult("Error: unauthorized"));
        }

        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        boolean success = logoutService.logout(logoutRequest.getAuthToken());

        // Set the response type to JSON
        response.type("application/json");

        if (success) {
            response.status(200);
            return gson.toJson(new LogoutResult(true));
        } else {
            response.status(401);
            return gson.toJson(new LogoutResult("Error: unauthorized"));
        }
    }
}
