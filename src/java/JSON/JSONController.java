package src.java.JSON;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import src.java.GUI.Data;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JSONController {

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

    public List<School> getSchoolsFromGeoJSON(String filePath) throws IOException {
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
                                String postcode = Data.findClosestZipCode(latitude, longitude);
                                schools.add(new School(name, postcode, latitude, longitude));
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
        }
    }
}