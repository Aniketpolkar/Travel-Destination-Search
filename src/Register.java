import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Register extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private boolean succeeded;
    private Connection connection;

    public Register(JFrame owner, Connection connection) {
        super(owner, "Register", true);
        this.connection = connection;

        // Main panel with padding and background color
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(240, 240, 240));

        GridBagConstraints cs = new GridBagConstraints();
        cs.fill = GridBagConstraints.HORIZONTAL;

        // Title label
        JLabel title = new JLabel("Create a New Account");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(new Color(70, 130, 180));
        cs.gridx = 0;
        cs.gridy = 0;
        cs.gridwidth = 3;
        cs.insets = new Insets(0, 0, 15, 0);
        panel.add(title, cs);

        // Username label and field
        JLabel lbUsername = new JLabel("Username:");
        lbUsername.setFont(new Font("SansSerif", Font.PLAIN, 14));
        cs.gridx = 0;
        cs.gridy = 1;
        cs.gridwidth = 1;
        cs.insets = new Insets(5, 0, 5, 10);
        panel.add(lbUsername, cs);

        usernameField = new JTextField(20);
        cs.gridx = 1;
        cs.gridy = 1;
        cs.gridwidth = 2;
        panel.add(usernameField, cs);

        // Password label and field
        JLabel lbPassword = new JLabel("Password:");
        lbPassword.setFont(new Font("SansSerif", Font.PLAIN, 14));
        cs.gridx = 0;
        cs.gridy = 2;
        cs.gridwidth = 1;
        panel.add(lbPassword, cs);

        passwordField = new JPasswordField(20);
        cs.gridx = 1;
        cs.gridy = 2;
        cs.gridwidth = 2;
        panel.add(passwordField, cs);

        // Confirm Password label and field
        JLabel lbConfirmPassword = new JLabel("Confirm Password:");
        lbConfirmPassword.setFont(new Font("SansSerif", Font.PLAIN, 14));
        cs.gridx = 0;
        cs.gridy = 3;
        cs.gridwidth = 1;
        panel.add(lbConfirmPassword, cs);

        confirmPasswordField = new JPasswordField(20);
        cs.gridx = 1;
        cs.gridy = 3;
        cs.gridwidth = 2;
        panel.add(confirmPasswordField, cs);

        // Register button with styling
        JButton btnRegister = new JButton("Register");
        btnRegister.setBackground(new Color(0, 153, 76));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnRegister.setBorderPainted(false);
        btnRegister.setFocusPainted(false);

        btnRegister.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = getUsername();
                String password = getPassword();
                String confirmPassword = new String(confirmPasswordField.getPassword());

                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(Register.this,
                            "Passwords do not match.",
                            "Registration",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (registerUser(username, password)) {
                    JOptionPane.showMessageDialog(Register.this,
                            "You have successfully registered.",
                            "Registration",
                            JOptionPane.INFORMATION_MESSAGE);
                    succeeded = true;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(Register.this,
                            "Registration failed. Username might be taken.",
                            "Registration",
                            JOptionPane.ERROR_MESSAGE);
                    usernameField.setText("");
                    passwordField.setText("");
                    confirmPasswordField.setText("");
                    succeeded = false;
                }
            }
        });

        // Cancel button with styling
        JButton btnCancel = new JButton("Cancel");
        btnCancel.setBackground(new Color(204, 51, 51));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnCancel.setBorderPainted(false);
        btnCancel.setFocusPainted(false);

        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // Button panel layout
        JPanel bp = new JPanel();
        bp.setBackground(new Color(240, 240, 240));
        bp.add(btnRegister);
        bp.add(btnCancel);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(bp, BorderLayout.PAGE_END);

        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    private boolean registerUser(String username, String password) {
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, username);
            pst.setString(2, password);  // Store plain password (should be hashed in production)
            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getUsername() {
        return usernameField.getText().trim();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public boolean isSucceeded() {
        return succeeded;
    }
}
