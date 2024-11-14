
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class TravelSearchApp extends JFrame {

    private JTextField searchField;
    private JTextArea resultArea;
    private JComboBox<String> searchTypeCombo;
    private JLabel imageLabel;
    private Connection connection;
    private boolean isLoggedIn;

    public TravelSearchApp() {
        // Set up JFrame properties
        setTitle("Travel Destination Search");
        setSize(700, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
        getContentPane().setBackground(new Color(255, 255, 240)); // Light cream background

        // Top panel for search input and login/register buttons
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        topPanel.setBackground(new Color(10, 102, 204)); // Deep blue

        String[] searchTypes = {"Country", "City", "Region"};
        searchTypeCombo = new JComboBox<>(searchTypes);
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");

        // Styling components
        searchTypeCombo.setBackground(Color.WHITE);
        searchField.setBackground(Color.WHITE);
        searchButton.setBackground(new Color(50, 205, 50)); // Green
        searchButton.setForeground(Color.WHITE);
        searchButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Rounded borders and hover effect for the search button
        searchButton.setFocusPainted(false);
        searchButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                searchButton.setBackground(new Color(34, 139, 34)); // Darker green on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                searchButton.setBackground(new Color(50, 205, 50)); // Original color
            }
        });

        topPanel.add(searchTypeCombo);
        topPanel.add(searchField);
        topPanel.add(searchButton);

        // Add Login and Register buttons
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");
        
        // Styling for login and register buttons
        loginButton.setBackground(new Color(165, 105, 225)); // Royal blue
        loginButton.setForeground(Color.WHITE);
        registerButton.setBackground(new Color(255, 99, 71)); // Tomato red

        registerButton.setForeground(Color.WHITE);

        topPanel.add(loginButton);
        topPanel.add(registerButton);

        // Result area
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setBackground(new Color(250, 250, 240));
        resultArea.setFont(new Font("Serif", Font.PLAIN, 16));
        resultArea.setForeground(new Color(30, 30, 30)); // Dark text color for readability
        resultArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(resultArea);

        // Image label
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setPreferredSize(new Dimension(200, 150)); // Consistent image size
        imageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add components to the frame
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(imageLabel, BorderLayout.SOUTH);

        // Connect to database
        connectToDatabase();
        showLoginMessage();

        // Add search button functionality
        searchButton.addActionListener(e -> searchDestinations());

        // Login and Register button actions
        loginButton.addActionListener(e -> new Login(this, connection, this).setVisible(true));
        registerButton.addActionListener(e -> new Register(this, connection).setVisible(true));
    }

    private void connectToDatabase() {
        String url = "jdbc:mysql://localhost:3306/traveldb";
        String user = "root";
        String password = "aniket";

        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showLoginMessage() {
        resultArea.setText("Please log in to see the destinations.");
        imageLabel.setIcon(null);
    }

    private void showAllDestinations() {
        String query = "SELECT * FROM destinations";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            resultArea.setText("All Destinations:\n\n");
            while (resultSet.next()) {
                displayDestination(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchDestinations() {
        if (!isLoggedIn) {
            JOptionPane.showMessageDialog(this, "Please log in to search destinations.");
            return;
        }

        String searchType = searchTypeCombo.getSelectedItem().toString().toLowerCase();
        String searchValue = searchField.getText().trim();

        if (searchValue.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search term.");
            return;
        }

        String query = "SELECT * FROM destinations WHERE " + searchType + " = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, searchValue);
            ResultSet resultSet = preparedStatement.executeQuery();

            resultArea.setText("Search Results:\n\n");
            boolean hasResults = false;

            while (resultSet.next()) {
                hasResults = true;
                displayDestination(resultSet);
            }

            if (!hasResults) {
                resultArea.append("No destinations found for the search term: " + searchValue);
                imageLabel.setIcon(null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayDestination(ResultSet resultSet) throws SQLException {
        String country = resultSet.getString("country");
        String city = resultSet.getString("city");
        String region = resultSet.getString("region");
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");

        resultArea.append("Country: " + country + "\nCity: " + city + "\nRegion: " + region + "\nPlace: " + name +
                "\nDescription: " + description + "\n\n=====================================\n");

        if (resultSet.getRow() == 1) displayImage(resultSet);
    }

    private void displayImage(ResultSet resultSet) {
        try {
            byte[] imgData = resultSet.getBytes("image");
            if (imgData != null) {
                ImageIcon icon = new ImageIcon(imgData);
                Image img = icon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(img));
            } else {
                imageLabel.setIcon(null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
        if (isLoggedIn) {
            showAllDestinations();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TravelSearchApp().setVisible(true));
    }
}
