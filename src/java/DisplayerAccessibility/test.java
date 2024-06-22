package src.java.DisplayerAccessibility;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import src.java.GUI.Data;
import src.java.Singletons.FileManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class test extends Application {
    private final double MAP_WIDTH = 600;
    private final double MAP_HEIGHT = 600;
    private double centerLatitude = 50.851368;
    private double centerLongitude = 5.690973;
    Map<String, List<List<Double[]>>> polygonsMap = new HashMap<>();
    HashMap<String, Integer> scoresMap = new HashMap<>();
    private int zoomLevel = 13;
    private String zoomedPostcode;
    private Button updateButton = new Button("Update");
    private Button zoomInButton = new Button("+");
    private Button zoomOutButton = new Button("-");
    private TextField zipCodeField1 = new TextField();
    private ArrayList<String> zipCodes = Data.getZipCodes();
    private String[] zipCodesAll;
    private ArrayList<Double> lats = Data.getLatitudes();
    private ArrayList<Double> longs = Data.getLongitudes();
    private ArrayList<String> colours = getGradientColors();
    private final String API_KEY = "AIzaSyAZwfzWK71qIgXSleA-02n-oXfo5OjOhhU";
    private Canvas canvas = new Canvas(MAP_WIDTH, MAP_HEIGHT);
    private ImageView mapView = new ImageView();
    private Label scoreField = new Label();
    private String URL;
    private double prevX, prevY;
    private final double offset = 268435456;
    private final double radius = offset / Math.PI;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Image Display");
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        zipCodeField1.setPromptText("Enter a zipcode to center");

        root.getChildren().addAll(updateButton, zoomInButton, zoomOutButton, zipCodeField1, scoreField, mapView);

        createActionListeners();
        primaryStage.setScene(new Scene(root, 600, 600));
        primaryStage.show();

        runAccessibilityDisplayer();
    }

    private void createActionListeners() {
        zoomInButton.setOnAction(e -> {
            zoomLevel++;
            requestNewImageIcon();
            try {
                drawScreen();
            } catch (IOException m) {
                m.printStackTrace();
            };
        });

        zoomOutButton.setOnAction(e -> {
            zoomLevel--;
            requestNewImageIcon();
            try {
                drawScreen();
            } catch (IOException m) {
                m.printStackTrace();
            }
        });

        updateButton.setOnAction(e -> {
            centerToZipCode();
            requestNewImageIcon();
            try {
                drawScreen();
            } catch (IOException m) {
                m.printStackTrace();
            }
        });

        mapView.setOnMousePressed(e -> {
            prevX = e.getX();
            prevY = e.getY();
        });

        mapView.setOnMouseDragged(e -> {
            double mouseSensitivity = 10.0;
            double deltaX = e.getX() - prevX;
            double deltaY = e.getY() - prevY;
            prevX = e.getX();
            prevY = e.getY();

            double lonChange = deltaX / MAP_WIDTH * 360 / Math.pow(2, zoomLevel) * mouseSensitivity;
            double latChange = deltaY / MAP_HEIGHT * 360 / Math.pow(2, zoomLevel) * mouseSensitivity;
            centerLongitude -= lonChange;
            centerLatitude += latChange;

            requestNewImageIcon();
            try {
                drawScreen();
            } catch (IOException m) {
                m.printStackTrace();
            }
        });

        mapView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                doubleClickZoom(e.getX(), e.getY());
            }
        });
    }


    public int getColorIndexForScore(int score, HashMap<String, Integer> scoreMap, ArrayList<String> colors) {
        int maxScore = Integer.MIN_VALUE;
        int minScore = Integer.MAX_VALUE;

        
        for (int value : scoreMap.values()) {
            if (value > maxScore) {
                maxScore = value;
            }
            if (value < minScore) {
                minScore = value;
            }
        }

        //https://en.wikipedia.org/wiki/Normalization_(statistics)
        int colorIndex = (int) ((double) (score - minScore) / (maxScore - minScore) * (colors.size() - 1));
        return colorIndex;
    }
    private void runAccessibilityDisplayer() {
        Data.getData();
        try {
            polygonsMap = (Map<String, List<List<Double[]>>>) FileManager.getInstance().getObject("polygonsMap.ser");
            scoresMap = (HashMap<String, Integer>) FileManager.getInstance().getObject("scores.ser");
            zipCodesAll = extractKeys(polygonsMap);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        requestNewImageIcon();
        try {
            drawScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // gc.drawImage(SwingFXUtils.toFXImage(bufferedImage, null), 0, 0);

    // // Drawing polygons and other elements goes here

    // mapView.setImage(SwingFXUtils.toFXImage(bufferedImage, null));

    private void drawScreen() throws IOException {
        // Load the background image
        GraphicsContext gc = canvas.getGraphicsContext2D();
        URL url = new URL(URL);
        BufferedImage bufferedImage = ImageIO.read(url);
        Image image = new Image(url.toString());
        gc.drawImage(image, 0, 0);

        // Draw the polygons
        Color lineColor = Color.BLACK;
        double startX = 0;
        double startY = 0;
        List<Double> xPoints = new ArrayList<>();
        List<Double> yPoints = new ArrayList<>();

        for (String postCode : polygonsMap.keySet()) {
            List<List<Double[]>> polygons = polygonsMap.get(postCode);
            Color polygonColor;

            if (scoresMap.get(postCode) != null) {
                int score = scoresMap.get(postCode);
                String[] split = colours.get(getColorIndexForScore(score, scoresMap, colours)).split(",");
                polygonColor = Color.rgb(Integer.parseInt(split[0]), Integer.parseInt(split[1]), 0, 0.5);
            } else {
                polygonColor = Color.rgb(255, 255, 255, 0.64);
            }

            if (polygons == null || polygons.isEmpty()) {
                continue;
            }

            gc.setStroke(lineColor);
            gc.setLineWidth(this.zoomedPostcode != null && this.zoomedPostcode.equals(postCode) ? 2 : 0.5);

            for (List<Double[]> polygon : polygons) {
                xPoints.clear();
                yPoints.clear();

                for (int i = 0; i < polygon.size(); i++) {
                    Double[] coordinates = polygon.get(i);
                    double lat = coordinates[0];
                    double lon = coordinates[1];

                    int[] xY = adjust(lon, lat, centerLongitude, centerLatitude, zoomLevel);

                    xPoints.add(xY[0] + MAP_WIDTH / 2);
                    yPoints.add(xY[1] + MAP_HEIGHT / 2);

                    if (i > 0) {
                        gc.strokeLine(startX + MAP_WIDTH / 2, startY + MAP_HEIGHT / 2, xY[0] + MAP_WIDTH / 2, xY[1] + MAP_HEIGHT / 2);
                    }

                    startX = xY[0];
                    startY = xY[1];
                }

                // xPoints.add(xPoints.get(0));
                // yPoints.add(yPoints.get(0));

                double[] xArray = xPoints.stream().mapToDouble(Double::doubleValue).toArray();
                double[] yArray = yPoints.stream().mapToDouble(Double::doubleValue).toArray();

                gc.setFill(polygonColor);
                gc.fillPolygon(xArray, yArray, xArray.length);
            }
        }
        Image canvasImage = canvas.snapshot(null, null);
        mapView.setImage(canvasImage);
    }

    private int[] adjust(double x, double y, double xcenter, double ycenter, int zoomlevel) {
        int xr = (lToX(x) - lToX(xcenter)) >> (21 - zoomlevel);
        int yr = (lToY(y) - lToY(ycenter)) >> (21 - zoomlevel);
        return new int[]{xr, yr};
    }

    private int lToX(double x) {
        return (int) (Math.round(offset + radius * x * Math.PI / 180));
    }

    private int lToY(double y) {
        return (int) (Math.round(offset - radius * Math.log((1 + Math.sin(y * Math.PI / 180)) / (1 - Math.sin(y * Math.PI / 180))) / 2));
    }

    private void doubleClickZoom(double x, double y) {
        double lonPerPixel = 360 / (256 * Math.pow(2, zoomLevel));
        double latPerPixel = 360 / (256 * Math.pow(2, zoomLevel));

        centerLongitude += (x - MAP_WIDTH / 2) * lonPerPixel;
        centerLatitude -= (y - MAP_HEIGHT / 2) * latPerPixel;

        zoomLevel++;
        requestNewImageIcon();
        try {
            drawScreen();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void centerToZipCode() {
        String zipCode = zipCodeField1.getText();
        zoomedPostcode = zipCode;
        ArrayList<Double> latLong;
        if (zipCode.isEmpty()) {
            return;
        }
        try {
            latLong = Data.getLatLong(zipCode);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        zoomLevel = 18;
        if (latLong == null) {
            return;
        }
        centerLongitude = latLong.get(1);
        centerLatitude = latLong.get(0);
    }

    private String requestNewImageIcon() {
        StringBuilder mapUrlBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/staticmap");
        mapUrlBuilder.append("?center=").append(centerLatitude).append(",").append(centerLongitude);
        mapUrlBuilder.append("&zoom=").append(zoomLevel);
        mapUrlBuilder.append("&size=600x600");
        mapUrlBuilder.append("&scale=").append(1);
        mapUrlBuilder.append("&key=").append(API_KEY);
        URL = mapUrlBuilder.toString();
        return URL;
    }

    private ArrayList<String> getGradientColors() {
        ArrayList<String> colors = new ArrayList<>();
        int steps = 256;

        for (int i = 0; i < steps; i++) {
            int r = 255;
            int g = i;
            int b = 0;
            colors.add(r + "," + g + "," + b);
        }

        for (int i = 0; i < steps; i++) {
            int r = 255 - i;
            int g = 255;
            int b = i;
            colors.add(r + "," + g + "," + b);
        }

        return colors;
    }

    private String[] extractKeys(Map<String, List<List<Double[]>>> polygonsMap) {
        Set<String> keys = polygonsMap.keySet();
        return keys.toArray(new String[0]);
    }
}
