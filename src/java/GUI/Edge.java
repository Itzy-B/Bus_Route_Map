package src.java.GUI;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Edge implements Serializable {
    private Place from;
    private Place to;
    private String tripHeadSign;
    private List<Trip> trips;

    private long walkingTime;
    private int walkingDist;

    public Edge(Place from, Place to, String tripHeadSign) {
        this.from = from;
        this.to = to;
        this.tripHeadSign = tripHeadSign;
        this.trips = new ArrayList<>();
    }

    public Edge(Place from, Place to, long walkingTime, int walkingDist, String tripHeadSign) {
        this.from = from;
        this.to = to;
        this.tripHeadSign = tripHeadSign;
        this.walkingTime = walkingTime;
        this.walkingDist = walkingDist;
        this.trips = new ArrayList<>();
    }

    public void addTrip(Trip trip) {
        trips.add(trip);
    }

    public List<Trip> getTrips() {
        return trips;
    }
    public Place getFrom() {
        return from;
    }

    public Place getTo() {
        return to;
    }

    public String getTripHeadSign() {
        return tripHeadSign;
    }

    public long getWalkingTime() {return walkingTime;}

    public int getWalkingDist() {return walkingDist;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return from.equals(edge.from) &&
                to.equals(edge.to) &&
                tripHeadSign.equals(edge.tripHeadSign);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, tripHeadSign, trips.size());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Edge{")
                .append("from=").append(from)
                .append(", to=").append(to)
                .append(", tripHeadSign='").append(tripHeadSign).append('\'')
                .append(", trips=[");
        for (Trip trip : trips) {
            sb.append("\n    ").append(trip.toString());
        }
        sb.append("\n  ]}");
        return sb.toString();
    }
}
