package src.java.Main;

import static src.java.Main.CalculateDistance.getDistance;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.mysql.cj.jdbc.Driver;

public class ClosestBusStop {
        public String findClosestBus(ArrayList<Double> latLong) throws ClassNotFoundException  {
            connectToDatabase();
            return "";
        }

        //Sucks very much, we need to create a Database manager, not do it in the class itself.
        public void connectToDatabase() {
            String host, port, databaseName, userName, password;
            host = port = databaseName = userName = password = null;
                try {
                        // Replace "credentials.txt" with your actual text file name
                        List<String> lines = Files.readAllLines(Paths.get("src/java/Database/credentials.txt"));
                        if (lines.size() > 0) {
                            String[] config = lines.get(0).split(", ");
                            host = config[0];
                            port = config[1];
                            databaseName = config[2];
                            userName = config[3];
                            password = config[4];
                        }
                    } catch (Exception e) {
                        System.out.println("Failed to read configuration from file.");
                        e.printStackTrace();
                        return;
                    }
            
                    if (host == null || port == null || databaseName == null) {
                        System.out.println("Host, port, and database information are required");
                        return;
                    }
            
                    // Manually registering the JDBC driver
                    try {
                        DriverManager.registerDriver(new Driver());
                    } catch (SQLException e) {
                        System.out.println("Failed to register MySQL JDBC driver");
                        e.printStackTrace();
                        return;
                    }
            
                    try (Connection connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + databaseName, userName, password);
                         Statement statement = connection.createStatement();
                         ResultSet resultSet = statement.executeQuery("SELECT * FROM shapes LIMIT 10")) {
            
                        while (resultSet.next()) {
                            String shapeId = resultSet.getString("shape_id");
                            int shapePtSequence = resultSet.getInt("shape_pt_sequence");
                            double shapePtLat = resultSet.getDouble("shape_pt_lat");
                            double shapePtLon = resultSet.getDouble("shape_pt_lon");
                            double shapeDistTraveled = resultSet.getDouble("shape_dist_traveled");
            
                            System.out.println("Shape ID: " + shapeId + ", Sequence: " + shapePtSequence +
                                    ", Latitude: " + shapePtLat + ", Longitude: " + shapePtLon +
                                    ", Distance Traveled: " + shapeDistTraveled);
                        }
                    } catch (SQLException e) {
                        System.out.println("Connection failure.");
                        e.printStackTrace();
                    }
                }
            }