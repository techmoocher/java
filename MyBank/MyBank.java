// import java.util.InputMismatchException;
import java.util.ArrayList;
import java.util.Scanner;

public class MyBank {
    private static final String MAIN_HELP_MESSAGES =
    """
    All commands:
    - help: Show the list of commands.
    - exit: Exit the application.
    - list: List all available users.
    - adduser: Create a new user.
    - login: Log in to an existing account.
    """;

    private static final String USER_HELP_MESSAGES =
    """
    All commands:
    - help: Show the list of commands.
    - exit: Exit the application.
    - logout: Log out from the current account.
    - delete: Delete the current user (requires password).
    - resetpassword: Reset your password.
    - checkbalance: Check your current balance.
    - deposit <amount>: Deposit money into your account.
    - withdraw <amount>: Withdraw money from your account.
    - transfer <recipient_username> <amount>: Transfer money to another user.
    """;

    private static final ArrayList<String> BLACKLIST_USERNAMES = new ArrayList<String>() {{
        add("admin"); add("administrator"); add("root"); add("superuser");
        add("system"); add("guest"); add("test"); add("support"); add("info");
        add("main"); add("user"); add("bank"); add("sex"); add("cunt");
        add("whore"); add("bitch"); add("penis"); add("fuck"); add("dick");
        add("asshole"); add("motherfucker"); add("nigga");
    }};

    public static void main(String[] args) {
        UserManager userManager = new UserManager();
        String currentUser = "main";
        Scanner sc = new Scanner(System.in);

        System.out.println("Welcome to MyBank!");
        System.out.println("------------------------------");
        System.out.println("Type 'help' for a list of commands.\n");

        while (true) {
            System.out.print(currentUser + "> ");
            String[] input = sc.nextLine().trim().split("\\s+");
            String command = input[0].toLowerCase();

            if (command.isEmpty()) {
                continue;
            }

            if (currentUser.equals("main")) {
                // MAIN interface
                switch (command) {
                    case "help" -> System.out.println(MAIN_HELP_MESSAGES);
                    case "exit" -> {
                        System.out.println("Exiting MyBank. Goodbye!\n");
                        sc.close();
                        return;
                    }
                    case "list" -> userManager.getUserList();
                    case "adduser" -> handleAddUser(sc, userManager);
                    case "login" -> {
                        String loggedInUser = handleLogin(sc, userManager);
                        if (loggedInUser != null) {
                            currentUser = loggedInUser;
                        }
                    }
                    default -> System.out.println("Invalid command. Type 'help' for the command list.\n");
                }
            }
            else {
                // <user> interface
                User user = userManager.findUserByUsername(currentUser);
                if (user == null) { // Safety check
                    System.out.println("Critical error: User data lost. Logging out.");
                    currentUser = "main";
                    continue;
                }

                switch (command) {
                    case "help" -> System.out.println(USER_HELP_MESSAGES);
                    case "exit" -> {
                        System.out.println("Exiting MyBank. Goodbye!\n");
                        sc.close();
                        return;
                    }
                    case "logout" -> {
                        currentUser = "main";
                        System.out.println("Logged out successfully!\n");
                    }
                    case "delete" -> {
                        if (handleDeleteUser(sc, userManager, user)) {
                            currentUser = "main"; // Log out after deletion
                        }
                    }
                    case "resetpassword" -> handleResetPassword(sc, user);
                    case "checkbalance" -> System.out.printf("Your current balance is: $%.2f%n%n", user.getBalance());
                    case "deposit" -> handleDeposit(input, user);
                    case "withdraw" -> handleWithdraw(input, user);
                    case "transfer" -> handleTransfer(input, user, userManager);
                    default -> System.out.println("Invalid command. Type 'help' for the command list.\n");
                }
            }
        }
    }

