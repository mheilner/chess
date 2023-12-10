package handler;

import chess.ChessPiece;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataAccess.DataAccessException;
import dataAccess.GameDao;
import results.ListGamesResult;
import services.ListGamesService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Objects;

public class ListGamesHandler implements Route {

    private static ListGamesHandler instance;

    private final Gson gson;
    private final ListGamesService listGamesService;

    private ListGamesHandler() throws DataAccessException {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ChessPiece.class, new GameDao.ChessPieceSerializer());
        this.gson = builder.create();
        listGamesService = new ListGamesService();
    }

    public static ListGamesHandler getInstance() throws DataAccessException {
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

        if (result.getMessage() == null || Objects.equals(result.getMessage(), "No games available.")) {
            response.status(200);
        } else if ("Invalid authentication token.".equals(result.getMessage())) {
            response.status(401);
        } else {
            response.status(500);
        }
        // return gson.toJson(result);
        String jsonResult = gson.toJson(result);
//        System.out.println("JSON being sent to client: " + jsonResult);
        return jsonResult;
    }
}
