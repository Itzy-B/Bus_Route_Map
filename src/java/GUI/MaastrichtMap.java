package src.java.GUI;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;

public class MaastrichtMap extends Application{

    private static final double ZOOM_FACTOR = 1.1;
    private static final double MAP_WIDTH = 600.0;
    private static final double MAP_HEIGHT = 400.0;
    private static final double WINDOW_WIDTH = 800.0;
    private static final double WINDOW_HEIGHT = 400.0;

    // Google Map static API key
    private static final String API_KEY = "AIzaSyDnJH0pu5NzqH0b6GjiPyTDfdkBDugYw6w";

    private ImageView mapView;
    private double scale = 1.0;

    private TextField zipCode1;
    private TextField zipCode2;
    private ArrayList<Place> places;


    @Override
    public void start(Stage primaryStage) {
        // Sample locations
        places = new ArrayList<>();
        places.add(new Place(50.8552328455939, 5.69223719348659, "6211AL"));
        places.add(new Place(50.8966117277777, 5.70705921111111, "6223GR"));

        // Construct the URL for the map image of Maastricht
        String mapUrl = constructMapUrl();

        // Load the map image from the URL
        Image mapImage = new Image(mapUrl);

        // Create an ImageView to display the map image
        mapView = new ImageView(mapImage);
        mapView.setFitWidth(MAP_WIDTH);
        mapView.setPreserveRatio(true);

        // Create zoom buttons
        Button zoomInButton = new Button("Zoom In");
        zoomInButton.setOnAction(event -> zoomIn());
        zoomInButton.setPrefSize(100, 40);
        Button zoomOutButton = new Button("Zoom Out");
        zoomOutButton.setOnAction(event -> zoomOut());
        zoomOutButton.setPrefSize(100, 40);

        // Create text fields for location names
        zipCode1 = new TextField();
        zipCode1.setPromptText("Enter Zip Code 1");
        zipCode2 = new TextField();
        zipCode2.setPromptText("Enter Zip Code 2");

        // Create text fields for location names
        zipCode1 = new TextField();
        zipCode1.setPromptText("Enter Zip Code 1");
        zipCode1.setOnAction(event -> addPlaceFromTextField(zipCode1.getText()));

        zipCode2 = new TextField();
        zipCode2.setPromptText("Enter Zip Code 2");
        zipCode2.setOnAction(event -> addPlaceFromTextField(zipCode2.getText()));

        // Create HBox for control buttons
        HBox controlButtons = new HBox(10, zoomInButton, zoomOutButton);
        controlButtons.setPrefSize((WINDOW_WIDTH - MAP_WIDTH) / 2, WINDOW_HEIGHT);
        controlButtons.setPadding(new Insets(10));

        // Create a VBox for text fields and control buttons
        VBox controlsVBox = new VBox(10, zipCode1, zipCode2, controlButtons);
        controlsVBox.setAlignment(Pos.CENTER);
        controlsVBox.setPrefSize((WINDOW_WIDTH - MAP_WIDTH) / 2, WINDOW_HEIGHT);
        controlsVBox.setPadding(new Insets(10));

        // Create a HBox to hold the map and controls
        HBox hbox = new HBox(mapView, controlsVBox);

        // Create a Scene and set it on the Stage
        Scene scene = new Scene(hbox, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setTitle("Map of Maastricht");

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void zoomIn() {
        scale *= ZOOM_FACTOR;
        mapView.setScaleX(scale);
        mapView.setScaleY(scale);
    }

    private void zoomOut() {
        scale /= ZOOM_FACTOR;
        mapView.setScaleX(scale);
        mapView.setScaleY(scale);
    }

    private String constructMapUrl() {
        StringBuilder mapUrlBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/staticmap");
        mapUrlBuilder.append("?center=Maastricht");
        mapUrlBuilder.append("&zoom=13");
        mapUrlBuilder.append("&size=600x400");

        // Add markers for each location
        for (Place p : places) {
            mapUrlBuilder.append("&markers=color:red%7Clabel:")
                    .append(p.getZipCode())
                    .append("%7C")
                    .append(p.getLatitude())
                    .append(",")
                    .append(p.getLongitude());
        }

        // Add API Key
        mapUrlBuilder.append("&key=");
        mapUrlBuilder.append(API_KEY);

        return mapUrlBuilder.toString();
    }

    private void addPlaceFromTextField(String zipCode) {
        // Retrieve latitude and longitude for the given zip code (You need to implement this part)
        double latitude = 0.0; // Example latitude
        double longitude = 0.0; // Example longitude

        // Add new place to the list
        places.add(new Place(latitude, longitude, zipCode));

        // Update the map
        updateMap();
    }

    private void updateMap() {
        String mapUrl = constructMapUrl();
        Image mapImage = new Image(mapUrl);
        mapView.setImage(mapImage);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
