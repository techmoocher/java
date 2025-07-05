import java.util.ArrayList;
import java.util.Random;

class UserManager {
    private ArrayList<User> users;
    private Random random = new Random();

    public UserManager() {
        users = new ArrayList<>();
    }

    private String generateUserID() {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String newID;
        boolean isUnique;
        do {
            StringBuilder userIDBuilder = new StringBuilder(12);
            for (int i = 0; i < 12; i++) {
                userIDBuilder.append(characters.charAt(random.nextInt(characters.length())));
            }
            newID = userIDBuilder.toString();
            isUnique = true;
            for (User user : users) {
                if (user.getUserID().equals(newID)) {
                    isUnique = false;
                    break;
                }
            }
        } while (!isUnique);
        return newID;
    }

    public User findUserByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null;
    }

    public void addUser(String username, String password) {
        String newUserID = generateUserID();
        User newUser = new User(newUserID, username, password);
        users.add(newUser);
    }

    public boolean deleteUser(String username) {
        User userToDelete = findUserByUsername(username);
        if (userToDelete != null) {
            users.remove(userToDelete);
            return true;
        }
        return false;
    }
    
    public boolean login(String username, String password) {
        User user = findUserByUsername(username);
        return user != null && user.getPassword().equals(password);
    }

    public void getUserList() {
        if (users.isEmpty()) {
            System.out.println("No users found! :(\n");
        } else {
            System.out.println("Registered Users:");
            String border = "+----------------+------------------+";
            String header = "| User ID        | Username         |";
            System.out.println(border);
            System.out.println(header);
            System.out.println(border);
            for (User user : users) {
                System.out.printf("| %-14s | %-16s |%n", user.getUserID(), user.getUsername());
            }
            System.out.println(border + "\n");
        }
    }
}
