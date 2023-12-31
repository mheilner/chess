package server;

import dataAccess.DataAccessException;
import dataAccess.Database;
import handler.*;

import static spark.Spark.*;

public class Server {
    public static Database db = new Database();
    public static void main(String[] args) throws DataAccessException {
        // Set the port number
        port(8080);

        // Set up WebSocket connection
        webSocket("/connect", WSHandler.class);

        // Set up static file handling
        externalStaticFileLocation("web");

        createRoutes();


        // Set up exception handling
        exception(Exception.class, (exception, request, response) -> {
            // Log the exception (using SLF4J or any other logger)
            // Send a 500 response
            response.status(500);
            response.body("server.Server error: " + exception.getMessage());
        });
        // If no routes match, it's a 404
        notFound((req, res) -> {
            return "404 - Not Found";
        });

    }

    private static void createRoutes() throws DataAccessException {

        post("/user", RegisterHandler.getInstance());

        post("/session", LoginHandler.getInstance());

        delete("/session", LogoutHandler.getInstance());

        get("/game", ListGamesHandler.getInstance());

        post("/game", CreateGameHandler.getInstance());

        put("/game", new JoinGameHandler());

        delete("/db", ClearHandler.getInstance());
    }
}
