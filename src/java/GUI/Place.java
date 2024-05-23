package src.java.GUI;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Represents a geographic place identified by a zip code, latitude, and longitude.
 */
public class Place {
    private String zipCode;
    private double latitude;
    private double longitude;


    /**
     * Constructs a Place object with the provided zip code.
     *
     * @param zipCode The zip code of the place.
     * @throws IOException If there is an error while retrieving data.
     */
    public Place(String zipCode) throws IOException {
        this.zipCode = zipCode;
        Data data = new Data();
        data.getData();
        ArrayList<Double> LatLong = data.getLatLong(this.zipCode);
        this.latitude = LatLong.get(0);
        this.longitude = LatLong.get(1);

    }

    public Place(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
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
