package src.java.GUI;

import java.util.Objects;

public class Edge {
    private final Place from;
    private final Place to;
    private final double weight;
    private final String tripHeadsign;

    public Edge(Place from, Place to, double weight, String tripHeadsign) {
        this.from = from;
        this.to = to;
        this.weight = weight;
        this.tripHeadsign = tripHeadsign;
    }

    public Place getFrom() {
        return from;
    }

    public Place getTo() {
        return to;
    }

    public double getWeight() {
        return weight;
    }

    public String getTripHeadsign() {
        return tripHeadsign;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return Double.compare(edge.weight, weight) == 0 &&
                from.equals(edge.from) &&
                to.equals(edge.to) &&
                Objects.equals(tripHeadsign, edge.tripHeadsign);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, weight, tripHeadsign);
    }

    @Override
    public String toString() {
        return "Edge{" +
                "from=" + from +
                ", to=" + to +
                ", weight=" + weight +
                ", tripHeadsign='" + tripHeadsign + '\'' +
                '}';
    }
}
