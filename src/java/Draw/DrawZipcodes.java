package src.java.Draw;

import java.io.IOException;
import java.util.ArrayList;

import src.java.GUI.Data;
import src.java.GUI.Place;

public class DrawZipcodes {
    public StringBuilder drawOnZipCodes(int start, int end) {
        StringBuilder urlBuilder = new StringBuilder();
        Data.getData();
        ArrayList<String> zipCodes = Data.getZipCodes();
        DrawSquare drawer = new DrawSquare();
        for (int index = start; index < end; index++) {
            try {
                Place coordinatesZipCode = new Place(zipCodes.get(index));
                urlBuilder.append("&path=fillcolor:0xff0000ff%7Cweight:3%7Cenc:");
                urlBuilder.append(drawer.drawSquare(coordinatesZipCode.getLatitude(), coordinatesZipCode.getLongitude(), 0.1));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return urlBuilder;
    }
}