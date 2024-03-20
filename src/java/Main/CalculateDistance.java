package src.java.Main;

import com.microsoft.schemas.office.visio.x2012.main.CellType;

import src.java.API.RetrievePostalWithAPI;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

public class CalculateDistance {
    private static ArrayList<String> zipCodes = new ArrayList<String>();
    private static ArrayList<Double> latitude = new ArrayList<Double>();
    private static ArrayList<Double> longitude = new ArrayList<Double>();
    private static final int EARTH_RADIUS = 6371;

    /**
     * Reads the data from the Excel file and stores it in the zipCodes and latitude arrays.
     */
    public static void getData() {
        boolean isFirstRow = true; // Flag to indicate the first row
        try (FileInputStream fis = new FileInputStream("src/java/Resources/MassZipLatLon.xlsx");
             XSSFWorkbook wb = new XSSFWorkbook(fis)) {

            XSSFSheet sheet = wb.getSheetAt(0);
            for (Row row : sheet) {
                if (isFirstRow) {
                    isFirstRow = false;
                    continue; // Skip processing the first row
                }
                for (Cell cell : row) {
                    switch (cell.getColumnIndex()) {
                        case 0: // Zip Code Column
                            zipCodes.add(cell.getStringCellValue());
                            break;
                        case 1: // Latitude Column
                            if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                latitude.add(cell.getNumericCellValue());
                            } else {
                                throw new IllegalArgumentException("Latitude column must be numeric");
                            }
                            break;
                        case 2: // Longitude Column
                            if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                longitude.add(cell.getNumericCellValue());
                            } else {
                                throw new IllegalArgumentException("Longitude column must be numeric");
                            }
                            break;
                        default:
                            // Handle other columns if needed
                    }
                }
            }
        } catch (IOException e) {
            // Handle file IO error
            e.printStackTrace();
        }
    }

    /**
     * Calculates the distance between two zip codes using the Haversine formula.
     * If the zip codes are not present in the data, an API call is made to retrieve the coordinates.
     * @param p1 the first zip code
     * @param p2 the second zip code
     * @return the distance between the two zip codes in kilometers or meters, depending on the distance
     */
    public static double getDistance(String p1, String p2) throws IOException {
        getData(); // Initialize the data arrays
        double distance = 0;

        Collections.sort(zipCodes); // Sort the zip codes

        int indexP1 = Collections.binarySearch(zipCodes, p1); //Finding the index of the zip code with a binary search
        int indexP2 = Collections.binarySearch(zipCodes, p2);

        //If both zip codes are present in the data array we calculate the distance between them
        if (indexP1 >= 0 && indexP2 >= 0) {
            distance = distanceBetween(latitude.get(indexP1), longitude.get(indexP1), latitude.get(indexP2), longitude.get(indexP2));
        }
        //If either of the zip codes are not present we make an API call and find the latitude and longitude from there
        else {
            //API call
            RetrievePostalWithAPI api = new RetrievePostalWithAPI();
            ArrayList<Double> latLong = api.getPCode(p1);
            if (latLong.size() > 0) {
                distance = distanceBetween(latLong.get(0), latLong.get(1), latLong.get(2), latLong.get(3));
            }

            else {
                //TODO: restore this line after done debugging, throw new IllegalArgumentException("Invalid postal code or postal code in not in database");
            }
        }
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


    public static String printDistance(String p1, String p2) throws IOException {
        double distance = getDistance(p1, p2);
        if (distance >= 1) {
            // return distance + " Kilometers";
        } else {
            // return distance + " Meters";
        }
        return "";
    }


}
