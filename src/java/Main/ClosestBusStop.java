package src.java.Main;

import static src.java.Main.CalculateDistance.findMidpoint;

import java.sql.SQLException;
import java.util.ArrayList;

import src.java.Database.DatabaseController;

public class ClosestBusStop {
    public static void main(String[] args) throws SQLException{
        ArrayList<Double> list = new ArrayList<>();
        list.add(4.854370);
        list.add(52.453042);
        ClosestBusStop finder = new ClosestBusStop();
        finder.findClosestBusStop(list);
    }

    public String findClosestBusStop(ArrayList<Double> latLong) throws SQLException  {
        DatabaseController databaseController = new DatabaseController();
        Double lat = latLong.get(0);
        Double lon = latLong.get(1);
        Double searchFactor = 1.0;
        ArrayList<String> list = null;
        //TODO: add function that increases the search radius depending on if it returns any results
        for (int i = 0; i < 1000; i++) {
            list = databaseController.executeFetchQuery(
            "SELECT stop_id, stop_lon, stop_lat FROM stops WHERE stop_lon BETWEEN " + (lat - 0.01000 * searchFactor) + " AND " + (lat + 0.01000 * searchFactor) +
            " AND stop_lat BETWEEN " + (lon - 0.01000 * searchFactor) + " AND " + (lon + 0.01000 * searchFactor)
            );
            if (list.size() > 1);
                searchFactor -= 0.1;
            
            if (list.size() == 1) {
                break;
            }

            else {
                searchFactor += 0.1;
            }
        }

        for (String string : list) {
            System.out.println(string);
        }
        String[] parts = list.get(0).split(";");
        System.out.println(parts[0]);
        return parts[0];
    }
}