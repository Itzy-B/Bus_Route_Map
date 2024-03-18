package src.java.GUI;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

// A simple example, still working on it.
public class MaastrichtMap extends Application{

    private static final double ZOOM_FACTOR = 1.1;

    private ImageView mapView;
    private double scale = 1.0;

    @Override
    public void start(Stage primaryStage) {
        // Construct the URL for the map image of Maastricht
        String mapUrl = "https://maps.googleapis.com/maps/api/staticmap"
                + "?center=Maastricht"
                + "&zoom=13"
                + "&size=600x400"
                + "&key=API key"; // Replace YOUR_API_KEY with your actual API key

        // API key = AIzaSyDnJH0pu5NzqH0b6GjiPyTDfdkBDugYw6w
        // Don't use it too many times, causing bills.

        // Load the map image from the URL
        Image mapImage = new Image(mapUrl);

        // Create an ImageView to display the map image
        mapView = new ImageView(mapImage);

        // Create zoom buttons
        Button zoomInButton = new Button("Zoom In");
        zoomInButton.setOnAction(event -> zoomIn());
        Button zoomOutButton = new Button("Zoom Out");
        zoomOutButton.setOnAction(event -> zoomOut());

        // Create a VBox for zoom buttons
        VBox zoomButtons = new VBox(zoomInButton, zoomOutButton);
        zoomButtons.setSpacing(10);

        // Create an HBox to hold the map and zoom buttons
        HBox root = new HBox(new StackPane(mapView), zoomButtons);

        // Create a Scene and set it on the Stage
        Scene scene = new Scene(root, 800, 400);
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

    public static void main(String[] args) {
        launch(args);
    }

}
