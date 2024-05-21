package src.java.Main;

import java.util.List;
import java.util.ArrayList;

import src.java.GUI.Place;
import src.java.Database.DatabaseController;
import src.java.Main.ClosestBusStop.BusStop;

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
            tripId = tripFinder.getTripId(busStopsDes, busStopsDep, databaseController);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (busStopsDep.isEmpty()) {
            throw new IllegalArgumentException("busStops array is empty");
        }

        if (tripId == -1) {
            throw new IllegalArgumentException("tripId is null");
        }

        int shapeId = getShapeId(tripId, databaseController);
        ArrayList<String> shapes = getShapes(shapeId, databaseController);
        List<Place> list = new ArrayList<>();
        for (String string: shapes) {
            double lon = Double.parseDouble(string.split(";")[1].split(":")[1].split(" ")[1]);
            double lat = Double.parseDouble(string.split(";")[2].split(":")[1].split(" ")[1]);
            Place place = new Place(lon, lat);
            list.add(place);
        }
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

    public int getTripId(ArrayList<BusStop> busStopDep, ArrayList<BusStop> busStopDes,  DatabaseController databaseController) throws Exception {
        String stopId = busStopDes.get(0).getStopId().split(":")[1];
        String stopId2 = busStopDep.get(0).getStopId().split(":")[1];

        //Get trip_ids from overlapping stopId's if they exist
        //TODO: increase performance of this, wait too slow
        ArrayList<String> list = databaseController.executeFetchQuery ( 
            "SELECT DISTINCT s1.trip_id " +
            "FROM stop_times s1 " +
            "JOIN stop_times s2 ON s1.trip_id = s2.trip_id " +
            "WHERE s1.stop_id = " + stopId +" AND s2.stop_id = " + stopId2
        );

        return Integer.parseInt(list.get(0).split(":")[1].split(";")[0].split(" ")[1]);
    }
}
