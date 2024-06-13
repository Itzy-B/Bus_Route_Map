package src.java.GUI;

import java.util.*;

public class Graph {
    private Map<Place, Set<Edge>> adjList = new HashMap<>();

    public void addVertex(Place vertex) {
        adjList.putIfAbsent(vertex, new HashSet<>());
    }

    public void addEdge(Place from, Place to, Trip trip, String tripHeadsign) {
        Edge newEdge = new Edge(from, to, tripHeadsign);

        for (Edge e : adjList.get(from)) {
            if (e.equals(newEdge)) {
                e.addTrip(trip);
                return;
            }
        }

        newEdge.addTrip(trip);
        adjList.get(from).add(newEdge);
    }

    public void addEdge(Edge edge) {
        Place from = edge.getFrom();
        adjList.get(from).add(edge);
    }

    public void removeVertex(Place vertex) {
        adjList.remove(vertex);
    }

    public Set<Edge> getEdges(Place vertex) {
        return adjList.get(vertex);
    }

    public Set<Place> getVertices() {
        return adjList.keySet();
    }

    // Method to find the N nearest bus stops to given coordinates
    public List<BusStop> findNearestBusStops(double lat, double lon, int stops) {
        PriorityQueue<BusStop> nearestStops = new PriorityQueue<>(Comparator.comparingDouble(busStop -> -busStop.distanceTo(lat, lon)));

        for (Place place : adjList.keySet()) {
            if (place instanceof BusStop) {
                nearestStops.offer((BusStop) place);
                if (nearestStops.size() > stops) {
                    nearestStops.poll(); // Remove the farthest bus stop if we have more than N
                }
            }
        }

        // Convert the priority queue to a list and return
        List<BusStop> result = new ArrayList<>();
        while (!nearestStops.isEmpty()) {
            result.add(nearestStops.poll());
        }
        Collections.reverse(result); // Optional: to get closest first
        return result;
    }

}
