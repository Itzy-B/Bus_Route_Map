package src.java.GUI;

import src.java.Main.CalculateDistance;

import java.sql.*;
import java.util.*;

public class GraphBuilder {
    private final Graph g;

    public GraphBuilder(Graph g) {this.g = g;}

    public void getBusStops() throws SQLException {
        /*String query = "SELECT stop_id, stop_name, stop_lat, stop_lon, trip_id, stop_sequence, trip_headsign, shape_dist_traveled " +
                "FROM maastricht_stops " +
                "WHERE trip_id >= 178414559 " +
                "ORDER BY trip_id, stop_sequence; ";*/

        String query = "SELECT stop_id, stop_name, stop_lat, stop_lon, trip_id, stop_sequence, trip_headsign, shape_dist_traveled " +
                "FROM maastricht_stops " +
                "WHERE stop_name LIKE '%Maastricht,%' " +
                "ORDER BY trip_id, stop_sequence ";
        Map<Integer, BusStop> busStopMap = new HashMap<>();
        Map<Integer, Set<String>> stopHeadsignsProcessed = new HashMap<>();

        BusStop previousStop = null;
        int previousTripId = -1;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int stopId = rs.getInt("stop_id");
                String stopName = rs.getString("stop_name");
                double stopLat = rs.getDouble("stop_lat");
                double stopLon = rs.getDouble("stop_lon");
                int tripId = rs.getInt("trip_id");
                String tripHeadsign = rs.getString("trip_headsign");
                int stopSequence = rs.getInt("stop_sequence");
                double shapeDistTraveled = rs.getDouble("shape_dist_traveled");

                BusStop currentStop = busStopMap.get(stopId);
                if (currentStop == null) {
                    currentStop = new BusStop(stopId, stopName, stopLat, stopLon);
                    busStopMap.put(stopId, currentStop);
                    g.addVertex(currentStop);
                }

                currentStop.addTrip(tripId, tripHeadsign, stopSequence, shapeDistTraveled);

                stopHeadsignsProcessed.putIfAbsent(stopId, new HashSet<>());

                // Skip processing if this headsign has already been processed for this stop
                if (stopHeadsignsProcessed.get(stopId).contains(tripHeadsign)) {
                    continue;
                }

                // Connect the current stop with the previous stop in the same trip
                if (previousStop != null && tripId == previousTripId) {
                    double distance = shapeDistTraveled - previousStop.getTrips()
                            .get(previousStop.getTrips().size() - 1)
                            .getShapeDistTraveled();
                    if (distance == 0) {
                        distance = CalculateDistance.distanceBetween(stopLat, stopLon, previousStop.lat, previousStop.lon);
                    }
                    g.addEdge(previousStop, currentStop, distance, tripHeadsign);
                    stopHeadsignsProcessed.get(stopId).add(tripHeadsign);
                }

                previousStop = currentStop;
                previousTripId = tripId;
            }
        }
    }


    public static void main(String[] args) throws SQLException {
        Graph g = new Graph();
        GraphBuilder graphBuilder = new GraphBuilder(g);
        graphBuilder.getBusStops();

        // Print the graph to verify
        /*BusStop busStop1 = new BusStop(2578367, "Maastricht, Calvariestraat", 50.847532, 5.680894);
        for (Edge edge : g.getEdges(busStop1)) {
            System.out.println(edge);
        }*/

        /*BusStop busStop2 = new BusStop(2578389, "Maastricht, Mosae Forum/Centrum", 50.852559, 5.693933);
        for (Edge edge : g.getEdges(busStop2)) {
            System.out.println(edge);
        }*/

        for (BusStop busStop : g.findNearestBusStops(50.8481233263157, 5.68969885999999)) {
            System.out.println(busStop);
        }
    }

}

