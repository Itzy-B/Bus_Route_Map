package src.java.GUI;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import src.java.Database.DatabaseController;
import src.java.Main.BusRouteFinder;
import src.java.Main.CalculateDistance;
import src.java.Singletons.ExceptionManager;
/**
 * This class represents the graphical user interface (GUI) for launching a map application.
 */
public class MapLauncher extends Application{

    // Constants for defining dimensions of the map window
    private static final double MAP_WIDTH = 900.0;
    private static final double MAP_HEIGHT = 780.0;
    private static final double WINDOW_WIDTH = 1540;
    private static final double WINDOW_HEIGHT = 780.0;

    // Google Map static API key
    private static final String API_KEY = "AIzaSyDnJH0pu5NzqH0b6GjiPyTDfdkBDugYw6w";
    // private static final String API_KEY = "0";  

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

    private CheckBox checkBox = null;
    private CheckBox toggleQuerySystem = null;
    private ListView<String> listView;

    //private Place departure;
    //private Place destination;
    private static Place departure;
    private static Place destination;

    private List<Place> path;
    /**
     * Entry point of the JavaFX application.
     *
     * @param primaryStage The primary stage for displaying the map window.
     * @throws IOException If there is an error while initializing the map.
     */
    @Override
    public void start(Stage primaryStage) {
        // Initialize data
        Data.getData();

        // Initialize places to null
        departure = null;
        destination = null;

        // Construct the URL for the map image of Maastricht
        String mapUrl = constructMapUrl();

        // Load the map image from the URL
        // Image mapImage = new Image("/staticmap.png");
        Image mapImage = new Image(mapUrl);

        // Create an ImageView to display the map image
        mapView = new ImageView(mapImage);
        mapView.setFitWidth(MAP_WIDTH);
        mapView.setFitHeight(MAP_HEIGHT);
        mapView.setPreserveRatio(true);

        // Create zoom buttons
        Button showAccessibilityZipCodes = new Button("Show accessibility of postal codes");
        Button zoomInButton = new Button("+");
        showAccessibilityZipCodes.setOnAction(event -> showAccessibilityZipCodes());
        zoomInButton.setOnAction(event -> zoomIn());
        zoomInButton.setPrefSize(50, 50);
        Button zoomOutButton = new Button("-");
        zoomOutButton.setOnAction(event -> zoomOut());
        zoomOutButton.setPrefSize(50, 50);
        checkBox = new CheckBox("Toggle graphhopper");
        toggleQuerySystem = new CheckBox("(Don't use this option)Search outside of Maastricht");

        // Create a Search button
        Button searchButton = new Button("Search");
        searchButton.setOnAction(event -> {
            try {
                if (getQueryButtonState()) {
                    constructMapUrl();
                }
                else {
                    searchPlaces();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        searchButton.setPrefSize(150, 50);

        // Create text fields for location names
        zipCodeField1 = new TextField();
        zipCodeField1.setPromptText("Enter Zip Code 1");
        zipCodeField1.setPrefWidth(150);
        zipCodeField1.setStyle("-fx-font-size: 18px;");
        zipCodeField1.setPrefHeight(50);

        zipCodeField2 = new TextField();
        zipCodeField2.setPromptText("Enter Zip Code 2");
        zipCodeField2.setPrefWidth(150);
        zipCodeField2.setStyle("-fx-font-size: 18px;");
        zipCodeField2.setPrefHeight(50);

        // Create VBox for text fields and Search button
        VBox textFieldAndSearch = new VBox(10, zipCodeField1, zipCodeField2, searchButton);
        textFieldAndSearch.setAlignment(Pos.TOP_LEFT);
        textFieldAndSearch.setPadding(new Insets(10));

        // Create an HBox for zoom buttons
        HBox zoomButtons = new HBox(10, zoomInButton, zoomOutButton);
        zoomButtons.setAlignment(Pos.TOP_LEFT);
        zoomButtons.setPadding(new Insets(10));

        HBox checkBoxs = new HBox(10, checkBox, toggleQuerySystem);
        checkBoxs.setAlignment(Pos.TOP_LEFT);
        checkBoxs.setPadding(new Insets(10));

        HBox guiOptions = new HBox(10, showAccessibilityZipCodes);
        checkBoxs.setAlignment(Pos.TOP_LEFT);
        checkBoxs.setPadding(new Insets(10));

        // Create a ListView for the list of strings
        listView = new ListView<>();
        listView.getItems().addAll(Collections.emptyList());
        listView.setPrefHeight(1000);
        listView.setPrefWidth(600);

        // Create a VBox for the right side content
        VBox rightContentVBox = new VBox(10, textFieldAndSearch, zoomButtons, checkBoxs, guiOptions, listView);
        rightContentVBox.setAlignment(Pos.TOP_LEFT);
        rightContentVBox.setPadding(new Insets(10));
        rightContentVBox.setPrefWidth(600);

        // Create a HBox to hold the map and right side content
        HBox hbox = new HBox(mapView, rightContentVBox);
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

    private boolean getCheckBoxState() {
        return checkBox.isSelected();
    }

    private boolean getQueryButtonState() {
        return toggleQuerySystem.isSelected();
    }

    private void zoomOut() {
        if (zoomLevel >= 1) {
            zoomLevel -= 1;
            scale /= 2;
            Platform.runLater(this::updateMap);
        }
    }

    private void showAccessibilityZipCodes() {
        src.java.DisplayerAccessibility.AccessibilityDisplayer displayer = new src.java.DisplayerAccessibility.AccessibilityDisplayer();
        displayer.runAccessibilityDisplayer();
    }

    // Method for searching places and updating information
    private void searchPlaces() throws Exception {
        String zipCode1 = zipCodeField1.getText();
        String zipCode2 = zipCodeField2.getText();

        // Check if both zip code fields are filled
        if (!zipCode1.isEmpty() && !zipCode2.isEmpty()) {
            // Fetch place from zipcode
            departure = new Place(zipCode1);
            destination = new Place(zipCode2);

            ArrayList<Double> midPoint = CalculateDistance.findMidpoint(departure, destination);
            CENTER_LATITUDE = midPoint.get(0);
            CENTER_LONGITUDE = midPoint.get(1);

            Graph graph = new Graph();
            GraphBuilder graphBuilder = new GraphBuilder(graph);
            graphBuilder.getBusStops();

            AStar aStar = new AStar(graph);
            path = aStar.findShortestPath(departure, destination);

            listView.getItems().setAll(aStar.directions);
            updateMap();

        } else {
            System.out.println("Please enter both zip codes.");
            ExceptionManager.showError("Missing A Postcode!", "Fill All Necessary Textfields", "You are missing a postcode, please enter both the postcodes.", AlertType.ERROR);
        }
    }


    // Method for constructing the URL for the map image
    private String constructMapUrl() {

        StringBuilder mapUrlBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/staticmap");

        if (departure == null || destination == null) {
            mapUrlBuilder.append("?center=Maastricht");
        }

        mapUrlBuilder.append("?center=").append(CENTER_LATITUDE).append(",").append(CENTER_LONGITUDE);
        mapUrlBuilder.append("&zoom=").append(zoomLevel);
        mapUrlBuilder.append("&size=600x500");
        mapUrlBuilder.append("&scale=").append(scale);

        if (zipCodeField1 instanceof TextField && zipCodeField2 instanceof TextField) {
            String zipCode1 = zipCodeField1.getText();
            String zipCode2 = zipCodeField2.getText();
            try {
                departure = new Place(zipCode1);
                destination = new Place(zipCode2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (departure != null && destination != null && toggleQuerySystem.isSelected()) {
            
            System.out.println("Using queries");
            ArrayList<Double> departureCoords = new ArrayList<>();
            ArrayList<Double> destinationCoords = new ArrayList<>();
            departureCoords.add(departure.getLongitude());
            departureCoords.add(departure.getLatitude());
            destinationCoords.add(destination.getLongitude());
            destinationCoords.add(destination.getLatitude());

            BusRouteFinder finder = new BusRouteFinder();
            List<Place> placeList = new ArrayList<>();
            try {
                DatabaseController databaseController = new DatabaseController();
                placeList = finder.getShapes(departureCoords,destinationCoords, databaseController);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (placeList.isEmpty()) {
                ExceptionManager.showError("No route found", "Problem", "No route could be found with given postal codes", AlertType.ERROR);
            }

            mapUrlBuilder.append("&path=color:0xff0000ff%7Cweight:5%7Cenc:");
            mapUrlBuilder.append(PolylineEncoder.encode(placeList));
        }


        // show the path between two points if places are not null
        if (departure != null && destination != null && !toggleQuerySystem.isSelected()) {
            mapUrlBuilder.append("&path=color:0xff0000ff%7Cweight:3%7Cenc:");
            mapUrlBuilder.append(PolylineEncoder.encode(path));
        }
        // Add API Key
        mapUrlBuilder.append("&key=").append(API_KEY);

        return mapUrlBuilder.toString();
    }

    private void updateMap() {
        Platform.runLater(() -> {
            try {
                String mapUrl = constructMapUrl();
                Image mapImage = new Image(mapUrl);
                mapView.setImage(mapImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        /* try {
            //Not an elegant solutions, fix later
            launchGraphHopper().waitFor(1, TimeUnit.MINUTES);
            Thread.sleep(6000);
        }
        catch (Exception e) {
            e.printStackTrace();
        } */
        launch(args);
    }
}
