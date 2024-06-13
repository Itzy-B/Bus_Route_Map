package src.java.GUI;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import src.java.Draw.DrawZipcodes;

import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AccessibilityDisplayer extends JFrame implements ActionListener{
    private double CENTER_LATITUDE = 50.866825678462234;
    private double CENTER_LONGITUDE = 5.65494508021394;
    final int R = 6371;
    private static final String API_KEY = "AIzaSyDnJH0pu5NzqH0b6GjiPyTDfdkBDugYw6w";
    private static int scale = 1;
    private static int zoomLevel = 14;
    private static JButton button;
    private JFrame frame;
    private List<ImageIcon> images;

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

    public void drawPoints() {

    }

    public static void main(String[] args) {
        AccessibilityDisplayer displayer = new AccessibilityDisplayer();
    }
    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
    }

}
