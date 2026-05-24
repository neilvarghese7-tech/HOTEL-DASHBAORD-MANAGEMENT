package S4.EL;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class checkinout {
    private Connection con;
    
    public checkinout(Connection connection) {
        if (connection == null) {
            JOptionPane.showMessageDialog(null,
                "Database connection not available",
                "Connection Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        this.con = connection;
        initializeUI();
    }
    
    private void initializeUI() {
        JFrame frame = new JFrame("Check In/Out System");
        frame.setSize(400, 250);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 1, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JButton checkInButton = createStyledButton("Check In", new Color(70, 130, 180));
        checkInButton.addActionListener(e -> {
            frame.dispose();
            processCheckIn();
        });

        JButton checkOutButton = createStyledButton("Check Out", new Color(220, 60, 60));
        checkOutButton.addActionListener(e -> {
            frame.dispose();
            processCheckOut();
        });

        JButton backButton = new JButton("Back to Main Menu");
        backButton.addActionListener(e -> frame.dispose());

        panel.add(checkInButton);
        panel.add(checkOutButton);
        panel.add(backButton);

        frame.add(panel);
        frame.setVisible(true);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return button;
    }

    private void processCheckIn() {
        String roomNoStr = JOptionPane.showInputDialog(null,
            "Enter Room Number:", "Check In", JOptionPane.QUESTION_MESSAGE);
            
        if (roomNoStr == null || roomNoStr.trim().isEmpty()) {
            return;
        }

        try {
            int roomNo = Integer.parseInt(roomNoStr);
            if (isRoomBooked(roomNo)) {
                JOptionPane.showMessageDialog(null, 
                    "Successfully checked into Room " + roomNo, 
                    "Check In Complete", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, 
                    "Room " + roomNo + " is not booked or doesn't exist", 
                    "Check In Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, 
                "Please enter a valid room number (digits only)", 
                "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, 
                "Database error: " + ex.getMessage(), 
                "System Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void processCheckOut() {
        String roomNoStr = JOptionPane.showInputDialog(null,
            "Enter Room Number:", "Check Out", JOptionPane.QUESTION_MESSAGE);
            
        if (roomNoStr == null || roomNoStr.trim().isEmpty()) {
            return;
        }

        try {
            int roomNo = Integer.parseInt(roomNoStr);
            if (isRoomBooked(roomNo)) {
                new billing().generateBill(con, roomNo);
            } else {
                JOptionPane.showMessageDialog(null, 
                    "Room " + roomNo + " is not booked or doesn't exist", 
                    "Check Out Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, 
                "Please enter a valid room number (digits only)", 
                "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, 
                "Database error: " + ex.getMessage(), 
                "System Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isRoomBooked(int roomNo) throws SQLException {
        String query = "SELECT ROOMNO FROM DATA WHERE ROOMNO = ?";
        try (PreparedStatement pst = con.prepareStatement(query)) {
            pst.setInt(1, roomNo);
            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }
        }
    }
}
