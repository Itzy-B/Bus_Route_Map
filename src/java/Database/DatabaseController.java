package src.java.Database;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseController {
    private Connection connection;

    public DatabaseController() {
        initializeDatabaseConnection();
    }

    private void initializeDatabaseConnection() {
        try {
            List<String> lines = Files.readAllLines(Paths.get("src/java/Database/credentials.txt"));
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
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize database connection: " + e.getMessage());
        }
    }

    public ArrayList<String> executeFetchQuery(String query) throws SQLException {
        if (!query.trim().toLowerCase().startsWith("select")) {
            throw new IllegalArgumentException("Only SELECT queries are allowed.");
        }

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            ArrayList<String> results = new ArrayList<>();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                StringBuilder row = new StringBuilder();
                for (int i = 1; i <= columnCount; i++) {
                    row.append(metaData.getColumnName(i)).append(": ").append(rs.getString(i)).append("; ");
                }
                results.add(row.toString());
            }
            return results;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error executing query: " + e.getMessage(), e);
        }
    }
}
