package src.java.GUI;

import java.time.LocalTime;

public class Trip implements Comparable<Trip> {
    private int tripId;
    private String tripHeadSign;
    private int shapeDistTraveled;

    private LocalTime arriveTime;
    private LocalTime departureTime;

    private int shapeId;

    public Trip(int tripId,  LocalTime arriveTime, LocalTime departureTime, String tripHeadSign, int shapeDistTraveled, int shapeId) {
        this.tripId = tripId;
        this.tripHeadSign = tripHeadSign;
        this.shapeDistTraveled = shapeDistTraveled;
        this.arriveTime = arriveTime;
        this.departureTime = departureTime;
        this.shapeId = shapeId;
    }

    public int getShapeDistTraveled() {return shapeDistTraveled;}

    public LocalTime getArriveTime() {return arriveTime;}

    public LocalTime getDepartureTime() {return departureTime;}

    public int getShapeId() {return shapeId;}

    public String getTripHeadSign() {return tripHeadSign;}

    @Override
    public int compareTo(Trip that) {
        return this.arriveTime.compareTo(that.arriveTime);
    }

    @Override
    public String toString() {
        return "Trip{" +
                "tripId=" + tripId +
                ", tripHeadSign='" + tripHeadSign + '\'' +
                ", shapeDistTraveled=" + shapeDistTraveled +
                ", arriveTime=" + arriveTime +
                ", departureTime=" + departureTime +
                ", shapeId=" + shapeId +
                '}';
    }
}
