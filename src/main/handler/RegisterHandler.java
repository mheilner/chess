package handler;

import com.google.gson.Gson;
import requests.RegisterRequest;
import results.RegisterResult;
import services.RegisterService;
import spark.Request;
import spark.Response;
import spark.Route;

public class RegisterHandler implements Route {

    private static RegisterHandler instance;

    private final Gson gson;
    private final RegisterService registerService;

    // Make the constructor private to prevent instantiation
    private RegisterHandler() {
        gson = new Gson();
        registerService = new RegisterService();
    }

    // Public method to provide access to the instance
    public static RegisterHandler getInstance() {
        if (instance == null) {
            instance = new RegisterHandler();
        }
        return instance;
    }

    public Object handle(Request request, Response response) {
        // Deserialize the request body into a RegisterRequest object
        RegisterRequest registerRequest = gson.fromJson(request.body(), RegisterRequest.class);

        // Pass the RegisterRequest to the RegisterService
        RegisterResult registerResult = registerService.register(registerRequest);

        // Set the response type to JSON
        response.type("application/json");

        // Check for specific error messages and set appropriate status codes
        if (registerResult.getMessage() != null) {
            if (registerResult.getMessage().contains("Username already exists")) {
                response.status(403);
                registerResult.setMessage("Error: already taken");
            } else if (registerResult.getMessage().startsWith("Error while registering user")) {
                response.status(500);
                registerResult.setMessage("Error: " + registerResult.getMessage());
            } else {
                response.status(400);
                registerResult.setMessage("Error: bad request");
            }
        } else {
            response.status(200);
        }

        // Serialize the RegisterResult into the response body
        return gson.toJson(registerResult);
    }
}
