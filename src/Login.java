import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Login extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private boolean succeeded;
    private Connection connection;
    private TravelSearchApp parentApp;

    public Login(JFrame owner, Connection connection, TravelSearchApp parentApp) {
        super(owner, "Login", true);
        this.connection = connection;
        this.parentApp = parentApp;

        // Main panel setup with padding and background color
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(240, 240, 240));

        GridBagConstraints cs = new GridBagConstraints();
        cs.fill = GridBagConstraints.HORIZONTAL;

        // Title label
        JLabel title = new JLabel("Welcome, Please Login");
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
        cs.insets = new Insets(5, 0, 5, 10);
        panel.add(lbPassword, cs);

        passwordField = new JPasswordField(20);
        cs.gridx = 1;
        cs.gridy = 2;
        cs.gridwidth = 2;
        panel.add(passwordField, cs);

        // Login button with styling
        JButton btnLogin = new JButton("Login");
        btnLogin.setBackground(new Color(0, 153, 76));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnLogin.setBorderPainted(false);
        btnLogin.setFocusPainted(false);

        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (authenticate(getUsername(), getPassword())) {
                    JOptionPane.showMessageDialog(Login.this,
                            "Hi " + getUsername() + "! You have successfully logged in.",
                            "Login",
                            JOptionPane.INFORMATION_MESSAGE);
                    parentApp.setLoggedIn(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(Login.this,
                            "Invalid username or password",
                            "Login",
                            JOptionPane.ERROR_MESSAGE);
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

        // Button panel for layout
        JPanel bp = new JPanel();
        bp.setBackground(new Color(240, 240, 240));
        bp.add(btnLogin);
        bp.add(btnCancel);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(bp, BorderLayout.PAGE_END);

        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    private boolean authenticate(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement pst = connection.prepareStatement(query)) {
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error during authentication.", "Error", JOptionPane.ERROR_MESSAGE);
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
