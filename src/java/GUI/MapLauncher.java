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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapLauncher extends Application{

    private static final double MAP_WIDTH =900.0;
    private static final double MAP_HEIGHT = 00.0;
    private static final double WINDOW_WIDTH = 1540;
    private static final double WINDOW_HEIGHT = 780.0;

    // Google Map static API key
    private static final String API_KEY = "AIzaSyDnJH0pu5NzqH0b6GjiPyTDfdkBDugYw6w";

    private double CENTER_LATITUDE = 50.851368; // Latitude of Maastricht
    private double CENTER_LONGITUDE = 5.690973; // Longitude of Maastricht

    private ImageView mapView;
    private static int scale = 1;
    private static int zoomLevel = 13;

    private TextField zipCodeField1;
    private TextField zipCodeField2;

    private TextField distanceTextField;
    private TextField walkTimeTextField;
    private TextField bikeTimeTextField;
    private TextField carTimeTextField;
    private Place place1;
    private Place place2;


    @Override
    public void start(Stage primaryStage) throws IOException {
        Data.getData();

        // Initialize places to null
        place1 = null;
        place2 = null;

        // Construct the URL for the map image of Maastricht
        String mapUrl = constructMapUrl();

        // Load the map image from the URL
        Image mapImage = new Image(mapUrl);


        // Create an ImageView to display the map image
        mapView = new ImageView(mapImage);
        mapView.setFitWidth(MAP_WIDTH);
        mapView.setFitHeight(MAP_HEIGHT);
        mapView.setPreserveRatio(true);

        // Create zoom buttons
        Button zoomInButton = new Button("Zoom In");
        zoomInButton.setOnAction(event -> zoomIn());
        zoomInButton.setPrefSize(100, 40);
        Button zoomOutButton = new Button("Zoom Out");
        zoomOutButton.setOnAction(event -> zoomOut());
        zoomOutButton.setPrefSize(100, 40);

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
        Label distanceLabel = new Label("Distance: ");
        Label walkTimeLabel = new Label("Average time by walk: ");
        Label bikeTimeLabel = new Label("Average time by bike: ");
        Label carTimeLabel = new Label("Average time by car: ");

        // Create TextFields for displaying/updating the values
        distanceTextField = new TextField();
        walkTimeTextField = new TextField();
        bikeTimeTextField = new TextField();
        carTimeTextField = new TextField();


        // Set preferred widths for TextFields
        distanceTextField.setPrefWidth(100);
        walkTimeTextField.setPrefWidth(100);
        bikeTimeTextField.setPrefWidth(100);
        carTimeTextField.setPrefWidth(100);

        // Create HBoxes to hold the labels and text fields
        HBox distanceBox = new HBox(10, distanceLabel, distanceTextField);
        HBox walkTimeBox = new HBox(10, walkTimeLabel, walkTimeTextField);
        HBox bikeTimeBox = new HBox(10, bikeTimeLabel, bikeTimeTextField);
        HBox carTimeBox = new HBox(10, carTimeLabel, carTimeTextField);

        // Set alignment and padding for HBoxes
        distanceBox.setAlignment(Pos.CENTER);
        walkTimeBox.setAlignment(Pos.CENTER);
        bikeTimeBox.setAlignment(Pos.CENTER);
        carTimeBox.setAlignment(Pos.CENTER);
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
        infoVBox.setAlignment(Pos.CENTER);
        infoVBox.setPrefSize((WINDOW_WIDTH - MAP_WIDTH) / 2, WINDOW_HEIGHT);
        infoVBox.setPadding(new Insets(10));

        // Create a HBox to hold the map and controls
        HBox hbox = new HBox(mapView, controlsVBox, infoVBox);

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

    private void searchPlaces() throws IOException {
        String zipCode1 = zipCodeField1.getText();
        String zipCode2 = zipCodeField2.getText();

        // Check if both zip code fields are filled
        if (!zipCode1.isEmpty() && !zipCode2.isEmpty()) {
            // Retrieve latitude and longitude for the given zip codes
            place1 = new Place(zipCode1);
            place2 = new Place(zipCode2);

//            double distance = CalculateDistance.getDistance(zipCode1, zipCode2);
//            double walkTime = TimeCalculator.calculateAverageTimeTaken(distance, new Walk());
//            double bikeTime = TimeCalculator.calculateAverageTimeTaken(distance, new Bike());
//            double carTime = TimeCalculator.calculateAverageTimeTaken(distance, new Car());

            // Update map and information
            updateMap();
            //updateInformation(distance, walkTime, bikeTime, carTime);
        } else {
            // Show an error message if either of the zip code fields is empty
            System.out.println("Please enter both zip codes.");
        }
    }

    private void updateInformation(double distance, double walkTime, double bikeTime, double carTime) {
        distanceTextField.setText(String.valueOf(distance));
        walkTimeTextField.setText(String.valueOf(walkTime));
        bikeTimeTextField.setText(String.valueOf(bikeTime));
        carTimeTextField.setText(String.valueOf(carTime));
    }


    private String constructMapUrl() {
        StringBuilder mapUrlBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/staticmap");
        mapUrlBuilder.append("?center=Maastricht");
        //mapUrlBuilder.append("?center=").append(CENTER_LATITUDE).append(",").append(CENTER_LONGITUDE);
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
