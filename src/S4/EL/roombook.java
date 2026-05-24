package S4.EL;

import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class roombook {
    private static final int STANDARD_SINGLE_PRICE = 1200;
    private static final int STANDARD_DOUBLE_PRICE = 2500;
    private static final int EXECUTIVE_SUITE_PRICE = 7000;
    private static final int PRESIDENTIAL_SUITE_PRICE = 10000;
    
    private static final int BREAKFAST_PRICE = 100;
    private static final int HALF_BOARD_PRICE = 300;
    private static final int FULL_BOARD_PRICE = 800;

    public void bookRoom(Userdet user, Connection con) {
        JFrame frame = new JFrame("Room Booking");
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Tab 1: Book with existing details
        tabbedPane.addTab("Existing User", createExistingUserPanel(user, con, frame));
        
        // Tab 2: Book with new details
        tabbedPane.addTab("New Guest", createNewGuestPanel(con, frame));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> frame.dispose());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private JPanel createExistingUserPanel(Userdet user, Connection con, JFrame parentFrame) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel infoPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        infoPanel.add(new JLabel("Name:"));
        infoPanel.add(new JLabel(user.getName()));
        infoPanel.add(new JLabel("Phone:"));
        infoPanel.add(new JLabel(user.getPhone()));
        infoPanel.add(new JLabel("Age:"));
        infoPanel.add(new JLabel(String.valueOf(user.getAge())));
        infoPanel.add(new JLabel("Address:"));
        infoPanel.add(new JLabel(user.getAddress()));

        JPanel bookingPanel = createBookingFormPanel();
        
        JButton bookButton = new JButton("Book Room");
        bookButton.addActionListener(e -> {
            try {
                BookingDetails details = getBookingDetails(bookingPanel, user);
                if (saveUserBooking(user, details, con)) {
                    JOptionPane.showMessageDialog(parentFrame, 
                        "Booking confirmed for " + user.getName() + 
                        "\nRoom No: " + details.roomNo, 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    parentFrame.dispose();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parentFrame, 
                    "Error: " + ex.getMessage(), 
                    "Booking Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(infoPanel, BorderLayout.NORTH);
        panel.add(bookingPanel, BorderLayout.CENTER);
        panel.add(bookButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createNewGuestPanel(Connection con, JFrame parentFrame) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel guestPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField ageField = new JTextField();
        JTextField addressField = new JTextField();

        guestPanel.add(new JLabel("Full Name:"));
        guestPanel.add(nameField);
        guestPanel.add(new JLabel("Phone:"));
        guestPanel.add(phoneField);
        guestPanel.add(new JLabel("Age:"));
        guestPanel.add(ageField);
        guestPanel.add(new JLabel("Address:"));
        guestPanel.add(addressField);

        JPanel bookingPanel = createBookingFormPanel();
        
        JButton bookButton = new JButton("Book Room");
        bookButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String phone = phoneField.getText().trim();
                int age = Integer.parseInt(ageField.getText().trim());
                String address = addressField.getText().trim();
                
                if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                    throw new IllegalArgumentException("All fields are required");
                }

                BookingDetails details = getBookingDetails(bookingPanel, 
                    new Userdet("", "", name, age, phone, address));
                
                if (saveNewGuestBooking(details, con)) {
                    JOptionPane.showMessageDialog(parentFrame, 
                        "Booking confirmed for " + name + 
                        "\nRoom No: " + details.roomNo, 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    parentFrame.dispose();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parentFrame, 
                    "Error: " + ex.getMessage(), 
                    "Booking Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(guestPanel, BorderLayout.NORTH);
        panel.add(bookingPanel, BorderLayout.CENTER);
        panel.add(bookButton, BorderLayout.SOUTH);

        return panel;
    }

    // ... [Rest of the methods remain unchanged]
    private JPanel createBookingFormPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        
        String[] roomTypes = {"Standard single room", "Standard double room", 
                            "Executive suite", "Presidential suite"};
        JComboBox<String> roomTypeCombo = new JComboBox<>(roomTypes);
        
        String[] mealPlans = {"Breakfast", "Half board", "Full board"};
        JComboBox<String> mealPlanCombo = new JComboBox<>(mealPlans);
        
        JTextField daysField = new JTextField();

        panel.add(new JLabel("Room Type:"));
        panel.add(roomTypeCombo);
        panel.add(new JLabel("Meal Plan:"));
        panel.add(mealPlanCombo);
        panel.add(new JLabel("Duration (days):"));
        panel.add(daysField);

        return panel;
    }

    private BookingDetails getBookingDetails(JPanel bookingPanel, Userdet user) {
        JComboBox<String> roomTypeCombo = (JComboBox<String>) bookingPanel.getComponent(1);
        JComboBox<String> mealPlanCombo = (JComboBox<String>) bookingPanel.getComponent(3);
        JTextField daysField = (JTextField) bookingPanel.getComponent(5);
        
        String roomType = (String) roomTypeCombo.getSelectedItem();
        String mealPlan = (String) mealPlanCombo.getSelectedItem();
        int days = Integer.parseInt(daysField.getText());

        if (days <= 0) {
            throw new IllegalArgumentException("Duration must be at least 1 day");
        }

        int roomPrice = calculateRoomPrice(roomType);
        int mealPrice = calculateMealPrice(mealPlan);
        int roomNo = generateRoomNumber();

        return new BookingDetails(
            user.getName(), user.getAge(), user.getPhone(), user.getAddress(),
            roomNo, roomType, roomPrice, mealPlan, mealPrice, days
        );
    }

    private boolean saveUserBooking(Userdet user, BookingDetails details, Connection con) throws SQLException {
        String query = "UPDATE DATA SET ROOMNO = ?, ROOMTYPE = ?, ROOMPRICE = ?, " +
                      "MEALPLAN = ?, MEALPRICE = ?, DAYS = ? WHERE UID = ?";
        
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, details.roomNo);
            pst.setString(2, details.roomType);
            pst.setInt(3, details.roomPrice);
            pst.setString(4, details.mealPlan);
            pst.setInt(5, details.mealPrice);
            pst.setInt(6, details.days);
            pst.setString(7, user.getUsername());
            
            return pst.executeUpdate() > 0;
        }
    }

    private boolean saveNewGuestBooking(BookingDetails details, Connection con) throws SQLException {
        String query = "INSERT INTO DATA (NAME, AGE, PHNO, ADR, ROOMNO, ROOMTYPE, " +
                      "ROOMPRICE, MEALPLAN, MEALPRICE, DAYS) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setString(1, details.name);
            pst.setInt(2, details.age);
            pst.setString(3, details.phone);
            pst.setString(4, details.address);
            pst.setInt(5, details.roomNo);
            pst.setString(6, details.roomType);
            pst.setInt(7, details.roomPrice);
            pst.setString(8, details.mealPlan);
            pst.setInt(9, details.mealPrice);
            pst.setInt(10, details.days);
            
            return pst.executeUpdate() > 0;
        }
    }

    private int calculateRoomPrice(String roomType) {
        switch (roomType) {
            case "Standard single room": return STANDARD_SINGLE_PRICE;
            case "Standard double room": return STANDARD_DOUBLE_PRICE;
            case "Executive suite": return EXECUTIVE_SUITE_PRICE;
            case "Presidential suite": return PRESIDENTIAL_SUITE_PRICE;
            default: return 0;
        }
    }

    private int calculateMealPrice(String mealPlan) {
        switch (mealPlan) {
            case "Breakfast": return BREAKFAST_PRICE;
            case "Half board": return HALF_BOARD_PRICE;
            case "Full board": return FULL_BOARD_PRICE;
            default: return 0;
        }
    }

    private int generateRoomNumber() {
        return (int) (Math.random() * 400) + 101;
    }

    private static class BookingDetails {
        String name;
        int age;
        String phone;
        String address;
        int roomNo;
        String roomType;
        int roomPrice;
        String mealPlan;
        int mealPrice;
        int days;

        public BookingDetails(String name, int age, String phone, String address,
                            int roomNo, String roomType, int roomPrice,
                            String mealPlan, int mealPrice, int days) {
            this.name = name;
            this.age = age;
            this.phone = phone;
            this.address = address;
            this.roomNo = roomNo;
            this.roomType = roomType;
            this.roomPrice = roomPrice;
            this.mealPlan = mealPlan;
            this.mealPrice = mealPrice;
            this.days = days;
        }
    }
}

