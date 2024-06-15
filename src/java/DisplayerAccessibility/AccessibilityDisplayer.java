package src.java.DisplayerAccessibility;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import src.java.GUI.Data;

public class AccessibilityDisplayer extends JFrame implements ActionListener{
    private final double MAP_WIDTH = 600;
    private final double MAP_HEIGHT = 600;
    private double centerLatitude = 50.851368;
    private double centerLongitude = 5.690973;
    private int zoomLevel = 13;
    private JButton updateButton = new JButton("Update");
    private JButton zoomInButton = new JButton("+");
    private JButton zoomOutButton = new JButton("-");
    private JTextField zipCodeField1 = new JTextField();
    private ArrayList<String> zipCodes = Data.getZipCodes();
    private ArrayList<Double> lats = Data.getLatitudes();
    private ArrayList<Double> longs = Data.getLongitudes();
    private ArrayList<String> colours = getGradientColors();
    private final String API_KEY = "AIzaSyAZwfzWK71qIgXSleA-02n-oXfo5OjOhhU";
    private JPanel panel = new JPanel();
    private final double offset = 268435456;
    private final double radius = offset / Math.PI;
    private JFrame frame;
    private String URL;
    private int prevX, prevY;

    public AccessibilityDisplayer(boolean start) {
        frame = new JFrame();
        frame.setTitle("Image Display");
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
    
        ImageIcon imageIcon = new ImageIcon("./staticmap.png");
        JLabel label = new JLabel(imageIcon);
        zipCodeField1.setPreferredSize(new Dimension(150, 30));
        TextPrompt textPrompt = new TextPrompt("Enter a zipcode to center", zipCodeField1);
        Random rand = new Random();
        frame.add(updateButton);
        frame.add(zoomInButton);
        frame.add(zoomOutButton);
        frame.add(zipCodeField1);
        frame.add(textPrompt);
        frame.add(panel);
        panel.add(label);

        frame.setVisible(true);

    }

    public AccessibilityDisplayer() {
    }

