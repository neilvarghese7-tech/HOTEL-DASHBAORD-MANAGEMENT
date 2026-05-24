import S4.EL.Userdet;
import S4.EL.billing;
import S4.EL.checkinout;
import S4.EL.roombook;
import S4.EL.roomservice;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class App {
    private static Connection con;
    private static Userdet currentUser;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            establishDatabaseConnection();
            new WelcomeScreen();
        });
    }

    private static void establishDatabaseConnection() {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Hotelmanagement", "root", "Myroot123*");
            System.out.println("✅ Database connection established");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Database connection failed: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    public static Connection getConnection() {
        return con;
    }

    public static Userdet getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(Userdet user) {
        currentUser = user;
    }

    public static void closeApplication() {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
                System.out.println("🔌 Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
        System.exit(0);
    }
}

class WelcomeScreen extends JFrame {
    public WelcomeScreen() {
        setTitle("XYZ Regency - Welcome");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel welcomeLabel = new JLabel("Welcome to XYZ Regency", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(welcomeLabel, BorderLayout.CENTER);

        JButton nextButton = new JButton("Next");
        nextButton.setFont(new Font("Arial", Font.PLAIN, 16));
        nextButton.addActionListener(e -> {
            dispose();
            new AuthSelectionScreen();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(nextButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);
        setVisible(true);
    }
}

class AuthSelectionScreen extends JFrame {
    public AuthSelectionScreen() {
        setTitle("XYZ Regency - Authentication");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Please select an option:", JLabel.CENTER);
        panel.add(titleLabel);

        JButton loginButton = new JButton("Login");
        JButton signupButton = new JButton("Sign Up");

        loginButton.addActionListener(e -> {
            dispose();
            new LoginScreen();
        });

        signupButton.addActionListener(e -> {
            dispose();
            new SignUpScreen();
        });

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.add(loginButton);
        buttonPanel.add(signupButton);
        panel.add(buttonPanel);

        add(panel);
        setVisible(true);
    }
}

class LoginScreen extends JFrame {
    private JTextField userField = new JTextField(15);
    private JPasswordField passField = new JPasswordField(15);

    public LoginScreen() {
        setTitle("XYZ Regency - Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Username:"));
        panel.add(userField);
        panel.add(new JLabel("Password:"));
        panel.add(passField);

        JButton backButton = new JButton("Back");
        JButton loginButton = new JButton("Login");

        backButton.addActionListener(e -> {
            dispose();
            new AuthSelectionScreen();
        });

        loginButton.addActionListener(e -> attemptLogin());

        panel.add(backButton);
        panel.add(loginButton);

        add(panel);
        setVisible(true);
    }

    private void attemptLogin() {
        String username = userField.getText().trim();
        String password = new String(passField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Userdet user = authenticateUser(username, password);
            if (user != null) {
                App.setCurrentUser(user);
                JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new MainMenuScreen();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private Userdet authenticateUser(String username, String password) throws SQLException {
        String query = "SELECT * FROM DATA WHERE UID = ? AND PAS = ?";
        try (PreparedStatement pst = App.getConnection().prepareStatement(query)) {
            pst.setString(1, username);
            pst.setString(2, password);

            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return new Userdet(
                            rs.getString("UID"),
                            rs.getString("PAS"),
                            rs.getString("NAME"),
                            rs.getInt("AGE"),
                            rs.getString("PHNO"),
                            rs.getString("ADR"));
                }
            }
        }
        return null;
    }
}

class SignUpScreen extends JFrame {
    private JTextField nameField = new JTextField();
    private JTextField ageField = new JTextField();
    private JTextField phoneField = new JTextField();
    private JTextField addressField = new JTextField();
    private JTextField usernameField = new JTextField();
    private JPasswordField passwordField = new JPasswordField();

    public SignUpScreen() {
        setTitle("XYZ Regency - Sign Up");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Age:"));
        panel.add(ageField);
        panel.add(new JLabel("Phone Number:"));
        panel.add(phoneField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        JButton backButton = new JButton("Back");
        JButton registerButton = new JButton("Register");

        backButton.addActionListener(e -> {
            dispose();
            new AuthSelectionScreen();
        });

        registerButton.addActionListener(e -> attemptRegistration());

        panel.add(backButton);
        panel.add(registerButton);

        add(panel);
        setVisible(true);
    }

    private void attemptRegistration() {
        try {
            String name = nameField.getText().trim();
            int age = Integer.parseInt(ageField.getText().trim());
            String phone = phoneField.getText().trim();
            String address = addressField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (!isValidPassword(password)) {
                JOptionPane.showMessageDialog(this,
                        "Password must be at least 12 characters with:\n- 1 uppercase letter\n- 1 number\n- 1 special character",
                        "Weak Password", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (isUsernameTaken(username)) {
                JOptionPane.showMessageDialog(this, "Username already exists", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Userdet user = registerUser(name, age, phone, address, username, password);
            if (user != null) {
                App.setCurrentUser(user);
                JOptionPane.showMessageDialog(this, "Registration successful!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new MainMenuScreen();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid age", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 12 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*\\d.*") &&
                password.matches(".*[@#$%^&+=!].*");
    }

    private boolean isUsernameTaken(String username) throws SQLException {
        String query = "SELECT UID FROM DATA WHERE UID = ?";
        try (PreparedStatement pst = App.getConnection().prepareStatement(query)) {
            pst.setString(1, username);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Userdet registerUser(String name, int age, String phone, String address,
            String username, String password) throws SQLException {
        String query = "INSERT INTO DATA (UID, PAS, NAME, AGE, PHNO, ADR, ROOMNO, DAYS, ROOMTYPE, ROOMPRICE, MEALPLAN, MEALPRICE) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = App.getConnection().prepareStatement(query)) {
            pst.setString(1, username);
            pst.setString(2, password);
            pst.setString(3, name);
            pst.setInt(4, age);
            pst.setString(5, phone);
            pst.setString(6, address);
            pst.setNull(7, Types.INTEGER); // ROOMNO (NULL)
            pst.setInt(8, 0); // DAYS
            pst.setString(9, "NOT ASSIGNED"); // ROOMTYPE
            pst.setInt(10, 0); // ROOMPRICE
            pst.setString(11, "NONE"); // MEALPLAN
            pst.setInt(12, 0); // MEALPRICE

            if (pst.executeUpdate() > 0) {
                return new Userdet(username, password, name, age, phone, address);
            }
        }
        return null;
    }
}

class MainMenuScreen extends JFrame {
    public MainMenuScreen() {
        setTitle("XYZ Regency - Main Menu");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        addButton(panel, "User Details", e -> App.getCurrentUser().showUserDetails());
        addButton(panel, "Book a Room", e -> new roombook().bookRoom(App.getCurrentUser(), App.getConnection()));
        addButton(panel, "Book Room Service", e -> new roomservice().bookService());
        addButton(panel, "Check In/Out", e -> new checkinout(App.getConnection())); // Modified to pass connection
        addButton(panel, "Billing", e -> showBillingScreen());
        addButton(panel, "Exit", e -> App.closeApplication());

        add(panel);
        setVisible(true);
    }

    private void showBillingScreen() {
        String roomNoStr = JOptionPane.showInputDialog(this,
                "Enter Room Number:", "Billing", JOptionPane.QUESTION_MESSAGE);

        if (roomNoStr == null || roomNoStr.trim().isEmpty()) {
            return;
        }

        try {
            int roomNo = Integer.parseInt(roomNoStr);
            new billing().generateBill(App.getConnection(), roomNo);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid room number",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addButton(JPanel panel, String text, ActionListener action) {
        JButton button = new JButton(text);
        button.addActionListener(action);
        panel.add(button);
    }
}
