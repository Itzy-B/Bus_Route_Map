package src.java.GUI;

import java.util.Objects;

public class Node {
    Place place;
    double g; // Cost from start to this node
    double h; // Heuristic cost from this node to goal
    double f; // g + h
    Node cameFrom;
    String tripHeadsign;

    Node(Place place, double g, double h, Node cameFrom, String tripHeadsign) {
        this.place = place;
        this.g = g;
        this.h = h;
        this.f = g + h;
        this.cameFrom = cameFrom;
        this.tripHeadsign = tripHeadsign;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return place.equals(node.place);
    }

    @Override
    public int hashCode() {
        return Objects.hash(place.hashCode(), g, h);
    }
}
