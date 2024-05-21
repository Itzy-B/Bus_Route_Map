package src.java.Main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import src.java.Database.DatabaseController;

public class ClosestBusStop {
    public static void main(String[] args) throws Exception {
        ArrayList<Double> list = new ArrayList<>();
        list.add(4.854370);
        list.add(52.453042);
        ClosestBusStop finder = new ClosestBusStop();
        finder.findClosestBusStop(list);
    }

    //Reference the method that is already in the code instead of this one
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        final int R = 6371; // Radius of the earth in kilometers
        return R * c * 1000; // Convert to meters
    }

    public ArrayList<BusStop> findClosestBusStop(ArrayList<Double> latLong) throws Exception {
        DatabaseController databaseController = new DatabaseController();
        Double lat = latLong.get(0);
        Double lon = latLong.get(1);
        double broaderRange = 0.01; //Search range for initial query;

        ArrayList<String> list = databaseController.executeFetchQuery(
                "SELECT stop_id, stop_lon, stop_lat FROM stops WHERE stop_lon BETWEEN " + (lat - broaderRange) + " AND " + (lat + broaderRange) +
                        " AND stop_lat BETWEEN " + (lon - broaderRange) + " AND " + (lon + broaderRange)
        );

        ArrayList<BusStop> busStops = new ArrayList<>();
        for (String row : list) {
            String[] parts = row.split(";");
            String stopId = parts[0];
            double stopLon = Double.parseDouble(parts[1].split(":")[1]);
            double stopLat = Double.parseDouble(parts[2].split(":")[1]);
            double distance = calculateDistance(lat, lon, stopLat, stopLon);
            busStops.add(new BusStop(stopId, stopLat, stopLon, distance));
        }

        //Change this function later to make it more readable
        Collections.sort(busStops, Comparator.comparingDouble(BusStop::getDistance));

        // Return the ID of the closest bus stop
        return busStops;
    }

    class BusStop {
        private String stopId;
        private double lat;
        private double lon;
        private double distance;

        public BusStop(String stopId, double lat, double lon, double distance) {
            this.stopId = stopId;
            this.lat = lat;
            this.lon = lon;
            this.distance = distance;
        }

        public String getStopId() {
            return stopId;
        }

        public double getDistance() {
            return distance;
        }
    }
}