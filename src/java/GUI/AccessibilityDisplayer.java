package src.java.GUI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import javafx.scene.image.Image;

public class AccessibilityDisplayer extends JFrame implements ActionListener{
    private static final double MAP_WIDTH = 600; // Width of your map image
    private static final double MAP_HEIGHT = 600;
    private double CENTER_LATITUDE = 50.851368;
    private double CENTER_LONGITUDE = 5.690973;
    private static int zoomLevel = 13;
    private static JButton button;
    private JPanel panel;
    static double offset = 268435456; 
    static double radius = offset / Math.PI;
    private JFrame frame;

    public AccessibilityDisplayer() {
        frame = new JFrame();
        frame.setTitle("Image Display");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        button = new JButton("Update");
        JPanel panel = new JPanel();
        ImageIcon imageIcon = new ImageIcon("./staticmap.png");
        JLabel label = new JLabel(imageIcon);
        panel.add(label);
        frame.add(panel);

        frame.add(button);
        frame.setVisible(true);
    }

    public void getCircle() throws IOException {
        Data.getData();
        ArrayList<String> zipCodes = Data.getZipCodes();
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        frame.getContentPane().removeAll();
        frame.add(button);
        frame.add(panel);
        BufferedImage bufferedImage =  ImageIO.read(new File("./staticmap.png"));
        Graphics2D g = (Graphics2D) bufferedImage.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Stroke originalStroke = g.getStroke();
        g.setStroke(new BasicStroke(5));
        g.setColor(new Color(255, 0, 0, 64));
        g.setStroke(originalStroke);
        ImageIcon imageIcon = null;
        JLabel label = new JLabel(imageIcon);
        long startTime = System.currentTimeMillis();
    
        for (int index = 0; index < zipCodes.size(); index++) {
            if (index % 100 == 0) {
                System.out.println(index);
                imageIcon = new ImageIcon(bufferedImage);
                label.setIcon(imageIcon);
                panel.add(label);
                frame.setVisible(true); 
                java.awt.Image image = imageIcon.getImage();
                bufferedImage = (BufferedImage) image;
                g = (Graphics2D) bufferedImage.getGraphics();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                originalStroke = g.getStroke();
                g.setStroke(new BasicStroke(5));
                g.setColor(new Color(255, 0, 0, 64));
                g.setStroke(originalStroke);
            }
            Place coordinatesZipCode = new Place(zipCodes.get(index));
            int[] Xy = adjust(coordinatesZipCode.getLongitude(), coordinatesZipCode.getLatitude(), CENTER_LONGITUDE,CENTER_LATITUDE,zoomLevel);
            // int[] Xy = adjust(50.83702537886311, 5.69366003053462,CENTER_LATITUDE, CENTER_LONGITUDE,zoomLevel);
            g.fillOval((int)(Xy[0] + MAP_WIDTH/2 -5), (int) (Xy[1] + MAP_HEIGHT/2 -5), 10, 10);
        }

        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime); // Calculate the duration in milliseconds
        System.out.println("Execution time in milliseconds: " + duration/1000);
        
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
        try {
            displayer.getCircle();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
    }
    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
    }

}
