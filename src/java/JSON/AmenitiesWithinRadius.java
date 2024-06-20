package src.java.JSON;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AmenitiesWithinRadius {

    public static class Node {
        double latitude;
        double longitude;
        String amenities;

        public Node(double latitude, double longitude, String amenities) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.amenities = amenities;
        }
    }

    public static void main(String[] args) {
        try {
            // Read GeoJSON files
            String amenityGeoJsonContent = readGeoJsonFile("C:\\Users\\alenq\\Downloads\\transitorartifact\\sourcecode\\bcs25-project-1-2\\bcs25-project-1-2\\src\\java\\JSON\\amenity.geojson");
            String shopGeoJsonContent = readGeoJsonFile("C:\\Users\\alenq\\Downloads\\transitorartifact\\sourcecode\\bcs25-project-1-2\\bcs25-project-1-2\\src\\java\\JSON\\shop.geojson");

            // Get user input for latitude and longitude
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter latitude: ");
            double userLat = scanner.nextDouble();
            System.out.print("Enter longitude: ");
            double userLon = scanner.nextDouble();

            // Find nodes within 2 kilometers from both files
            double radius = 2000; // 2 kilometers in meters
            List<Node> nodesWithinDistance = new ArrayList<>();
            nodesWithinDistance.addAll(findNodesWithinDistance(amenityGeoJsonContent, userLat, userLon, radius));
            nodesWithinDistance.addAll(findNodesWithinDistance(shopGeoJsonContent, userLat, userLon, radius));

            // Print amenities of nodes within 2 kilometers
            if (!nodesWithinDistance.isEmpty()) {
                System.out.println("Amenities within 2 kilometers:");
                for (Node node : nodesWithinDistance) {
                    System.out.println("Latitude: " + node.latitude + ", Longitude: " + node.longitude + ", Amenity: " + node.amenities);
                }
            } else {
                System.out.println("No amenities found within 2 kilometers.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readGeoJsonFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    public static List<Node> findNodesWithinDistance(String geoJsonContent, double userLat, double userLon, double radius) {
        List<Node> nodesWithinDistance = new ArrayList<>();
        JSONObject geoJson = new JSONObject(geoJsonContent);
        JSONArray features = geoJson.getJSONArray("features");

        for (int i = 0; i < features.length(); i++) {
            JSONObject feature = features.getJSONObject(i);
            JSONObject geometry = feature.getJSONObject("geometry");
            JSONArray coordinates = geometry.getJSONArray("coordinates");

            double nodeLon = coordinates.getDouble(0);
            double nodeLat = coordinates.getDouble(1);
            String amenities = feature.getJSONObject("properties").optString("amenities", "N/A");

            if (calculateDistance(userLat, userLon, nodeLat, nodeLon) <= radius) {
                nodesWithinDistance.add(new Node(nodeLat, nodeLon, amenities));
            }
        }
        return nodesWithinDistance;
    }

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Vincenty's formula for calculating distance
        final double a = 6378137; // Semi-major axis of the Earth (meters)
        final double f = 1 / 298.257223563; // Flattening of the Earth
        final double b = 6356752.314245; // Semi-minor axis of the Earth (meters)

        double L = Math.toRadians(lon2 - lon1);
        double U1 = Math.atan((1 - f) * Math.tan(Math.toRadians(lat1)));
        double U2 = Math.atan((1 - f) * Math.tan(Math.toRadians(lat2)));

        double sinU1 = Math.sin(U1), cosU1 = Math.cos(U1);
        double sinU2 = Math.sin(U2), cosU2 = Math.cos(U2);

        double lambda = L, lambdaP;
        int iterLimit = 100;
        double cosSqAlpha, sinSigma, cos2SigmaM, cosSigma, sigma, sinLambda, cosLambda;
        do {
            sinLambda = Math.sin(lambda);
            cosLambda = Math.cos(lambda);
            sinSigma = Math.sqrt((cosU2 * sinLambda) * (cosU2 * sinLambda) +
                    (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda) * (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda));
            if (sinSigma == 0) return 0; // co-incident points
            cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
            sigma = Math.atan2(sinSigma, cosSigma);
            double sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
            cosSqAlpha = 1 - sinAlpha * sinAlpha;
            cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;
            double C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha));
            lambdaP = lambda;
            lambda = L + (1 - C) * f * sinAlpha *
                    (sigma + C * sinSigma * (cos2SigmaM + C * cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM)));
        } while (Math.abs(lambda - lambdaP) > 1e-12 && --iterLimit > 0);

        if (iterLimit == 0) return Double.NaN; // formula failed to converge

        double uSq = cosSqAlpha * (a * a - b * b) / (b * b);
        double A = 1 + uSq / 16384 * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
        double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
        double deltaSigma = B * sinSigma * (cos2SigmaM + B / 4 *
                (cosSigma * (-1 + 2 * cos2SigmaM * cos2SigmaM) -
                        B / 6 * cos2SigmaM * (-3 + 4 * sinSigma * sinSigma) *
                                (-3 + 4 * cos2SigmaM * cos2SigmaM)));

        double s = b * A * (sigma - deltaSigma);
        return s; // distance in meters
    }
}
