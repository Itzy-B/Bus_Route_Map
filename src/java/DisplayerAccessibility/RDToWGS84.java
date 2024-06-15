package src.java.DisplayerAccessibility;

import java.awt.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

//AI ofcourse, I am not a genius, and somehow this works
public class RDToWGS84 {
    public static double[] convertRDToWGS84(double x, double y) {
        double dX = (x - 155000) * Math.pow(10, -5);
        double dY = (y - 463000) * Math.pow(10, -5);

        double sumN = (3235.65389 * dY) + (-32.58297 * Math.pow(dX, 2)) + (-0.2475 * Math.pow(dY, 2)) + 
                      (-0.84978 * Math.pow(dX, 2) * dY) + (-0.0655 * Math.pow(dY, 3)) + 
                      (-0.01709 * Math.pow(dX, 2) * Math.pow(dY, 2)) + (-0.00738 * dX) + 
                      (0.0053 * Math.pow(dX, 4)) + (-0.00039 * Math.pow(dX, 2) * Math.pow(dY, 3)) + 
                      (0.00033 * Math.pow(dX, 4) * dY) + (-0.00012 * dX * dY);

        double sumE = (5260.52916 * dX) + (105.94684 * dX * dY) + (2.45656 * dX * Math.pow(dY, 2)) + 
                      (-0.81885 * Math.pow(dX, 3)) + (0.05594 * dX * Math.pow(dY, 3)) + 
                      (-0.05607 * Math.pow(dX, 3) * dY) + (0.01199 * dY) + 
                      (-0.00256 * Math.pow(dX, 3) * Math.pow(dY, 2)) + (0.00128 * dX * Math.pow(dY, 4)) + 
                      (0.00022 * Math.pow(dY, 2)) + (-0.00022 * Math.pow(dX, 2)) + 
                      (0.00026 * Math.pow(dX, 5));

        double latitude = 52.15517 + (sumN / 3600);
        double longitude = 5.387206 + (sumE / 3600);

        return new double[]{latitude, longitude};
    }

    public static void main(String[] args) {
        ArrayList<String> file = readFileLineByLine("./polygons.csv");
        for (String string: file) {
            if (string.length() < 8) {
                try {
                    writeCoordinatesToCSV(string.split(",")[0], null, "coordinates.csv");
                } catch (IOException e) {
                    System.out.println("excpetions");
                    e.printStackTrace();
                }
                continue;
            }
            ArrayList<Double[]> list = parsePolygon(string.substring(7), string.split(",")[0]);
            try {
                writeCoordinatesToCSV(string.split(",")[0], list, "coordinates.csv");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("");
        // writeCoordinatesToCSV(latLon, new File("polys.csv"));
        // System.out.println("Latitude: " + latLon[0]);
        // System.out.println("Longitude: " + latLon[1]);
    }

    public static ArrayList<ArrayList<Double[]>> getPolyGon() {
        ArrayList<ArrayList<Double[]>> polygons = new ArrayList<>();
        ArrayList<String> lines = readFileLineByLine("./coordinates.csv");
        
        for (String line : lines) {
            ArrayList<Double[]> polygon = new ArrayList<>();
            String[] parts = line.split("-");
            
            if (parts.length != 2) {
                // Add a single pair of -1 coordinates
                polygon.add(new Double[]{-1.0, -1.0});
            } else {
                String coordinatesString = parts[1];
                String[] coordinatePairs = coordinatesString.split(";");
                
                for (String pair : coordinatePairs) {
                    String[] latLng = pair.split(",");
                    if (latLng.length != 2) {
                        continue;
                    }
                    Double latitude = Double.parseDouble(latLng[0]);
                    Double longitude = Double.parseDouble(latLng[1]);
                    polygon.add(new Double[]{latitude, longitude});
                }
            }
            polygons.add(polygon);
        }
        
        return polygons;
    }

    public static ArrayList<Double[]> parsePolygon(String polygonString, String postalCode) {
        ArrayList<Double[]> coordinates = new ArrayList<>();

        // Remove "POLYGON((" prefix and "))" suffix
        polygonString = polygonString.replace("MULTIPOLYGON(((", "").replace("))", "").replace("((","").replace("))","");
        polygonString = polygonString.replace("POLYGON", "").replace(")", "");
        polygonString = polygonString.replace("(","");


        // Ensure the string starts with "(" and ends with ")"

        // Split the string into individual coordinate pairs
        // Using regex to account for variations in spacing and 
        if (polygonString.isEmpty()) {
            System.out.println(postalCode);
        }
        String[] coordinatePairs = polygonString.substring(1, polygonString.length() - 1).split("\\s*,\\s*");

        // Convert each coordinate pair from RD to WGS84 and add to the list
        for (String pair : coordinatePairs) {
            String[] coords = pair.split("\\s+");
            try {
                double rdX = Double.parseDouble(coords[0]);
                double rdY = Double.parseDouble(coords[1]);
                double[] wgs84 = convertRDToWGS84(rdX, rdY); // Assuming this method is correctly implemented elsewhere
                coordinates.add(new Double[]{wgs84[0], wgs84[1]});
            } catch (NumberFormatException e) {
                System.err.println("Error parsing coordinate pair: " + pair);
                e.printStackTrace();
            }
        }

        return coordinates;
    }

    public static void writeCoordinatesToCSV(String postalCode, ArrayList<Double[]> coordinates, String filename) throws IOException {
        StringBuilder sb = new StringBuilder();
        
        sb.append(postalCode).append("-");
        if (!(coordinates == null)) {
            for (Double[] coord : coordinates) {
                sb.append(coord[0]).append(",").append(coord[1]).append(";");
            }
        }

        // Remove trailing comma and append newline character
        sb.deleteCharAt(sb.length() - 1).append("\n");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.write(sb.toString());
        }
    }

    public static ArrayList<String> readFileLineByLine(String filePath) {
        ArrayList<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine())!= null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }
}