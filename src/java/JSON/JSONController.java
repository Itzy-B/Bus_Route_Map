package src.java.JSON;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import src.java.GUI.Data;
import src.java.GUI.Place;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class JSONController {
    private static final String NAME_API_URL_TEMPLATE = "https://geocode.maps.co/reverse?lat=%f&lon=%f&api_key=6666d2ba4331d378476246ngzcb1fcc";
    private static final String API_URL_TEMPLATE = "https://api.geoapify.com/v1/geocode/reverse?lat=%f&lon=%f&apiKey=c9a518e5482a431f8c1dbd0d894ef95e";
    private static final String SCHOOL_AMENITY = "school";



    public static String[] getPostalCode(double latitude, double longitude) {
        String[] data = new String[2];
        String apiUrl = String.format(API_URL_TEMPLATE, latitude, longitude);

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuilder response = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                response.append(output);
            }

            conn.disconnect();

            // Parse JSON response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response.toString());
            JsonNode featuresNode = rootNode.get("features");

            if (featuresNode != null && featuresNode.isArray() && featuresNode.size() > 0) {
                JsonNode firstFeature = featuresNode.get(0);
                JsonNode propertiesNode = firstFeature.get("properties");

                if (propertiesNode != null) {
                    JsonNode postcodeNode = propertiesNode.get("postcode");
                    JsonNode nameNode = propertiesNode.get("name");

                    if (postcodeNode != null) {
                        data[0] = postcodeNode.asText();
                        if(nameNode == null){
                            data[1] = getLocationName(latitude, longitude);
                        }
                        //System.out.println(data[1]);
                        return data;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getLocationName(double latitude, double longitude) {
        String apiUrl = String.format(NAME_API_URL_TEMPLATE, latitude, longitude);
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuilder response = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                response.append(output);
            }

            conn.disconnect();

            // Parse JSON response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response.toString());
            JsonNode displayNameNode = rootNode.get("display_name");

            if (displayNameNode != null) {
                return displayNameNode.asText();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Place> getSchoolsFromGeoJSON(String filePath, boolean useAPI) throws IOException, InterruptedException {
        Data.getData();
        List<Place> schools = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(new File(filePath));

        JsonNode features = rootNode.get("features");
        if (features != null && features.isArray()) {
            for (JsonNode feature : features) {
                JsonNode properties = feature.get("properties");
                if (properties != null) {
                    JsonNode amenityNode = properties.get("amenity");
                    if (amenityNode != null && SCHOOL_AMENITY.equals(amenityNode.asText())) {
                        String name = properties.has("name") ? properties.get("name").asText() : "Unnamed School";
                        JsonNode geometry = feature.get("geometry");
                        if (geometry != null && "Point".equals(geometry.get("type").asText())) {
                            JsonNode coordinates = geometry.get("coordinates");
                            if (coordinates != null && coordinates.isArray() && coordinates.size() == 2) {
                                double longitude = coordinates.get(0).asDouble();
                                double latitude = coordinates.get(1).asDouble();
                                if(useAPI){
                                    String[] data = getPostalCode(latitude, longitude);
                                    String postcode = data[0];
                                    if(name.equals("Unnamed School") && data[1] != null){
                                        name = data[1];
                                    }
                                    schools.add(new Place(name, postcode, latitude, longitude));
                                    Thread.sleep(200);
                                }else{
                                    String postcode = Data.findClosestZipCode(latitude, longitude);
                                    schools.add(new Place(name, postcode, latitude, longitude));
                                }

                            }
                        }
                    }
                }
            }
        }
        return schools;
    }


    public static void main(String[] args) {
        JSONController controller = new JSONController();
        try {
            long startTime = System.currentTimeMillis(); // Start timing
            List<Place> schools = controller.getSchoolsFromGeoJSON("src/java/JSON/amenity.geojson", false);
            for (Place school : schools) {
                System.out.println(school);
            }
            long endTime = System.currentTimeMillis(); // End timing
            long duration = endTime - startTime; // Calculate the duration
            System.out.println("Time taken: " + duration + " ms");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}