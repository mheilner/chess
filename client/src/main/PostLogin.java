package ui; // or the appropriate package name

import java.util.Scanner;

public class PostLogin {
    private Scanner scanner;
    private boolean isLoggedIn = true; // Flag to maintain login state

    public PostLogin(Scanner scanner) {
        this.scanner = scanner;
    }

    public void displayMenu() {
        while (isLoggedIn) {
            System.out.println("Post-login menu:");
            System.out.println("1. List Games");
            System.out.println("2. Create Game");
            System.out.println("3. Join Game");
            System.out.println("4. Logout");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    listGames();
                    break;
                case 2:
                    createGame();
                    break;
                case 3:
                    joinGame();
                    break;
                case 4:
                    logout();
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }

    private void listGames() {
        // Implementation to list games
        System.out.println("Listing games...");
    }

    private void createGame() {
        // Implementation to create a game
        System.out.println("Creating a game...");
    }

    private void joinGame() {
        // Implementation to join a game
        System.out.println("Joining a game...");
    }

    private void logout() {
        // Implementation to logout
        System.out.println("Logging out...");
        isLoggedIn = false; // Update the flag to exit the loop
    }
}
