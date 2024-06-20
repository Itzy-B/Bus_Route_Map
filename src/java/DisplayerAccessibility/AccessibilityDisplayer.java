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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import src.java.Singletons.FileManager;

public class AccessibilityDisplayer extends JFrame implements ActionListener{
    private final double MAP_WIDTH = 600;
    private final double MAP_HEIGHT = 600;
    private double centerLatitude = 50.851368;
    private double centerLongitude = 5.690973;
    Map<String, List<List<Double[]>>> polygonsMap = new HashMap<>();
    private int zoomLevel = 13;
    private String zoomedPostcode;
    private JButton updateButton = new JButton("Update");
    private JButton zoomInButton = new JButton("+");
    private JButton zoomOutButton = new JButton("-");
    private JTextField zipCodeField1 = new JTextField();
    private ArrayList<String> zipCodes = Data.getZipCodes();
    private String[] zipCodesAll;
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
        g.setStroke(new BasicStroke(1));
        g.setColor(new Color(255,255,255,64));
        g.setStroke(originalStroke);
        ImageIcon imageIcon = new ImageIcon(bufferedImage);
        JLabel label = new JLabel(imageIcon);
        int lengthColours = colours.size();
        int colorIndex = 0;
        
        double startX = 0;
        double startY = 0;
        ArrayList<Integer> xPoints = new ArrayList<>();
        ArrayList<Integer> yPoints = new ArrayList<>();

        // ArrayList<ArrayList<Double[]>> list = parser.getPolyGon();
        int iteratorColors = lengthColours / zipCodesAll.length;
        //Change this.zipCodesAll to zipCodes to draw only the zipcodes from MassZipLatLon.xldx
        for (String postCode: this.zipCodesAll) {
            List<List<Double[]>> polygons = polygonsMap.get(postCode);
            if (polygons == null || polygons.get(0).isEmpty()) {
                //Postcodes with no polygons in db, so we just draw a circle, give it later a relevant color
                //Cover only the Maastricht postcodes
                if(postCode.equals("6211BM") || postCode.equals("6227CC")) {
                    ArrayList<Double> latLong = Data.getLatLong(postCode);
                    int[] Xy = adjust(latLong.get(1), latLong.get(0), centerLongitude, centerLatitude, zoomLevel);
                    g.setColor(new Color(0,0,0, 50));
                    g.fillOval((int) (Xy[0] + MAP_WIDTH / 2 - 5), (int) (Xy[1] + MAP_HEIGHT / 2 -5), 10, 10);
                }
                continue;
            }

            String[] split = colours.get(colorIndex).split(",");
            if (colorIndex+ iteratorColors <= colours.size()) {
                colorIndex+= iteratorColors;
            }
            // Color color = new Color(Integer.parseInt(split[0]),Integer.parseInt(split[1]),0, 255);
            Color color = new Color(0,0,0, 255);
            Color polygonColor = new Color(Integer.parseInt(split[0]),Integer.parseInt(split[1]),0, 64);
            g.setColor(color);




            for(List<Double[]> polygon: polygons) {
                if(this.zoomedPostcode != null && this.zoomedPostcode.equals(postCode)) {
                    // g.setStroke(new BasicStroke((int) (5 * (zoomLevel /18))));
                    g.setStroke(new BasicStroke((float) 2));

                    color = new Color(0,0,0);
                    g.setColor(color);
                }
                else {
                    g.setStroke(new BasicStroke((float) 0.5));
                }
                xPoints.clear();
                yPoints.clear();



                // if(index > -1 && index < 2783) {
                //     // int[] Xy = adjust(longs.get(index), lats.get(index), centerLongitude, centerLatitude, zoomLevel);
                //     g.setColor(new Color(0,0,0, 50));
                //     g.fillOval((int) (Xy[0] + MAP_WIDTH / 2 - 5), (int) (Xy[1] + MAP_HEIGHT / 2 -5), 5, 5);
                //     continue;
                // }
                
                for (int i = 0; i < polygon.size(); i++) {
                    Double[] coordinates = polygon.get(i);
                    double lat = coordinates[0];
                    double lon = coordinates[1];
                    
                    int[] Xy = adjust(lon, lat, centerLongitude, centerLatitude, zoomLevel);
                    
                    xPoints.add(Xy[0]);
                    yPoints.add(Xy[1]);
                    
                    if (i > 0) {
                        g.drawLine(
                            (int) (startX + MAP_WIDTH / 2 ),
                            (int) (startY + MAP_HEIGHT / 2 ),
                            (int) (Xy[0] + MAP_WIDTH / 2 ),
                            (int) (Xy[1] + MAP_HEIGHT / 2 )
                        );
                    }
                    
                    startX = Xy[0];
                    startY = Xy[1];
                }

                if(xPoints.size() == 0) {
                    System.out.println("");
                } 
            
                xPoints.add(xPoints.get(0));
                yPoints.add(yPoints.get(0));
            
                //https://stackoverflow.com/questions/718554/how-to-convert-an-arraylist-containing-integers-to-primitive-int-array
                int[] xArray = xPoints.stream().mapToInt(i -> i).toArray();
                int[] yArray = yPoints.stream().mapToInt(i -> i).toArray();
            
                g.setColor(polygonColor);
                g.fillPolygon(
                    //https://stackoverflow.com/questions/71495980/java-8-stream-add-1-to-each-element-and-remove-if-element-is-5-in-the-list
                    Arrays.stream(xArray).map(x -> x + (int) MAP_WIDTH / 2 ).toArray(),
                    Arrays.stream(yArray).map(y -> y + (int) MAP_HEIGHT / 2 ).toArray(),
                    xArray.length
                );
            
                xPoints.clear();
                yPoints.clear();
            }
        }

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
        displayer.zoomedPostcode = zipCode;
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
        if (latLong == null) {
            JOptionPane.showMessageDialog(null, "No coordinates could be found for zipcode", "Error", JOptionPane.ERROR_MESSAGE);
        }
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
        try {
            displayer.polygonsMap = (Map<String, List<List<Double[]>>>) FileManager.getInstance().getObject("polygonsMap.ser");
            displayer.zipCodesAll = extractKeys(displayer.polygonsMap);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        displayer.URL = requestNewImageIcon();
        createActionListeners(displayer);
        try {
            displayer.drawScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String[] extractKeys(Map<String, List<List<Double[]>>> polygonsMap) {
        Collection<String> keys = polygonsMap.keySet();
        return keys.toArray(new String[keys.size()]);
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        //Is empty to be able to compile. Java doesn't see nested actionPerformed functions for some reason
    }
}