import java.io.*;
import java.util.*;

class User implements Serializable {
    String userId;
    String pin;
    double balance;

    public User(String userId, String pin, double balance) {
        this.userId = userId;
        this.pin = pin;
        this.balance = balance;
    }
}

class UserDAO {
    private final String fileName = "users.dat";

    public void saveUser(User user) throws IOException {
        List<User> users = getAllUsers();
        users.add(user);
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
        oos.writeObject(users);
        oos.close();
    }

    public List<User> getAllUsers() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));
            List<User> users = (List<User>) ois.readObject();
            ois.close();
            return users;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public User authenticate(String userId, String pin) {
        for (User user : getAllUsers()) {
            if (user.userId.equals(userId) && user.pin.equals(pin)) {
                return user;
            }
        }
        return null;
    }

    public void updateUser(User updatedUser) throws IOException {
        List<User> users = getAllUsers();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).userId.equals(updatedUser.userId)) {
                users.set(i, updatedUser);
                break;
            }
        }
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
        oos.writeObject(users);
        oos.close();
    }
}

public class ATM {
    private static final Scanner sc = new Scanner(System.in);
    private static final UserDAO dao = new UserDAO();

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n--- ATM Machine ---");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> register();
                case 2 -> login();
                case 3 -> System.exit(0);
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    static void register() {
        try {
            System.out.print("Enter User ID: ");
            String userId = sc.nextLine();
            System.out.print("Enter PIN: ");
            String pin = sc.nextLine();
            dao.saveUser(new User(userId, pin, 0));
            System.out.println("User registered successfully!");
        } catch (IOException e) {
            System.out.println("Error while saving user.");
        }
    }

    static void login() {
        System.out.print("Enter User ID: ");
        String userId = sc.nextLine();
        System.out.print("Enter PIN: ");
        String pin = sc.nextLine();

        User user = dao.authenticate(userId, pin);
        if (user == null) {
            System.out.println("Invalid credentials.");
            return;
        }

        while (true) {
            System.out.println("\n1. View Balance");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Logout");
            System.out.print("Choose option: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1 -> System.out.println("Balance: Rs. " + user.balance);
                case 2 -> {
                    System.out.print("Enter amount to deposit: ");
                    double amt = sc.nextDouble();
                    user.balance += amt;
                    try { dao.updateUser(user); } catch (IOException e) { }
                    System.out.println("Deposited successfully.");
                }
                case 3 -> {
                    System.out.print("Enter amount to withdraw: ");
                    double amt = sc.nextDouble();
                    if (amt <= user.balance) {
                        user.balance -= amt;
                        try { dao.updateUser(user); } catch (IOException e) { }
                        System.out.println("Withdrawn successfully.");
                    } else {
                        System.out.println("Insufficient balance.");
                    }
                }
                case 4 -> {
                    System.out.println("Logged out.");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }
}