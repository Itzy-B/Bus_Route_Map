package src.java.Database;

import javax.swing.*;
import java.awt.event.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.List;

public class RouteSearchGUI extends JFrame {
    private JTextField textField;
    private JTextArea textArea;
    private Connection connection;
    private JRadioButton shortNameButton;
    private JRadioButton longNameButton;
    private JCheckBox exactMatchCheckBox;
    private ButtonGroup buttonGroup;

    public RouteSearchGUI() {
        initializeDatabaseConnection();

        // Frame settings
        setTitle("Route Search");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Text field for input
        textField = new JTextField(20);
        JButton button = new JButton("Search");

        // Text area for results
        textArea = new JTextArea(10, 25);
        textArea.setEditable(false);

        // Radio buttons for selecting search type
        shortNameButton = new JRadioButton("Short Name Search", true);  // Default selected
        longNameButton = new JRadioButton("Long Name Search");
        buttonGroup = new ButtonGroup();
        buttonGroup.add(shortNameButton);
        buttonGroup.add(longNameButton);

        // Checkbox for exact match
        exactMatchCheckBox = new JCheckBox("Exact Match");

        // Layout
        JPanel panel = new JPanel();
        panel.add(textField);
        panel.add(button);
        panel.add(shortNameButton);
        panel.add(longNameButton);
        panel.add(exactMatchCheckBox);
        panel.add(new JScrollPane(textArea));

        // Add panel to frame
        add(panel);

        // Button click event
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String destination = textField.getText();
                searchRoutes(destination);
            }
        });

        setVisible(true);
    }

    private void initializeDatabaseConnection() {
        try {
            List<String> lines = Files.readAllLines(Paths.get("src/java/Database/local-credentials.txt"));
            if (!lines.isEmpty()) {
                String[] credentials = lines.get(0).split(", ");
                String host = credentials[0];
                String port = credentials[1];
                String databaseName = credentials[2];
                String user = credentials[3];
                String password = credentials[4];

                String url = "jdbc:mysql://" + host + ":" + port + "/" + databaseName;
                connection = DriverManager.getConnection(url, user, password);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to initialize database connection: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void searchRoutes(String destination) {
        String columnToSearch = longNameButton.isSelected() ? "route_long_name" : "route_short_name";
        String query;
        if (exactMatchCheckBox.isSelected()) {
            query = "SELECT route_id, route_short_name, route_long_name FROM routes WHERE " + columnToSearch + " = ?";
        } else {
            query = "SELECT route_id, route_short_name, route_long_name FROM routes WHERE " + columnToSearch + " LIKE ?";
        }

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            if (exactMatchCheckBox.isSelected()) {
                stmt.setString(1, destination);
            } else {
                stmt.setString(1, "%" + destination + "%");
            }
            try (ResultSet rs = stmt.executeQuery()) {
                StringBuilder results = new StringBuilder();
                while (rs.next()) {
                    results.append("ID: ").append(rs.getString("route_id"))
                            .append(", Short Name: ").append(rs.getString("route_short_name"))
                            .append(", Long Name: ").append(rs.getString("route_long_name"))
                            .append("\n");
                }
                textArea.setText(results.toString());
                if (results.toString().isEmpty()) {
                    textArea.setText("No routes found.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            textArea.setText("Database error.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RouteSearchGUI());
    }
}
