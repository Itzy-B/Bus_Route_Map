package src.java.Main;

import java.util.ArrayList;

import com.mysql.cj.jdbc.exceptions.SQLError;

import src.java.Database.DatabaseController;
import src.java.Main.ClosestBusStop.BusStop;

public class BusRouteFinder {
    public void getShapes(ArrayList<Double> depCoords, ArrayList<Double> desCoords) throws Exception {
        ClosestBusStop stopFinder = new ClosestBusStop();
        ArrayList<BusStop> busStopsDep = new ArrayList<>();
        ArrayList<BusStop> busStopsDes = new ArrayList<>();
        BusRouteFinder tripFinder = new BusRouteFinder();
        int tripId = -1;

        try {
            busStopsDep = stopFinder.findClosestBusStop(depCoords);
            busStopsDes = stopFinder.findClosestBusStop(desCoords);
            tripId = tripFinder.getTripId(busStopsDes, busStopsDep);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (busStopsDep.isEmpty()) {
            throw new IllegalArgumentException("busStops array is empty");
        }

        if (tripId == -1) {
            throw new IllegalArgumentException("tripId is null");
        }

        int shapeId = getShapeId(tripId);
        ArrayList<String> shapes = getShapes(shapeId);
    }
    public static void main(String[] args) throws Exception {
        BusRouteFinder finder =  new BusRouteFinder();
        ArrayList<Double> departureCoords = new ArrayList<>();
        ArrayList<Double> destinationCoords = new ArrayList<>();
        departureCoords.add(5.6628118700314545);
        departureCoords.add( 50.857339137628095);
        destinationCoords.add(5.807836);
        destinationCoords.add( 50.857729);
        finder.getShapes(departureCoords,destinationCoords);
    }

    public ArrayList<String> getShapes(int shapeId) throws Exception {
        DatabaseController databaseController = new DatabaseController();
        ArrayList<String> list = databaseController.executeFetchQuery(
        "SELECT shape_id, shape_pt_lat, shape_pt_lon FROM shapes WHERE shape_id = " + shapeId
        );
        return list;

    }

    public int getShapeId(int tripId) throws Exception{
        DatabaseController databaseController = new DatabaseController();
        ArrayList<String> list = databaseController.executeFetchQuery(
        "SELECT shape_id FROM trips WHERE trip_id = " + tripId
        );

        return Integer.parseInt(list.get(0).split(":")[1].split(";")[0].split(" ")[1]);
    }

    public int getTripId(ArrayList<BusStop> busStopDep, ArrayList<BusStop> busStopDes) throws Exception {
        DatabaseController databaseController = new DatabaseController();
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
