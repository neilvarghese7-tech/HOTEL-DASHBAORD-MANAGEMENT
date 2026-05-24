package S4.EL;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

public class roomservice {
    private static int totalServiceCost = 0;
    private final Map<String, Integer> servicePrices;

    public roomservice() {
        servicePrices = new HashMap<>();
        servicePrices.put("Cleanup", 500);
        servicePrices.put("Snacks", 300);
        servicePrices.put("Amenities", 700);
    }

    public void bookService() {
        JFrame frame = new JFrame("Room Service");
        frame.setSize(500, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JComboBox<String> serviceCombo = new JComboBox<>(servicePrices.keySet().toArray(new String[0]));
        JButton addButton = new JButton("Add Service");
        JLabel totalLabel = new JLabel("Total Service Cost: ₹0", JLabel.CENTER);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));

        addButton.addActionListener(e -> {
            String service = (String) serviceCombo.getSelectedItem();
            int price = servicePrices.get(service);
            totalServiceCost += price;
            totalLabel.setText("Total Service Cost: ₹" + totalServiceCost);
            JOptionPane.showMessageDialog(frame, 
                "Added " + service + " for ₹" + price, 
                "Service Added", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton doneButton = new JButton("Done");
        doneButton.addActionListener(e -> frame.dispose());

        JPanel inputPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        inputPanel.add(serviceCombo);
        inputPanel.add(addButton);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(totalLabel, BorderLayout.CENTER);
        panel.add(doneButton, BorderLayout.SOUTH);

        frame.add(panel);
        frame.setVisible(true);
    }

    public static int getTotalServiceCost() {
        return totalServiceCost;
    }

    public static void resetServiceCost() {
        totalServiceCost = 0;
    }
}
