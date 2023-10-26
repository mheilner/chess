import handler.*;

import static spark.Spark.*;

public class Server {

    public static void main(String[] args) {
        // Set the port number
        port(8080);

        // Set up static file handling
        externalStaticFileLocation("web"); // Assuming "web" is the directory containing your assets


        createRoutes();

        // Set up exception handling
        exception(Exception.class, (exception, request, response) -> {
            // Log the exception (using SLF4J or any other logger)
            // Send a 500 response
            response.status(500);
            response.body("Server error: " + exception.getMessage());
        });
        // If no routes match, it's a 404
        notFound((req, res) -> {
            return "404 - Not Found";
        });

    }

    private static void createRoutes() {

        post("/user", RegisterHandler.getInstance());

        post("/session", LoginHandler.getInstance());

        delete("/session", LogoutHandler.getInstance());

        //        post("/register", (req, res) -> {
//            // TODO: Handle register request
//            return "Register response"; // Placeholder
//        });
//
//        // ... Add other routes as needed ...

    }

}
