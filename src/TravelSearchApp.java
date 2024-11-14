import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class TravelSearchApp extends JFrame {

    private JTextField searchField;
    private JTextArea resultArea;
    private JComboBox<String> searchTypeCombo;
    private Connection connection;

    public TravelSearchApp() {
        // Set up JFrame properties
        setTitle("Travel Destination Search");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top panel for search input
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout());

        String[] searchTypes = {"Country", "City", "Region"};
        searchTypeCombo = new JComboBox<>(searchTypes);
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");

        searchPanel.add(searchTypeCombo);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Result area
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Connect to database
        connectToDatabase();

        // Add search button functionality
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchDestinations();
            }
        });
    }

    private void connectToDatabase() {
        String url = "jdbc:mysql://localhost:3306/your_database_name";
        String user = "your_username";
        String password = "your_password";

        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchDestinations() {
        String searchType = searchTypeCombo.getSelectedItem().toString().toLowerCase();
        String searchValue = searchField.getText().trim();

        if (searchValue.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search term.");
            return;
        }

        String query = "SELECT name, description FROM destinations WHERE " + searchType + " = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, searchValue);
            ResultSet resultSet = preparedStatement.executeQuery();

            resultArea.setText("");  // Clear previous results
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                resultArea.append("Place: " + name + "\nDescription: " + description + "\n\n");
            }

            if (!resultSet.first()) {
                resultArea.append("No destinations found for the search term: " + searchValue);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TravelSearchApp app = new TravelSearchApp();
            app.setVisible(true);
        });
    }
}
