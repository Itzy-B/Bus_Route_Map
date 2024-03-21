package src.java.GUI;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import src.java.Main.*;
import java.text.DecimalFormat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the graphical user interface (GUI) for launching a map application.
 */
public class MapLauncher extends Application{

    // Constants for defining dimensions of the map window
    private static final double MAP_WIDTH =900.0;
    private static final double MAP_HEIGHT = 780.0;
    private static final double WINDOW_WIDTH = 1540;
    private static final double WINDOW_HEIGHT = 780.0;

    // Google Map static API key
    private static final String API_KEY = "AIzaSyDnJH0pu5NzqH0b6GjiPyTDfdkBDugYw6w";

    // Coordinates of Maastricht
    private double CENTER_LATITUDE = 50.851368;
    private double CENTER_LONGITUDE = 5.690973;

    // Initial scale for the map
    private static int scale = 1;

    // Initial zoom level for the map
    private static int zoomLevel = 13;

    private ImageView mapView;

    private TextField zipCodeField1;
    private TextField zipCodeField2;

    private Label distanceLabel;
    private Label walkTimeLabel;
    private Label bikeTimeLabel;
    private Label carTimeLabel;

    private Place place1;
    private Place place2;

    /**
     * Entry point of the JavaFX application.
     *
     * @param primaryStage The primary stage for displaying the map window.
     * @throws IOException If there is an error while initializing the map.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        // Initialize data
        Data.getData();

        // Initialize places to null
        place1 = null;
        place2 = null;

        // Construct the URL for the map image of Maastricht
        String mapUrl = constructMapUrl();
        System.out.println(mapUrl);

        // Load the map image from the URL
        Image mapImage = new Image(mapUrl);

        // Create an ImageView to display the map image
        mapView = new ImageView(mapImage);
        mapView.setFitWidth(MAP_WIDTH);
        mapView.setFitHeight(MAP_HEIGHT);
        mapView.setPreserveRatio(true);

        // Create zoom buttons
        Button zoomInButton = new Button("+");
        zoomInButton.setOnAction(event -> zoomIn());
        zoomInButton.setPrefSize(50, 50);
        Button zoomOutButton = new Button("-");
        zoomOutButton.setOnAction(event -> zoomOut());
        zoomOutButton.setPrefSize(50, 50);

        // Create a Search button
        Button searchButton = new Button("Search");
        searchButton.setOnAction(event -> {
            try {
                searchPlaces();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        searchButton.setPrefSize(100, 40);

        // Create text fields for location names
        zipCodeField1 = new TextField();
        zipCodeField1.setPromptText("Enter Zip Code 1");

        zipCodeField2 = new TextField();
        zipCodeField2.setPromptText("Enter Zip Code 2");

        // Create Labels for displaying the titles
        distanceLabel = new Label();
        walkTimeLabel = new Label();
        bikeTimeLabel = new Label();
        carTimeLabel = new Label();

        // Set preferred widths for TextFields
        distanceLabel.setPrefWidth(200);
        walkTimeLabel.setPrefWidth(200);
        bikeTimeLabel.setPrefWidth(200);
        carTimeLabel.setPrefWidth(200);

        // Create HBoxes to hold the labels and text fields
        HBox distanceBox = new HBox(10, distanceLabel);
        HBox walkTimeBox = new HBox(10, walkTimeLabel);
        HBox bikeTimeBox = new HBox(10, bikeTimeLabel);
        HBox carTimeBox = new HBox(10, carTimeLabel);

        // Set alignment and padding for HBoxes
        distanceBox.setAlignment(Pos.CENTER_LEFT);
        walkTimeBox.setAlignment(Pos.CENTER_LEFT);
        bikeTimeBox.setAlignment(Pos.CENTER_LEFT);
        carTimeBox.setAlignment(Pos.CENTER_LEFT);
        distanceBox.setPadding(new Insets(10));
        walkTimeBox.setPadding(new Insets(10));
        bikeTimeBox.setPadding(new Insets(10));
        carTimeBox.setPadding(new Insets(10));

        // Create HBox for control buttons
        HBox controlButtons = new HBox(10, zoomInButton, zoomOutButton, searchButton);
        controlButtons.setPrefSize((WINDOW_WIDTH - MAP_WIDTH) / 2, WINDOW_HEIGHT);
        controlButtons.setPadding(new Insets(10));

        // Create a VBox for text fields and control buttons
        VBox controlsVBox = new VBox(10, zipCodeField1, zipCodeField2, controlButtons);
        controlsVBox.setAlignment(Pos.CENTER);
        controlsVBox.setPrefSize((WINDOW_WIDTH - MAP_WIDTH) / 2, WINDOW_HEIGHT);
        controlsVBox.setPadding(new Insets(10));

        // Create a VBox for displaying information
        VBox infoVBox = new VBox(10, distanceBox, walkTimeBox, bikeTimeBox, carTimeBox);
        infoVBox.setAlignment(Pos.CENTER_LEFT);
        infoVBox.setPrefSize((WINDOW_WIDTH - MAP_WIDTH) / 2, WINDOW_HEIGHT);
        infoVBox.setPadding(new Insets(10));

        // Create a HBox to hold the map and controls
        HBox hbox = new HBox(mapView, controlsVBox, infoVBox);
        hbox.setSpacing(20);
        hbox.setPadding(new Insets(20));

        // Create a Scene and set it on the Stage
        Scene scene = new Scene(hbox, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setTitle("Map of Maastricht");

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void zoomIn() {
        zoomLevel += 1;
        scale *= 2;
        Platform.runLater(this::updateMap);
    }

    private void zoomOut() {
        if (zoomLevel>=1) {
            zoomLevel -= 1;
            scale /= 2;
            Platform.runLater(this::updateMap);
        }
    }

    // Method for searching places and updating information
    private void searchPlaces() throws IOException {
        String zipCode1 = zipCodeField1.getText();
        String zipCode2 = zipCodeField2.getText();

        // Check if both zip code fields are filled
        if (!zipCode1.isEmpty() && !zipCode2.isEmpty()) {
            place1 = new Place(zipCode1);
            place2 = new Place(zipCode2);

            // Calculate distance and average times
            double distance = CalculateDistance.getDistance(zipCode1, zipCode2, false);
            long walkTime = TimeCalculator.calculateAverageTimeTaken(zipCode1, zipCode2, new Walk());
            long bikeTime = TimeCalculator.calculateAverageTimeTaken(zipCode1, zipCode2, new Bike());
            long carTime = TimeCalculator.calculateAverageTimeTaken(zipCode1, zipCode2, new Car());

            // Update map with midpoint and information with vehicles
            ArrayList<Double> midPoint = CalculateDistance.findMidpoint(place1, place2);
            CENTER_LATITUDE = midPoint.get(0);
            CENTER_LONGITUDE = midPoint.get(1);
            updateMap();
            updateInformation(distance, walkTime, bikeTime, carTime);
        } else {
            // Show an error message if either of the zip code fields is empty
            System.out.println("Please enter both zip codes.");
        }
    }

    // Method for updating information in the TextFields
    private void updateInformation(double distance, long walkTime, long bikeTime, long carTime) {
        distanceLabel.setText("Distance: " + distance + " kilometers");
        walkTimeLabel.setText("Average time by walk: " + walkTime + " minutes");
        bikeTimeLabel.setText("Average time by bike: " + bikeTime + " minutes");
        carTimeLabel.setText("Average time by car: " + carTime + " minutes");
    }

    // Method for constructing the URL for the map image
    private String constructMapUrl() {

        StringBuilder mapUrlBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/staticmap");

        if (place1 == null || place2 == null) {
            mapUrlBuilder.append("?center=Maastricht");
        }

        mapUrlBuilder.append("?center=").append(CENTER_LATITUDE).append(",").append(CENTER_LONGITUDE);
        mapUrlBuilder.append("&zoom=").append(zoomLevel);
        mapUrlBuilder.append("&size=600x500");
        mapUrlBuilder.append("&scale=").append(scale);

        // show the path between two points if places are not null
        if (place1 != null && place2 != null) {
            List<Place> placeList = new ArrayList<>();
            placeList.add(place1);
            placeList.add(place2);
            mapUrlBuilder.append("&path=color:0xff0000ff%7Cweight:5%7Cenc:");
            mapUrlBuilder.append(PolylineEncoder.encode(placeList));
        }
        // Add API Key
        mapUrlBuilder.append("&key=").append(API_KEY);

        return mapUrlBuilder.toString();
    }

    private void updateMap() {
        Platform.runLater(() -> {
            String mapUrl = constructMapUrl();
            try {
                Image mapImage = new Image(mapUrl);
                mapView.setImage(mapImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

}
