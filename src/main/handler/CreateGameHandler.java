package handler;

import com.google.gson.Gson;
import requests.CreateGameRequest;
import results.CreateGameResult;
import services.CreateGameService;
import spark.Request;
import spark.Response;
import spark.Route;

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

        // TODO: Validate the authToken and check if the user is authorized to create a game

        // Deserialize the request body
        CreateGameRequest createGameRequest = gson.fromJson(request.body(), CreateGameRequest.class);

        // Call the service
        CreateGameResult createGameResult = createGameService.createGame(createGameRequest);

        // Check for errors
        if (createGameResult.getMessage() != null) {
            response.status(400);  // Bad Request
            return gson.toJson(createGameResult);
        }

        // Return the gameID of the newly created game
        response.status(200);  // OK
        return gson.toJson(createGameResult);
    }
}
