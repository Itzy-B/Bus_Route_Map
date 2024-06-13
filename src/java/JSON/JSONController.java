package src.java.JSON;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import src.java.GUI.Data;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class JSONController {
    private static final String API_URL_TEMPLATE = "https://geocode.maps.co/reverse?lat=%f&lon=%f&api_key=6666d2ba4331d378476246ngzcb1fcc";

    private static final String SCHOOL_AMENITY = "school";

    public static class School {
        public String name;
        public String city;
        public String housenumber;
        public String postcode;
        public String street;
        public double latitude;
        public double longitude;

        public School(String name, String postcode, double latitude, double longitude) {
            this.name = name;
            this.postcode = postcode;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        public String toString() {
            return "School{" +
                    "name='" + name + '\'' +
                    ", postcode='" + postcode + '\'' +
                    ", latitude=" + latitude +
                    ", longitude=" + longitude +
                    '}';
        }

    }

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
            JsonNode addressNode = rootNode.get("address");
            JsonNode nameNode = rootNode.get("display_name");
            if (addressNode != null) {
                JsonNode postcodeNode = addressNode.get("postcode");
                if (postcodeNode != null) {
                    data[0] = postcodeNode.asText();
                    data[1] = nameNode.asText().split(", ")[0];
                    return data;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<School> getSchoolsFromGeoJSON(String filePath) throws IOException, InterruptedException {
        Data.getData();
        List<School> schools = new ArrayList<>();
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
                                String[] data = getPostalCode(latitude, longitude);
                                String postcode = data[0];
                                if(name.equals("Unnamed School")){
                                    name = data[1];
                                }
                                schools.add(new School(name, postcode, latitude, longitude));
                                Thread.sleep(1000);
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
            List<School> schools = controller.getSchoolsFromGeoJSON("src/java/JSON/amenity.geojson");
            for (School school : schools) {
                System.out.println(school);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}