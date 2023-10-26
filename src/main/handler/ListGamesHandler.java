package handler;

import com.google.gson.Gson;
import results.ListGamesResult;
import services.ListGamesService;
import spark.Request;
import spark.Response;
import spark.Route;

public class ListGamesHandler implements Route {

    private static ListGamesHandler instance;

    private final Gson gson;
    private final ListGamesService listGamesService;

    private ListGamesHandler() {
        gson = new Gson();
        listGamesService = new ListGamesService();
    }

    public static ListGamesHandler getInstance() {
        if (instance == null) {
            instance = new ListGamesHandler();
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
            return gson.toJson(new ListGamesResult(null, "Error: unauthorized"));
        }

        ListGamesResult result = listGamesService.listGames(authToken);

        // Set the response type to JSON
        response.type("application/json");

        if (result.getMessage() == null) {
            response.status(200);
        } else if ("Invalid authentication token.".equals(result.getMessage())) {
            response.status(401);
        } else {
            response.status(500);
        }

        return gson.toJson(result);
    }
}