    public void addMouseListenersPanning(JLabel label) {
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                prevX = e.getX();
                prevY = e.getY();
            }
        });

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                prevX = e.getX();
                prevY = e.getY();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    doubleClickZoom(e.getX(), e.getY());
                }
            }
        });

        label.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                double mouseSensitivity= 10.0;
                int deltaX = e.getX() - prevX;
                int deltaY = e.getY() - prevY;
                prevX = e.getX();
                prevY = e.getY();

                double lonChange = (double) deltaX / MAP_WIDTH * 360 / Math.pow(2, zoomLevel) * mouseSensitivity;
                double latChange = (double) deltaY / MAP_HEIGHT * 360 / Math.pow(2, zoomLevel) * mouseSensitivity;
                centerLongitude -= lonChange;
                centerLatitude += latChange;

                requestNewImageIcon();
                try {
                    drawScreen();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            
        });
    }

    public void doubleClickZoom(int x, int y) {
        double lonPerPixel = 360 / (256 * Math.pow(2, zoomLevel));
        double latPerPixel = 360 / (256 * Math.pow(2, zoomLevel));
        
        centerLongitude += (x - MAP_WIDTH / 2) * lonPerPixel;
        centerLatitude -= (y - MAP_HEIGHT / 2) * latPerPixel;
        
        zoomLevel++;
        requestNewImageIcon();
        try {
            drawScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void drawScreen() throws IOException {
        panel.removeAll();
        URL url = new URL(URL);
        java.awt.Image image = ImageIO.read(url);
        BufferedImage bufferedImage = (BufferedImage) image;
        bufferedImage.flush();
        Graphics2D g = (Graphics2D) bufferedImage.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Stroke originalStroke = g.getStroke();
        g.setStroke(new BasicStroke(5));
        g.setColor(new Color(255,0,0,64));
        g.setStroke(originalStroke);
        ImageIcon imageIcon = new ImageIcon(bufferedImage);
        JLabel label = new JLabel(imageIcon);
        int lengthColours = colours.size();
        int colorIndex = 0;
        
        RDToWGS84 parser = new RDToWGS84();
        // ArrayList<Double[]> list = parser.
        double startX = 0;
        double startY = 0;
        ArrayList<Integer> xPoints = new ArrayList<>();
        ArrayList<Integer> yPoints = new ArrayList<>();
        ArrayList<ArrayList<Double[]>> list = RDToWGS84.getPolyGon();
        int iteratorColors = lengthColours / list.size();
        for (ArrayList<Double[]> polygon : list) {
            xPoints.clear(); // Clear previous points for each new polygon
            yPoints.clear(); // Clear previous points for each new polygon

            if (polygon.get(0)[0] == -1) {
                System.out.println("we have a nuller");
            }
            
            for (int i = 0; i < polygon.size(); i++) {
                Double[] coordinates = polygon.get(i);
                double lat = coordinates[0];
                double lon = coordinates[1];
                
                // Calculate the adjusted coordinates based on the center and zoom level
                int[] Xy = adjust(lon, lat, centerLongitude, centerLatitude, zoomLevel);
                
                // Add the X and Y coordinates to the lists
                xPoints.add(Xy[0]);
                yPoints.add(Xy[1]);
                
                // Draw a line between the current and previous points
                if (i > 0) {
                    g.drawLine(
                        (int) (startX + MAP_WIDTH / 2 - 5),
                        (int) (startY + MAP_HEIGHT / 2 - 5),
                        (int) (Xy[0] + MAP_WIDTH / 2 - 5),
                        (int) (Xy[1] + MAP_HEIGHT / 2 - 5)
                    );
                }
                
                // Update the starting point for the next iteration
                startX = Xy[0];
                startY = Xy[1];
            }
        
            // Close the shape by connecting the last point back to the first point
            xPoints.add(xPoints.get(0));
            yPoints.add(yPoints.get(0));
        
            // Convert ArrayList<Integer> to int[] using stream()
            int[] xArray = xPoints.stream().mapToInt(i -> i).toArray();
            int[] yArray = yPoints.stream().mapToInt(i -> i).toArray();
        
            // Fill the polygon with a color
            String[] split = colours.get(colorIndex).split(",");
            colorIndex+= iteratorColors;
            Color color = new Color(Integer.parseInt(split[0]),Integer.parseInt(split[1]),0, 64);
            g.setColor(color); // Set the color you want to fill with
            g.fillPolygon(
                Arrays.stream(xArray).map(x -> x + (int) MAP_WIDTH / 2 - 5).toArray(),
                Arrays.stream(yArray).map(y -> y + (int) MAP_HEIGHT / 2 - 5).toArray(),
                xArray.length
            );
        
            // Optionally, reset xPoints and yPoints for the next polygon
            xPoints.clear();
            yPoints.clear();
        }



        
        // for (int index = 0; index < zipCodes.size(); index++) {
        //     String[] split = colours.get(colorIndex).split(",");
        //     colorIndex+= iteratorColors;
        //     Color color = new Color(Integer.parseInt(split[0]),Integer.parseInt(split[1]),0, 64);
        //     // Color randomColor = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256), 64);
        //     g.setColor(color);
        //     double zipLat = lats.get(index);
        //     double zipLon = longs.get(index);
        //     int[] Xy = adjust(zipLon, zipLat, centerLongitude, centerLatitude, zoomLevel);
        //     g.fillOval((int) (Xy[0] + MAP_WIDTH / 2 - 5), (int) (Xy[1] + MAP_HEIGHT / 2 - 5), 10, 10);
        // }

        imageIcon = new ImageIcon(bufferedImage);
        label.setIcon(imageIcon);
        addMouseListenersPanning(label);
        panel.add(label);
        frame.setVisible(true);
    }

    // X,Y ... location in degrees
    // xcenter,ycenter ... center of the map in degrees (same value as in
    // the google static maps URL)
    // zoomlevel (same value as in the google static maps URL)
    // xr, yr and the returned Point ... position of X,Y in pixels relative
    // to the center of the bitmap

    // https://stackoverflow.com/questions/23898964/getting-pixel-coordinated-from-google-static-maps
    public int[] adjust(double X, double Y, double xcenter, double ycenter, int zoomlevel) {
        int xr = (lToX(X) - lToX(xcenter)) >> (21 - zoomlevel);
        int yr = (lToY(Y) - lToY(ycenter)) >> (21 - zoomlevel);
        return new int[] { xr, yr };
    }

    public int lToX(double x) {
        return (int) (Math.round(offset + radius * x * Math.PI / 180));
    }

    public int lToY(double y) {
        return (int) (Math.round(
                offset - radius * Math.log((1 + Math.sin(y * Math.PI / 180)) / (1 - Math.sin(y * Math.PI / 180))) / 2));
    }

    public static void main(String[] args) {
        AccessibilityDisplayer displayer = new AccessibilityDisplayer();
        displayer.runAccessibilityDisplayer();
    }

    public ArrayList<String> getGradientColors() {
        ArrayList<String> colors = new ArrayList<>();
        for (int r = 255; r >= 0; r--) {
            for (int g = 0; g <= 255; g++) {
                colors.add(r + "," + g + ",0");
            }
        }
        return colors;
    }

    public String requestNewImageIcon() {
        StringBuilder mapUrlBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/staticmap");
        mapUrlBuilder.append("?center=").append(centerLatitude).append(",").append(centerLongitude);
        mapUrlBuilder.append("&zoom=").append(zoomLevel);
        mapUrlBuilder.append("&size=600x600");
        mapUrlBuilder.append("&scale=").append(1);
        mapUrlBuilder.append("&key=").append(API_KEY);
        URL = mapUrlBuilder.toString();
        return mapUrlBuilder.toString();
    }

    public void centerToZipCode(AccessibilityDisplayer displayer) {
        String zipCode = displayer.zipCodeField1.getText();
        ArrayList<Double> latLong = new ArrayList<>();
        if (zipCode.length() == 0) {
            JOptionPane.showMessageDialog(null, "No zipcode has been entered", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            latLong = Data.getLatLong(zipCode);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "No coordinates could be found for zipcode", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        displayer.zoomLevel = 18;
        displayer.centerLongitude = latLong.get(1);
        displayer.centerLatitude = latLong.get(0);

    }

    public void createActionListeners(AccessibilityDisplayer displayer) {
        displayer.zoomInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                displayer.zoomLevel++;
                displayer.requestNewImageIcon();
                try {
                    displayer.drawScreen();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        displayer.updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                centerToZipCode(displayer);
                displayer.requestNewImageIcon();
                try {
                    displayer.drawScreen();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        displayer.zoomOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                displayer.zoomLevel--;
                displayer.requestNewImageIcon();
                try {
                    displayer.drawScreen();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public void runAccessibilityDisplayer() {
        Data.getData();
        AccessibilityDisplayer displayer = new AccessibilityDisplayer(true);
        displayer.URL = requestNewImageIcon();
        createActionListeners(displayer);
        try {
            displayer.drawScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        //Is empty to be able to compile. Java doesn't see nested actionPerformed functions for some reason
    }
}