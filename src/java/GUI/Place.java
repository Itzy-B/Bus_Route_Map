package src.java.GUI;

import java.io.IOException;
import java.util.ArrayList;

public class Place {
    private String zipCode;
    private double latitude;
    private double longitude;

    public Place(String zipCode) throws IOException {
        this.zipCode = zipCode;
        Data data = new Data();
        data.getData();
        ArrayList<Double> LatLong = data.getLatLong(this.zipCode);
        this.latitude = LatLong.get(0);
        this.longitude = LatLong.get(1);

    }

    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public String getZipCode() {
        return zipCode;
    }
}