    private static void handleAddUser(Scanner sc, UserManager userManager) {
        System.out.print("Enter username (1-15 characters): ");
        String username = sc.nextLine().trim();
        if (username.length() < 1 || username.length() > 15) {
            System.out.println("Username must be between 1 and 15 characters long.\n");
            return;
        }
        if (BLACKLIST_USERNAMES.contains(username.toLowerCase())) {
            System.out.println("This username is not allowed. Please choose another.\n");
            return;
        }
        if (userManager.findUserByUsername(username) != null) {
            System.out.println("Username already exists. Please choose another.\n");
            return;
        }

        System.out.print("Enter password (8-20 characters): ");
        String password = sc.nextLine().trim();
        if (password.length() < 8 || password.length() > 20) {
            System.out.println("Password must be between 8 and 20 characters long.\n");
            return;
        }
        userManager.addUser(username, password);
        System.out.println("User '" + username + "' created successfully! You can now log in.\n");
    }

    private static String handleLogin(Scanner sc, UserManager userManager) {
        System.out.print("Enter username: ");
        String username = sc.nextLine().trim();
        System.out.print("Enter password: ");
        String password = sc.nextLine().trim();

        if (userManager.login(username, password)) {
            System.out.println("Login successful! Welcome, " + username + ".\nType 'help' for a list of commands.\n");
            // Return the canonical username to handle case differences
            return userManager.findUserByUsername(username).getUsername();
        } else {
            System.out.println("Login failed. Invalid username or password.\n");
            return null;
        }
    }

    private static boolean handleDeleteUser(Scanner sc, UserManager userManager, User user) {
        System.out.print("To confirm, please enter your password: ");
        String password = sc.nextLine().trim();
        if (user.getPassword().equals(password)) {
            userManager.deleteUser(user.getUsername());
            System.out.println("Your account has been permanently deleted. We're sorry to see you go.\n");
            return true;
        } else {
            System.out.println("Incorrect password. Account deletion cancelled.\n");
            return false;
        }
    }
    
    private static void handleResetPassword(Scanner sc, User user) {
        System.out.print("Enter your current password: ");
        String currentPassword = sc.nextLine().trim();
        System.out.print("Enter your new password (8-20 characters): ");
        String newPassword = sc.nextLine().trim();
        try {
            user.resetPassword(currentPassword, newPassword);
            System.out.println("Password reset successfully!\n");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage() + "\n");
        }
    }

    private static void handleDeposit(String[] input, User user) {
        if (input.length != 2) {
            System.out.println("Usage: deposit <amount>\n");
            return;
        }
        try {
            double amount = Double.parseDouble(input[1]);
            user.deposit(amount);
            System.out.printf("Successfully deposited $%.2f. New balance: %.2f%n%n", amount, user.getBalance());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Please enter a number.\n");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage() + "\n");
        }
    }
    
    private static void handleWithdraw(String[] input, User user) {
        if (input.length != 2) {
            System.out.println("Usage: withdraw <amount>\n");
            return;
        }
        try {
            double amount = Double.parseDouble(input[1]);
            user.withdraw(amount);
            System.out.printf("Successfully withdrew $%.2f. New balance: $%.2f%n%n", amount, user.getBalance());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Please enter a number.\n");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage() + "\n");
        }
    }

    private static void handleTransfer(String[] input, User sender, UserManager userManager) {
        if (input.length != 3) {
            System.out.println("Usage: transfer <recipient_username> <amount>\n");
            return;
        }
        String recipientName = input[1];
        if (recipientName.equalsIgnoreCase(sender.getUsername())) {
            System.out.println("You cannot transfer money to yourself.\n");
            return;
        }

        User recipient = userManager.findUserByUsername(recipientName);
        if (recipient == null) {
            System.out.println("Recipient user '" + recipientName + "' not found.\n");
            return;
        }

        try {
            double amount = Double.parseDouble(input[2]);
            // Perform transaction
            sender.withdraw(amount);
            recipient.deposit(amount);
            System.out.printf("Successfully transferred $%.2f to %s.%n", amount, recipient.getUsername());
            System.out.printf("Your new balance is: $%.2f%n%n", sender.getBalance());
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount. Please enter a number.\n");
        } catch (IllegalArgumentException e) {
            System.out.println("Transaction failed: " + e.getMessage() + "\n");
        }
    }
}
