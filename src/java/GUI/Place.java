package src.java.GUI;

public class Place {
    private String zipCode;
    private double latitude;
    private double longitude;

    public Place(double latitude, double longitude, String zipCode) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.zipCode = zipCode;
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
