package src.java.DisplayerAccessibility;

import java.util.ArrayList;
import java.util.List;

import src.java.GUI.Place;
import src.java.GUI.PolylineEncoder;

public class DrawSquare {
    public String drawSquare(double lat, double lon, double size) {
        List<Place> places = new ArrayList<Place>();
        double latAdjustment = size / 2 / 111.320; //Some math for sphere adjustment. Figure out how math works later when are going to use this.
        double lonAdjustment = size / 2 / (111.320 * Math.cos(Math.toRadians(lat)));
        places.add(new Place(lat, lon + lonAdjustment));
        places.add(new Place(lat + latAdjustment, lon));
        places.add(new Place(lat, lon - lonAdjustment));
        places.add(new Place(lat - latAdjustment, lon));
        places.add(new Place(lat, lon + lonAdjustment));

        return PolylineEncoder.encode(places);
    }
}