package src.java.Main;

import com.microsoft.schemas.office.visio.x2012.main.CellType;
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

    public static void getData() {
        boolean isFirstRow = true; // Flag to indicate the first row
        try (FileInputStream fis = new FileInputStream("resources/MassZipLatLon.xlsx");
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
                                // Handle unexpected cell type
                            }
                            break;
                        case 2: // Longitude Column
                            if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
                                longitude.add(cell.getNumericCellValue());
                            } else {
                                // Handle unexpected cell type
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

    public static String getDistance(String p1, String p2) {
        getData();
        Collections.sort(zipCodes);
        int indexP1 = Collections.binarySearch(zipCodes, p1);
        int indexP2 = Collections.binarySearch(zipCodes, p2);
        double distance = 0;
        if (indexP1 >= 0 && indexP2 >= 0) {
            double lat1Rad = Math.toRadians(latitude.get(indexP1));
            double lon1Rad = Math.toRadians(longitude.get(indexP1));
            double lat2Rad = Math.toRadians(latitude.get(indexP2));
            double lon2Rad = Math.toRadians(longitude.get(indexP2));

            // Calculate the change in coordinates
            double deltaLat = lat2Rad - lat1Rad;
            double deltaLon = lon2Rad - lon1Rad;

            // Calculate the distance using the Haversine formula
            double a = Math.pow(Math.sin(deltaLat / 2), 2) +
                    Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                            Math.pow(Math.sin(deltaLon / 2), 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            distance = EARTH_RADIUS * c;


        } else {
            //API Call
        }
        DecimalFormat df = new DecimalFormat("#.##"); // Format to two decimal places
        distance = Double.parseDouble(df.format(distance));
        if(distance >= 1){
            return distance + " Kilometers";
        }
        else{
            return distance * 100 + " meters";
        }

    }


}
