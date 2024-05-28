package src.java.GUI;

import java.util.*;

public class Graph {
    private Map<Place, Set<Edge>> adjList = new HashMap<>();

    public void addVertex(Place vertex) {
        adjList.putIfAbsent(vertex, new HashSet<>());
    }

    public void addEdge(Place from, Place to, double weight, String tripHeadsign) {
        adjList.get(from).add(new Edge(from, to, weight, tripHeadsign));
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

    // Method to find the 5 nearest bus stops to given coordinates
    public List<BusStop> findNearestBusStops(double lat, double lon, int stops) {
        PriorityQueue<BusStop> nearestStops = new PriorityQueue<>(Comparator.comparingDouble(busStop -> -busStop.distanceTo(lat, lon)));

        for (Place place : adjList.keySet()) {
            if (place instanceof BusStop) {
                nearestStops.offer((BusStop) place);
                if (nearestStops.size() > stops) {
                    nearestStops.poll(); // Remove the farthest bus stop if we have more than 5
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
