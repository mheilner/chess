package handler;

import com.google.gson.Gson;
import requests.LoginRequest;
import results.LoginResult;
import services.LoginService;
import spark.Request;
import spark.Response;
import spark.Route;

public class LoginHandler implements Route {

    private static LoginHandler instance;

    private final Gson gson;
    private final LoginService loginService;

    private LoginHandler() {
        gson = new Gson();
        loginService = new LoginService();
    }

    public static LoginHandler getInstance() {
        if (instance == null) {
            instance = new LoginHandler();
        }
        return instance;
    }

    @Override
    public Object handle(Request request, Response response) {
        // Deserialize the request body
        LoginRequest loginRequest = gson.fromJson(request.body(), LoginRequest.class);

        // Call the service
        LoginResult loginResult = loginService.login(loginRequest);

        // Set the response type to JSON
        response.type("application/json");

        // Check for errors
        if (loginResult.getMessage() != null) {
            if ("Error: Invalid username or password.".equals(loginResult.getMessage())) {
                response.status(401);
            } else {
                response.status(500);
            }
        }

        // Serialize and return the result
        return gson.toJson(loginResult);
    }
}
