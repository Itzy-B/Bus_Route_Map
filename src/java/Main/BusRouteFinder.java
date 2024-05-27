package src.java.Main;

import java.util.List;

import javafx.scene.control.Alert.AlertType;

import java.util.ArrayList;

import src.java.GUI.Place;
import src.java.Database.DatabaseController;
import src.java.Main.ClosestBusStop.BusStop;
import src.java.Singletons.ExceptionManager;

public class BusRouteFinder {
    public List<Place> getShapes(ArrayList<Double> depCoords, ArrayList<Double> desCoords, DatabaseController databaseController) throws Exception {
        ClosestBusStop stopFinder = new ClosestBusStop();
        ArrayList<BusStop> busStopsDep = new ArrayList<>();
        ArrayList<BusStop> busStopsDes = new ArrayList<>();
        BusRouteFinder tripFinder = new BusRouteFinder();
        int tripId = -1;

        try {
            busStopsDep = stopFinder.findClosestBusStop(depCoords, databaseController);
            busStopsDes = stopFinder.findClosestBusStop(desCoords, databaseController);
            ArrayList<Trip> tripsList = tripFinder.getTripId(busStopsDes, busStopsDep, databaseController);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (busStopsDep.isEmpty()) {
            throw new IllegalArgumentException("busStops array is empty");
        }

        if (tripId == -1) {
            ExceptionManager.showError("TripId error", "Problem", "Cannot find valid tripId, just walk", AlertType.ERROR);
            throw new IllegalArgumentException("tripId is null");
        }

        // int shapeId = getShapeId(tripId, databaseController);
        // ArrayList<String> shapes = getShapes(shapeId, databaseController);
        List<Place> list = new ArrayList<>();
        // for (String string: shapes) {
        //     double lon = Double.parseDouble(string.split(";")[1].split(":")[1].split(" ")[1]);
        //     double lat = Double.parseDouble(string.split(";")[2].split(":")[1].split(" ")[1]);
        //     Place place = new Place(lon, lat);
        //     list.add(place);
        // }
        System.out.println("");
        return list;
    }
    public static void main(String[] args) throws Exception {
        BusRouteFinder finder = new BusRouteFinder();
        ArrayList<Double> departureCoords = new ArrayList<>();
        ArrayList<Double> destinationCoords = new ArrayList<>();
        departureCoords.add(5.6628118700314545);
        departureCoords.add( 50.857339137628095);
        destinationCoords.add(5.807836);
        destinationCoords.add( 50.857729);
        DatabaseController databaseController = new DatabaseController();
        finder.getShapes(departureCoords,destinationCoords, databaseController);
    }

    public ArrayList<String> getShapes(int shapeId, DatabaseController databaseController ) throws Exception {
        ArrayList<String> list = databaseController.executeFetchQuery(
        "SELECT shape_id, shape_pt_lat, shape_pt_lon FROM shapes WHERE shape_id = " + shapeId
        );
        return list;

    }

    public int getShapeId(int tripId, DatabaseController databaseController ) throws Exception{
        ArrayList<String> list = databaseController.executeFetchQuery(
        "SELECT shape_id FROM trips WHERE trip_id = " + tripId
        );

        return Integer.parseInt(list.get(0).split(":")[1].split(";")[0].split(" ")[1]);
    }

    public ArrayList<Trip> getTripId(List<BusStop> busStopDep, List<BusStop> busStopDes,  DatabaseController databaseController) throws Exception {
        ArrayList<String> list = null;
        ArrayList<Trip> trips = null;
        //Gets all overlapping trips between the bus stop lists
        for (int x = 0; x < busStopDes.size(); x++) {
            for (int y = 0; y < busStopDep.size(); y++) {
                list = databaseController.executeFetchQuery ( 
                    "SELECT DISTINCT s1.trip_id " +
                    "FROM stop_times s1 " +
                    "JOIN stop_times s2 ON s1.trip_id = s2.trip_id " +
                    "WHERE s1.stop_id = " + busStopDes.get(x).getStopId().split(":")[1] +" AND s2.stop_id = " + busStopDep.get(y).getStopId().split(":")[1]
                );
                /* cast trip_id and stop_id to int and not to varchar, so that indexes actually make sense
                * Sort on trip_ids, then find the sortest one. Create function to calculate the length of it. Etc..
                */
                if (!list.isEmpty()) {
                    System.out.println("");
                }
                for (String string: list) {
                    Trip trip = new Trip(Integer.parseInt(string.split(":")[1].split(";")[0].split(" ")[1]));
                    trips.add(trip);
                }
            }
        }
        return trips;
    }
}
