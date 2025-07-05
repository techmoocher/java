class User {
    private String userID;
    private String username;
    private String password;
    private double balance;

    public User(String userID, String username, String password) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.balance = 0.0;
    }

    // Accessors
    public String getUserID() { return userID; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public double getBalance() { return balance; }

    // Mutators
    public void resetPassword(String currentPassword, String newPassword) {
        if (!this.password.equals(currentPassword)) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }
        if (newPassword == null || newPassword.length() < 8 || newPassword.length() > 20) {
            throw new IllegalArgumentException("New password must be 8-20 characters long.");
        }
        this.password = newPassword;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive.");
        }
        this.balance += amount;
    }
    
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        }
        if (amount > this.balance) {
            throw new IllegalArgumentException("Insufficient funds.");
        }
        this.balance -= amount;
    }
}
