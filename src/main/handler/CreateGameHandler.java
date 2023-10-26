package handler;

import com.google.gson.Gson;
import requests.CreateGameRequest;
import results.CreateGameResult;
import services.CreateGameService;
import spark.Request;
import spark.Response;
import spark.Route;
import dataAccess.AuthTokenDao;

public class CreateGameHandler implements Route {

    private static CreateGameHandler instance;

    private final Gson gson;
    private final CreateGameService createGameService;

    private CreateGameHandler() {
        gson = new Gson();
        createGameService = new CreateGameService();
    }

    public static CreateGameHandler getInstance() {
        if (instance == null) {
            instance = new CreateGameHandler();
        }
        return instance;
    }

    @Override
    public Object handle(Request request, Response response) {
        // Set the response type to JSON
        response.type("application/json");

        // Extract the authToken from the header
        String authToken = request.headers("authorization");

        // Validate the authToken
        if (!AuthTokenDao.getInstance().tokenExists(authToken)) {
            response.status(401);  // Unauthorized
            return gson.toJson(new CreateGameResult("Error: unauthorized"));
        }

        // Deserialize the request body
        CreateGameRequest createGameRequest = gson.fromJson(request.body(), CreateGameRequest.class);

        // Call the service
        CreateGameResult createGameResult = createGameService.createGame(createGameRequest);

        // Check for errors
        if (createGameResult.getMessage() != null) {
            // Determine the appropriate error code based on the message
            if (createGameResult.getMessage().equals("Error: unauthorized")) {
                response.status(401);
            } else if (createGameResult.getMessage().startsWith("Error:")) {
                response.status(500);
            } else {
                response.status(400);
            }
            return gson.toJson(createGameResult);
        }

        // Return the gameID of the newly created game
        response.status(200);  // OK
        return gson.toJson(createGameResult);
    }

}
