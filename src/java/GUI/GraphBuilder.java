package src.java.GUI;

import java.sql.*;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class GraphBuilder {
    private final Graph g;

    public GraphBuilder(Graph g) {this.g = g;}

    public void getBusStops() throws SQLException {
        String query = "SELECT stop_id, stop_name, stop_lat, stop_lon, trip_id, stop_sequence, arrival_time, departure_time, trip_headsign, shape_dist_traveled, shape_id " +
                "FROM maas_stops_time " +
                "WHERE stop_name LIKE '%Maastricht,%' AND trip_id NOT IN (SELECT trip_id FROM maas_stops_time mst WHERE arrival_time LIKE '24:%' OR arrival_time LIKE '25:%') " +
                "ORDER BY trip_id, stop_sequence; ";

        BusStop previousStop = null;
        int previousTripId = -1;
        Trip prevTrip = null;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int stopId = rs.getInt("stop_id");
                String stopName = rs.getString("stop_name");
                double stopLat = rs.getDouble("stop_lat");
                double stopLon = rs.getDouble("stop_lon");
                int tripId = rs.getInt("trip_id");
                int stopSequence = rs.getInt("stop_sequence");
                String arriveTime = rs.getString("arrival_time");
                String departureTime = rs.getString("departure_time");
                String tripHeadSign = rs.getString("trip_headsign");
                int shapeDistTraveled = rs.getInt("shape_dist_traveled");
                int shapeId = rs.getInt("shape_id");

                BusStop currentStop = new BusStop(stopId, stopName, stopLat, stopLon);
                Trip currTrip = new Trip(tripId, convertToLocalTime(arriveTime), convertToLocalTime(departureTime), tripHeadSign, shapeDistTraveled, shapeId);
                g.addVertex(currentStop);

                if (previousStop != null && prevTrip != null && tripId == previousTripId) {
                    LocalTime aTime = convertToLocalTime(arriveTime);
                    LocalTime dTime = prevTrip.getDepartureTime();
                    int distance = shapeDistTraveled - prevTrip.getShapeDistTraveled();
                    Trip trip = new Trip(tripId, aTime, dTime, tripHeadSign, distance, shapeId);
                    g.addEdge(previousStop, currentStop, trip, tripHeadSign);
                }

                previousStop = currentStop;
                previousTripId = tripId;
                prevTrip = currTrip;
            }
        }
    }

    // convert time string to LocalTime
    public static LocalTime convertToLocalTime(String timeStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return LocalTime.parse(timeStr, formatter);
    }

    // calculate the difference in minutes between two time strings
    public static long calculateTimeDifference(LocalTime startTime, LocalTime endTime) {
        Duration duration = Duration.between(startTime, endTime);
        return duration.toMinutes();
    }

    public static void main(String[] args) throws SQLException {
        Graph g = new Graph();
        GraphBuilder graphBuilder = new GraphBuilder(g);
        graphBuilder.getBusStops();

        // Print the graph to verify
        /*BusStop busStop1 = new BusStop(2578367, "Maastricht, Calvariestraat", 50.847532, 5.680894);
        System.out.println("Edges for " + busStop1 + ":");
        for (Edge edge : g.getEdges(busStop1)) {
            System.out.println(edge);
        }*/

        BusStop busStop2 = new BusStop(2578289, "Maastricht, Aramislaan", 50.83938, 5.673559);
        System.out.println("Edges for " + busStop2 + ":");
        for (Edge edge : g.getEdges(busStop2)) {
            System.out.println(edge);
        }

        System.out.println(g.getVertices().size());
    }

}