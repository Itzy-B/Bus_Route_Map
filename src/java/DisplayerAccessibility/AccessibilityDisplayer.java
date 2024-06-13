package src.java.DisplayerAccessibility;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import src.java.GUI.Data;

public class AccessibilityDisplayer extends JFrame implements ActionListener{
    private static final double MAP_WIDTH = 600; // Width of your map image
    private static final double MAP_HEIGHT = 600;
    private double centerLatitude = 50.851368;
    private double centerLongitude = 5.690973;
    private static int zoomLevel = 13;
    private static JButton button;
    private static JButton zoomInButton = new JButton("+");
    private static JButton zoomOutButton = new JButton("-");
    private static final String API_KEY = "AIzaSyDnJH0pu5NzqH0b6GjiPyTDfdkBDugYw6w";
    static double offset = 268435456; 
    static double radius = offset / Math.PI;
    private JFrame frame;
    private static String URL;

    public AccessibilityDisplayer(boolean start) {
        frame = new JFrame();
        frame.setTitle("Image Display");
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        button = new JButton("Update");
        JPanel panel = new JPanel();
        ImageIcon imageIcon = new ImageIcon("./staticmap.png");
        JLabel label = new JLabel(imageIcon);

        frame.add(zoomInButton);
        frame.add(zoomOutButton);
        panel.add(label);
        frame.add(panel);

        frame.add(button);
        frame.setVisible(true);
    }

    public AccessibilityDisplayer() {
    }

    public void getCircle() throws IOException {
        ArrayList<String> zipCodes = Data.getZipCodes();
        ArrayList<Double> lats = Data.getLatitudes();
        ArrayList<Double> longs = Data.getLongitudes();
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        frame.getContentPane().removeAll();
        frame.add(button);
        frame.add(zoomInButton);
        frame.add(zoomOutButton);
        frame.add(panel);
        URL url = new URL(URL);
        java.awt.Image image = ImageIO.read(url);
        BufferedImage bufferedImage = (BufferedImage) image;
        // BufferedImage bufferedImage =  ImageIO.read(new File("./staticmap.png"));
        bufferedImage.flush();
        Graphics2D g = (Graphics2D) bufferedImage.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Stroke originalStroke = g.getStroke();
        g.setStroke(new BasicStroke(5));
        g.setColor(new Color(255, 0, 0, 64));
        g.setStroke(originalStroke);
        ImageIcon imageIcon = new ImageIcon(bufferedImage);
        JLabel label = new JLabel(imageIcon);
    
        for (int index = 0; index < zipCodes.size(); index++) {
            if (index > 2770 && index < 2783) {
                System.out.println("");
            }
            double zipLat = lats.get(index);
            double zipLon = longs.get(index);
            int[] Xy = adjust(zipLon, zipLat, centerLongitude,centerLatitude,zoomLevel);
            g.fillOval((int)(Xy[0] + MAP_WIDTH/2 -5), (int) (Xy[1] + MAP_HEIGHT/2 -5), 10, 10);
        }
        
        imageIcon = new ImageIcon(bufferedImage);
        label.setIcon(imageIcon);
        panel.add(label);
        frame.setVisible(true);
    }

    // X,Y ... location in degrees
    // xcenter,ycenter ... center of the map in degrees (same value as in 
    // the google static maps URL)
    // zoomlevel (same value as in the google static maps URL)
    // xr, yr and the returned Point ... position of X,Y in pixels relative 
    // to the center of the bitmap

    //https://stackoverflow.com/questions/23898964/getting-pixel-coordinated-from-google-static-maps
    public int[] adjust(double X, double Y, double xcenter, double ycenter, int zoomlevel) {
        int xr = (lToX(X) - lToX(xcenter)) >> (21 - zoomlevel);
        int yr = (lToY(Y) - lToY(ycenter)) >> (21 - zoomlevel);
        return new int[]{xr, yr};
    }

    public static int lToX(double x) {
        return (int)(Math.round(offset + radius * x * Math.PI / 180));
    }

    public static int lToY(double y) {
        return (int)(Math.round(offset - radius * Math.log((1 + Math.sin(y * Math.PI / 180)) / (1 - Math.sin(y * Math.PI / 180))) / 2));
    }
    
    public static void main(String[] args) {
        AccessibilityDisplayer displayer = new AccessibilityDisplayer();
        displayer.runAccessibilityDisplayer();
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

    public void createActionListeners(AccessibilityDisplayer displayer) {
        zoomInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                zoomLevel++; 
                displayer.requestNewImageIcon();
                try {
                    displayer.getCircle();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            });

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                try {
                    displayer.getCircle();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            });


        zoomOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                zoomLevel--;
                displayer.requestNewImageIcon();
                try {
                    displayer.getCircle();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            });
    }
    
    public void runAccessibilityDisplayer() {
        Data.getData();
        AccessibilityDisplayer displayer = new AccessibilityDisplayer(true);
        URL = requestNewImageIcon();
        createActionListeners(displayer);
        try {
            displayer.getCircle();
        } catch (IOException e) {
            e.printStackTrace();
        }
        }

        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
    }
}
