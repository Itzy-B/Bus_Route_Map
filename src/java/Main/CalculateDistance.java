package src.java.Main;

import com.microsoft.schemas.office.visio.x2012.main.CellType;

import src.java.API.RetrievePostalWithAPI;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import src.java.GUI.Data;
import src.java.GUI.Place;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

public class CalculateDistance {
    private static final int EARTH_RADIUS = 6371;

    /**
     * Calculates the distance between two zip codes using the Haversine formula.
     * If the zip codes are not present in the data, an API call is made to retrieve the coordinates.
     * @param p1 the first zip code
     * @param p2 the second zip code
     * @return the distance between the two zip codes in kilometers or meters, depending on the distance
     */
    public static double getDistance(String p1, String p2) throws IOException {
        //Initialize data
        Data data = new Data();
        data.getData();

        double distance = 0;
        //Get LatLong Arrays from data class
        ArrayList<Double> latLong1 = data.getLatLong(p1);
        ArrayList<Double> latLong2 = data.getLatLong(p2);
        //Calculate distance between
        distance = distanceBetween(latLong1.get(0), latLong1.get(1), latLong2.get(0), latLong2.get(1));
        // Format to two decimal places
        DecimalFormat df = new DecimalFormat("#.##");
        distance = Double.parseDouble(df.format(distance));

        if(distance >= 1){
            return distance;
        }
        else{
            return distance * 100;
        }

    }

    /**
     * Calculates the distance between two points on the earth.
     *
     * @param lat1 the latitude of the first point
     * @param lon1 the longitude of the first point
     * @param lat2 the latitude of the second point
     * @param lon2 the longitude of the second point
     * @return the distance between the two points in kilometers
     */
    public static double distanceBetween(double lat1, double lon1, double lat2, double lon2) {

        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);
        double lat2Rad = Math.toRadians(lat2);
        double lon2Rad = Math.toRadians(lon2);

        // Calculate the change in coordinates
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        // Calculate the distance using the Haversine formula
        double a = Math.pow(Math.sin(deltaLat / 2), 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.pow(Math.sin(deltaLon / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }

    /**
     * Calculates the midpoint between two points on the earth.
     *
     * @param p1 the first point
     * @param p2 the second point
     * @return the midpoint between the two points as an ArrayList of two Doubles, where the first element is the latitude and the second element is the longitude
     * @throws IOException if there is an error retrieving the coordinates from the data file or making an API call
     */
    public static ArrayList<Double> findMidpoint(Place p1, Place p2) throws IOException {
        ArrayList<Double> midpoint = new ArrayList<>();

        double lat1 = p1.getLatitude();
        double lon1 = p1.getLongitude();
        double lat2 = p2.getLatitude();
        double lon2 = p2.getLongitude();

        double dLon = Math.toRadians(lon2 - lon1);

        // Convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);

        // Intermediate point
        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2),
                Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

        // Convert back to degrees
        midpoint.add(Math.toDegrees(lat3));
        midpoint.add(Math.toDegrees(lon3));

        return midpoint;
    }


    public static String printDistance(String p1, String p2) throws IOException {
        double distance = getDistance(p1, p2);
        if (distance >= 1) {
             return distance + " Kilometers";
        } else {
             return distance + " Meters";
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println(printDistance("6222CN", "6213HD"));
    }


}
