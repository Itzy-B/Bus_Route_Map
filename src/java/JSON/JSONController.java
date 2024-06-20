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

    private static final String AMENITY_PATH = "src/java/JSON/amenity.geojson";
    private static final String SHOP_PATH = "src/java/JSON/shop.geojson";
    private static final String TOURISM_PATH = "src/java/JSON/tourism.geojson";

    private static final String SCHOOL_AMENITY = "school";
    private static final String ATM_AMENITY = "atm";
    private static final String FUEL_AMENITY = "fuel";
    private static final String WASTE_AMENITY = "waste_basket";

    private static final String SUPERMARKET_SHOP = "supermarket";




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

    /**
     * Returns a list of places from the GEOJSON files provided
     * @param isAmenity specifies whether the palces are amenities or shops
     * @param type specifies the type of places to be returned eg. supermarket, shop, school, etc.
     * @param useAPI Whether the method is to use the API to supplement any missing information (should be left false because it is very slow)
     * @return Returns a list of places that the user searched for
     * @throws IOException
     * @throws InterruptedException
     */
    public List<Place> getPlacesFromGeoJSON(Boolean isAmenity, String type, boolean useAPI) throws IOException, InterruptedException {
        String filePath;
        if(isAmenity){
            filePath = AMENITY_PATH;
        }else{
            filePath = SHOP_PATH;
        }
        Data.getData();
        List<Place> places = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(new File(filePath));

        JsonNode features = rootNode.get("features");
        if (features != null && features.isArray()) {
            for (JsonNode feature : features) {
                JsonNode properties = feature.get("properties");
                if(isAmenity) {
                    if (properties != null) {
                        JsonNode amenityNode = properties.get("amenity");
                        if (amenityNode != null && type.equals(amenityNode.asText())) {
                            // Exclude waste baskets with waste type dog_excrement
                            if ("waste_basket".equals(type)) {
                                JsonNode wasteNode = properties.get("waste");
                                if (wasteNode != null && "dog_excrement".equals(wasteNode.asText())) {
                                    continue;  // Skip this feature
                                }
                            }

                            String name = properties.has("name") ? properties.get("name").asText() : "Unnamed " + capitalizeFirstLetter(type);
                            JsonNode geometry = feature.get("geometry");
                            if (geometry != null && "Point".equals(geometry.get("type").asText())) {
                                JsonNode coordinates = geometry.get("coordinates");
                                if (coordinates != null && coordinates.isArray() && coordinates.size() == 2) {
                                    double longitude = coordinates.get(0).asDouble();
                                    double latitude = coordinates.get(1).asDouble();
                                    if (useAPI) {
                                        String[] data = getPostalCode(latitude, longitude);
                                        String postcode = data[0];
                                        if (name.equals("Unnamed " + capitalizeFirstLetter(type)) && data[1] != null) {
                                            name = data[1];
                                        }
                                        places.add(new Place(name, postcode, latitude, longitude));
                                        Thread.sleep(200);
                                    } else {
                                        String postcode = Data.findClosestZipCode(latitude, longitude);
                                        places.add(new Place(name, postcode, latitude, longitude));
                                    }

                                }
                            }
                        }
                    }
                }else{
                    JsonNode shopNode = properties.get("shop");
                    if (shopNode != null && type.equals(shopNode.asText())) {

                        String name = properties.has("name") ? properties.get("name").asText() : "Unnamed " + capitalizeFirstLetter(type);
                        JsonNode geometry = feature.get("geometry");
                        if (geometry != null && "Point".equals(geometry.get("type").asText())) {
                            JsonNode coordinates = geometry.get("coordinates");
                            if (coordinates != null && coordinates.isArray() && coordinates.size() == 2) {
                                double longitude = coordinates.get(0).asDouble();
                                double latitude = coordinates.get(1).asDouble();
                                if (useAPI) {
                                    String[] data = getPostalCode(latitude, longitude);
                                    String postcode = data[0];
                                    if (name.equals("Unnamed " + capitalizeFirstLetter(type)) && data[1] != null) {
                                        name = data[1];
                                    }
                                    places.add(new Place(name, postcode, latitude, longitude));
                                    Thread.sleep(200);
                                } else {
                                    String postcode = Data.findClosestZipCode(latitude, longitude);
                                    places.add(new Place(name, postcode, latitude, longitude));
                                }

                            }
                        }
                    }
                }
            }
        }
        return places;
    }

    private String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }


    public static void main(String[] args) {
        JSONController controller = new JSONController();
        int count = 0;
        try {
            long startTime = System.currentTimeMillis(); // Start timing
            List<Place> places = controller.getPlacesFromGeoJSON(true, SCHOOL_AMENITY, false);
            for (Place place : places) {
                System.out.println(place);
                count++;
            }
            long endTime = System.currentTimeMillis(); // End timing
            long duration = endTime - startTime; // Calculate the duration
            System.out.println("Time taken: " + duration + " ms");
            System.out.println("Number of places: " + count);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}