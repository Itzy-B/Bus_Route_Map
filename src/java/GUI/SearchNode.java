package src.java.GUI;

import java.util.Objects;

public class SearchNode {
    Place place;
    long g; // Cost from start to this node
    long h; // Heuristic cost from this node to goal
    long f; // g + h

    int dist;
    SearchNode cameFrom;
    Trip trip;

    SearchNode(Place place, long g, long h, int dist, SearchNode cameFrom, Trip trip) {
        this.place = place;
        this.g = g;
        this.h = h;
        this.f = g + h;
        this.cameFrom = cameFrom;
        this.trip = trip;
        this.dist = dist;
    }

    public long getG() {return g;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchNode searchNode = (SearchNode) o;
        return place.equals(searchNode.place);
    }

    @Override
    public int hashCode() {
        return Objects.hash(place.hashCode(), g, h);
    }
}
