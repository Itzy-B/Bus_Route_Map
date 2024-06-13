package src.java.GUI;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import src.java.Draw.DrawZipcodes;

public class AccessibilityDisplayerOld extends JFrame implements ActionListener {
    private double CENTER_LATITUDE = 50.866825678462234;
    private double CENTER_LONGITUDE = 5.65494508021394;
    final int R = 6371;
    private static final String API_KEY = "AIzaSyDnJH0pu5NzqH0b6GjiPyTDfdkBDugYw6w";
    private static int scale = 1;
    private static int zoomLevel = 14;
    private static JButton button;
    private JFrame frame;
    private List<ImageIcon> images;

    public AccessibilityDisplayerOld() {
        frame = new JFrame();
        frame.setTitle("Image Display");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        button = new JButton("Update");
        frame.add(button);
        frame.setVisible(true);
    }

    public StringBuilder createAPIStringBuilderTemplate(double CENTER_LATITUDE, double CENTER_LONGITUDE) {
        StringBuilder mapUrlBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/staticmap");
        // mapUrlBuilder.append("?center=Maastricht");
        mapUrlBuilder.append("?center=").append(CENTER_LATITUDE).append(",").append(CENTER_LONGITUDE);
        mapUrlBuilder.append("&zoom=").append(zoomLevel);
        mapUrlBuilder.append("&size=200x200");
        mapUrlBuilder.append("&scale=").append(scale);
        return mapUrlBuilder;
    }

    private static final double DEGREES_PER_METER_AT_EQUATOR = 360 / (2 * Math.PI * 6378137);
    private static final double MULTIPLIER = 0.915;
    private static final double TILE_PX = 400;

    private double calculateLatIncrement(double currentLat, double zoom) {
        double metresAtEquatorPerTilePx = 156543.03392 / Math.pow(2, zoom);
        return (DEGREES_PER_METER_AT_EQUATOR * Math.cos(currentLat * Math.PI / 180) * metresAtEquatorPerTilePx
                * TILE_PX) * MULTIPLIER;
    }

    private double calculateLonIncrement(double zoom) {
        double metresAtEquatorPerTilePx = 156543.03392 / Math.pow(2, zoom);
        return (DEGREES_PER_METER_AT_EQUATOR * metresAtEquatorPerTilePx * TILE_PX) * MULTIPLIER;
    }

    public List<ImageIcon> generateMapImages() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        frame.getContentPane().removeAll();
        frame.add(button);
        DrawZipcodes drawer = new DrawZipcodes();
        int startPos = 0;
        int endPosIncrement = 200;
        double latIncrement = 0;
        double lonIncrement = 0;
        double increment = 0.0351;
        List<ImageIcon> images = new ArrayList<ImageIcon>();
        for (int index = 0; index < 601; index += 200) {
            startPos = index;
            if (2783 - index == 183) {
                index = 2783;
                startPos = 2600;
                endPosIncrement = 183;
            }

            StringBuilder mapUrlBuilder = createAPIStringBuilderTemplate(CENTER_LATITUDE + latIncrement,
                    CENTER_LONGITUDE + lonIncrement);
            latIncrement += 0.0351;
            // latIncrement += calculateLatIncrement(CENTER_LATITUDE, zoomLevel);
            // lonIncrement += calculateLonIncrement(zoomLevel);
            // mapUrlBuilder.append(drawer.drawOnZipCodes(startPos, index +
            // endPosIncrement));
            // mapUrlBuilder.append("&style=feature:all|element:labels|visibility:off");
            mapUrlBuilder.append("&key=").append(API_KEY);
            System.out.println(mapUrlBuilder.toString());
            try {
                java.net.URL url = new URL(mapUrlBuilder.toString());
                ImageIcon imageIcon = new ImageIcon(url);
                // JLabel label = new JLabel(imageIcon);
                // frame.getContentPane().add(label);
                // BufferedImage bufferedImage = new BufferedImage(imageIcon.getIconWidth(),
                // imageIcon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
                // bufferedImage.getGraphics().drawImage(imageIcon.getImage(), 0, 0, null);
                // BufferedImage cropped = bufferedImage.getSubimage(50,50, 300,300);
                // imageIcon = new ImageIcon(cropped);

                System.out.println("added image");
                JLabel label = new JLabel(imageIcon);
                panel.add(label);
                frame.add(panel);
                frame.setVisible(true);
                images.add(imageIcon);
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // Load the image using ImageIcon

        }

        return images;

    }

    public void drawMaps(List<ImageIcon> images) {
        for (ImageIcon image : images) {
            JLabel label = new JLabel(image);
            frame.getContentPane().add(label);
        }
    }

    public static void main(String[] args) {
        AccessibilityDisplayerOld displayer = new AccessibilityDisplayerOld();
        List<ImageIcon> images = displayer.generateMapImages();
        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                displayer.generateMapImages();
            }

        });
        // displayer.drawMaps(images);
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
    }

}
