package handler;

import com.google.gson.Gson;
import requests.JoinGameRequest;
import results.JoinGameResult;
import services.JoinGameService;
import spark.Request;
import spark.Response;
import spark.Route;

public class JoinGameHandler implements Route {

    private final Gson gson;
    private final JoinGameService joinGameService;

    public JoinGameHandler() {
        gson = new Gson();
        joinGameService = new JoinGameService();
    }

    @Override
    public Object handle(Request request, Response response) {
        String authToken = request.headers("authorization");
        JoinGameRequest joinGameRequest = gson.fromJson(request.body(), JoinGameRequest.class);

        JoinGameResult joinGameResult = joinGameService.joinGame(joinGameRequest, authToken);

        response.type("application/json");

        // Check for errors
        if (joinGameResult.getMessage() != null) {
            if (joinGameResult.getMessage().startsWith("Error: ")) {
                if ("Error: unauthorized".equals(joinGameResult.getMessage())) {
                    response.status(401);
                } else if ("Error: already taken".equals(joinGameResult.getMessage())) {
                    response.status(403);
                } else {
                    response.status(500);
                }
            } else {
                response.status(400);  // Bad request
            }
        } else {
            response.status(200);  // Success
        }

        return gson.toJson(joinGameResult);
    }
}
