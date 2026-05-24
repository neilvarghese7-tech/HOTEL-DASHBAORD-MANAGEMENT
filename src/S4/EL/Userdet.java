package S4.EL;

import java.awt.*;
import javax.swing.*;

public class Userdet {
    private final String username;
    private final String password;
    private final String name;
    private final String address;
    private final String phone;
    private final int age;

    public Userdet(String uid, String pas, String name, int age, String phno, String adr) {
        this.username = uid;
        this.password = pas;
        this.name = name;
        this.age = age;
        this.phone = phno;
        this.address = adr;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }

    public void showUserDetails() {
        JFrame frame = new JFrame("User Details");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        addDetailRow(panel, "Username:", username);
        addDetailRow(panel, "Name:", name);
        addDetailRow(panel, "Age:", String.valueOf(age));
        addDetailRow(panel, "Phone:", phone);
        addDetailRow(panel, "Address:", address);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> frame.dispose());
        
        panel.add(new JLabel());
        panel.add(closeButton);

        frame.add(panel);
        frame.setVisible(true);
    }

    private void addDetailRow(JPanel panel, String label, String value) {
        panel.add(new JLabel(label));
        panel.add(new JLabel(value));
    }
}
