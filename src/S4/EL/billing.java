package S4.EL;

import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class billing {

    public static void generateBill(Connection con, int roomNo) {
        JFrame frame = new JFrame("Billing - Room " + roomNo);
        frame.setSize(650, 550);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        try {
            String query = "SELECT UID, NAME, ROOMTYPE, ROOMPRICE, MEALPLAN, MEALPRICE, DAYS " +
                         "FROM DATA WHERE ROOMNO = ?";
            try (PreparedStatement pst = con.prepareStatement(query)) {
                pst.setInt(1, roomNo);
                try (ResultSet rs = pst.executeQuery()) {
                    if (!rs.next()) {
                        JOptionPane.showMessageDialog(frame, 
                            "No booking found for Room " + roomNo, 
                            "Error", JOptionPane.ERROR_MESSAGE);
                        frame.dispose();
                        return;
                    }

                    // Retrieve all booking details
                    String userId = rs.getString("UID");
                    String name = rs.getString("NAME");
                    String roomType = rs.getString("ROOMTYPE");
                    int roomPrice = rs.getInt("ROOMPRICE");
                    String mealPlan = rs.getString("MEALPLAN");
                    int mealPrice = rs.getInt("MEALPRICE");
                    int days = rs.getInt("DAYS");

                    // Calculate charges
                    int serviceCost = roomservice.getTotalServiceCost();
                    int totalRoomCost = roomPrice * days;
                    int totalMealCost = mealPrice * days;
                    double subtotal = totalRoomCost + totalMealCost;
                    double tax = subtotal * 0.1;
                    double totalBill = subtotal + serviceCost + tax;

                    // Create the bill display
                    JTextArea billArea = new JTextArea();
                    billArea.setEditable(false);
                    billArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
                    billArea.setText(
                        "============================================\n" +
                        "           XYZ REGENCY - FINAL BILL\n" +
                        "============================================\n" +
                        String.format("%-25s: %s\n", "Guest Name", name) +
                        String.format("%-25s: %s\n", "User ID", userId) +
                        String.format("%-25s: %d\n", "Room Number", roomNo) +
                        "--------------------------------------------\n" +
                        String.format("%-25s: %s\n", "Room Type", roomType) +
                        String.format("%-25s: ₹%,d x %d days\n", "Room Rate", roomPrice, days) +
                        String.format("%-25s: ₹%,d\n", "Room Charges", totalRoomCost) +
                        "--------------------------------------------\n" +
                        String.format("%-25s: %s\n", "Meal Plan", mealPlan) +
                        String.format("%-25s: ₹%,d x %d days\n", "Meal Rate", mealPrice, days) +
                        String.format("%-25s: ₹%,d\n", "Meal Charges", totalMealCost) +
                        "--------------------------------------------\n" +
                        String.format("%-25s: ₹%,d\n", "Additional Services", serviceCost) +
                        String.format("%-25s: ₹%,.2f\n", "Tax (10%)", tax) +
                        "============================================\n" +
                        String.format("%-25s: ₹%,.2f\n", "TOTAL AMOUNT DUE", totalBill) +
                        "============================================"
                    );

                    // Payment button
                    JButton payButton = new JButton("CONFIRM PAYMENT & CHECK OUT");
                    payButton.setFont(new Font("Arial", Font.BOLD, 16));
                    payButton.setBackground(new Color(0, 100, 0));
                    payButton.setForeground(Color.WHITE);
                    payButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                    payButton.addActionListener(e -> {
                        try {
                            // Delete booking record
                            String deleteQuery = "DELETE FROM DATA WHERE ROOMNO = ?";
                            try (PreparedStatement deleteStmt = con.prepareStatement(deleteQuery)) {
                                deleteStmt.setInt(1, roomNo);
                                deleteStmt.executeUpdate();
                            }

                            // Reset service charges
                            roomservice.resetServiceCost();

                            JOptionPane.showMessageDialog(frame, 
                                "Payment successful! Thank you for staying with us.", 
                                "Success", JOptionPane.INFORMATION_MESSAGE);

                            frame.dispose();
                            openMainMenu();
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(frame, 
                                "Payment failed: " + ex.getMessage(), 
                                "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    });

                    // Cancel button
                    JButton cancelButton = new JButton("CANCEL");
                    cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
                    cancelButton.addActionListener(e -> frame.dispose());

                    // Button panel
                    JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 15));
                    buttonPanel.add(cancelButton);
                    buttonPanel.add(payButton);

                    // Add components to main panel
                    panel.add(new JScrollPane(billArea), BorderLayout.CENTER);
                    panel.add(buttonPanel, BorderLayout.SOUTH);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, 
                "Database error: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            frame.dispose();
        }

        frame.add(panel);
        frame.setVisible(true);
    }

    private static void openMainMenu() {
        // Basic main menu implementation
        JFrame mainMenu = new JFrame("XYZ Regency - Main Menu");
        mainMenu.setSize(500, 400);
        mainMenu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainMenu.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add menu buttons (functionality would need to be implemented)
        panel.add(new JButton("User Details"));
        panel.add(new JButton("Book a Room"));
        panel.add(new JButton("Book Room Service"));
        panel.add(new JButton("Check In/Out"));
        panel.add(new JButton("Billing"));
        panel.add(new JButton("Exit"));

        mainMenu.add(panel);
        mainMenu.setVisible(true);
    }
}
