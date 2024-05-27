package src.java.GUI;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BusStop extends Place {
    private int stopId;
    private String name;
    private List<Trip> trips;

    public BusStop(int stopId, String name, double lat, double lon) {
        super(lat, lon);
        this.stopId = stopId;
        this.name = name;
        this.trips = new ArrayList<>();
    }

    public void addTrip(int tripId, String tripHeadsign, int stopSequence, double shapeDistTraveled) {
        this.trips.add(new Trip(tripId, tripHeadsign, stopSequence, shapeDistTraveled));
    }

    public int getStopId() {
        return stopId;
    }

    public String getName() {
        return name;
    }

    public List<Trip> getTrips() {
        return trips;
    }

    @Override
    public String toString() {
        return name + " (Bus Stop ID: " + stopId + ") at (" + lat + ", " + lon + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BusStop busStop = (BusStop) o;
        return Double.compare(busStop.lat, lat) == 0 &&
                Double.compare(busStop.lon, lon)  == 0 &&
                busStop.stopId == stopId &&
                name.equals(busStop.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stopId, name, lat, lon);
    }

}
