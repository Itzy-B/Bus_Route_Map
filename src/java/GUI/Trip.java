package src.java.GUI;

public class Trip {
    private int tripId;
    private String tripHeadsign;
    private int stopSequence;
    private double shapeDistTraveled;

    public Trip(int tripId, String tripHeadsign, int stopSequence, double shapeDistTraveled) {
        this.tripId = tripId;
        this.tripHeadsign = tripHeadsign;
        this.stopSequence = stopSequence;
        this.shapeDistTraveled = shapeDistTraveled;
    }

    public int getTripId() {
        return tripId;
    }

    public String getTripHeadsign() {
        return tripHeadsign;
    }

    public int getStopSequence() {
        return stopSequence;
    }

    public double getShapeDistTraveled() {
        return shapeDistTraveled;
    }
}
