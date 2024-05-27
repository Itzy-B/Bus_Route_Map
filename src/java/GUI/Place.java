package src.java.GUI;

import src.java.Main.CalculateDistance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents a geographic place identified by a zip code, latitude, and longitude.
 */
public class Place {
    protected String zipCode;
    protected double lat;
    protected double lon;


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
        this.lat = LatLong.get(0);
        this.lon = LatLong.get(1);

    }

    public Place(double latitude, double longitude) {
        this.lat = latitude;
        this.lon = longitude;
    }

    public double distanceTo(double lat, double lon) {
        return CalculateDistance.distanceBetween(this.lat, this.lon, lat, lon);
    }

    public double getLatitude() {
        return lat;
    }
    public double getLongitude() {
        return lon;
    }
    public String getZipCode() {
        return zipCode;
    }

    @Override
    public String toString() {
        return  "place at (" + getLatitude() + ", " + getLongitude() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Place place = (Place) o;
        return Double.compare(place.lat, lat) == 0 && Double.compare(place.lon, lon) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lat, lon);
    }
}
