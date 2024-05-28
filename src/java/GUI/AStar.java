package src.java.GUI;

import src.java.Main.CalculateDistance;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.*;

public class AStar {
    protected final double AVGWALKINGTIME = 78.0; // meters/min
    protected final double AVGBUSTIME = 333.333;
    protected final Graph graph;
    protected List<String> directions;

    public AStar(Graph graph) {
        this.graph = graph;
        directions = new ArrayList<>();
    }

    public List<Place> findShortestPath(Place startPlace, Place endPlace) {
        connectPlaceToGraph(startPlace, 10);
        connectPlaceToGraph(endPlace, 5);

        return aStarSearch(startPlace, endPlace);
    }

    private void connectPlaceToGraph(Place place, int stops) {
        List<BusStop> nearestBusStops = graph.findNearestBusStops(place.getLatitude(), place.getLongitude(), stops);
        graph.addVertex(place);
        for (BusStop busStop : nearestBusStops) {
            double distance = CalculateDistance.distanceBetween(place.getLatitude(), place.getLongitude(), busStop.getLatitude(), busStop.getLongitude());
            graph.addEdge(place, busStop, distance, "walk");
            graph.addEdge(busStop, place, distance, "walk");
        }
    }

    private List<Place> aStarSearch(Place start, Place goal) {
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(node -> node.f));
        Map<Place, Map<String, Boolean>> isMarked = new HashMap<>();

        Node startNode = new Node(start, 0, heuristic(start, goal), null, "walk");
        openSet.add(startNode);

        while (!openSet.isEmpty()) {
            Node currentNode = openSet.poll();
            isMarked.putIfAbsent(currentNode.place, new HashMap<>());
            isMarked.get(currentNode.place).put(currentNode.tripHeadsign, true);

            if (currentNode.place.equals(goal)) {
                constructDirections(currentNode);
                return reconstructPath(currentNode);
            }

            // System.out.println("Current Node: " + currentNode.place + "with g = " + currentNode.g + ", h = " + currentNode.h + ", f = " + currentNode.f + ", headsign: " + currentNode.tripHeadsign);

            for (Edge edge : graph.getEdges(currentNode.place)) {
                if (isMarked.containsKey(edge.getTo()) && isMarked.get(edge.getTo()).getOrDefault(edge.getTripHeadsign(), false)) {
                    continue;
                }

                // Handle start node and nodes with headsign
                String edgeHeadSign = edge.getTripHeadsign();
                String currHeadSign = currentNode.tripHeadsign;
                if (!edgeHeadSign.equals("walk") && !currHeadSign.equals("walk") && !edgeHeadSign.equals(currHeadSign)) {
                    continue;
                }

                // System.out.println("Considering edge to: " + edge.getTo() + " with weight: " + edge.getWeight() + " and tripHeadsign: " + edge.getTripHeadsign());
                double cost = currentNode.g + edge.getWeight();
                Node node = new Node(edge.getTo(), cost, heuristic(edge.getTo(), goal), currentNode, edgeHeadSign);
                openSet.add(node);
            }
        }

        return Collections.emptyList();
    }


    private double heuristic(Place place, Place goal) {
        return CalculateDistance.distanceBetween(place.getLatitude(), place.getLongitude(), goal.getLatitude(), goal.getLongitude());
    }

    private List<Place> reconstructPath(Node currentNode) {
        List<Place> path = new ArrayList<>();
        while (currentNode != null) {
            path.add(currentNode.place);
            currentNode = currentNode.cameFrom;
        }
        Collections.reverse(path);
        return path;
    }

    private void constructDirections(Node currentNode) {
        Node p = currentNode;
        List<Node> pathNodes = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        double totalTime = 0.0;
        double totalDistance = 0.0;

        while (p != null) {
            pathNodes.add(p);
            p = p.cameFrom;
        }
        Collections.reverse(pathNodes);

        for (int i = 0; i < pathNodes.size() - 1; i += 1) {
            sb = new StringBuilder();
            Node next = pathNodes.get(i + 1);
            Node curr = next.cameFrom;
            long dist = Math.round(next.g - curr.g);

            if (i == 0) {
                totalDistance += dist;
                totalTime += (dist / AVGWALKINGTIME);
                sb.append("Walk from your current location around ")
                        .append(dist)
                        .append(" meters to ")
                        .append(next.place.toString());
                directions.add(sb.toString());
            } else if (i == pathNodes.size() - 2) {
                totalDistance += dist;
                totalTime += (dist / AVGWALKINGTIME);
                sb.append("Walk around ")
                        .append(dist)
                        .append(" meters to your destination");
                directions.add(sb.toString());
            } else {
                totalDistance += dist;
                totalTime += (dist / AVGBUSTIME);
                sb.append("Take bus from ")
                        .append(curr.place.toString())
                        .append(" to ")
                        .append(next.place.toString())
                        .append(", headsign: ")
                        .append(next.tripHeadsign)
                        .append(", distance: ")
                        .append(dist);
                directions.add(sb.toString());
            }
        }

        sb = new StringBuilder();
        sb.append("Total distance: ")
                .append(Math.round(totalDistance))
                .append(" meters , total time: ")
                .append(Math.round(totalTime))
                .append("minutes");
        directions.add(sb.toString());
    }

    public static void main(String[] args) throws SQLException, FileNotFoundException {
        Graph graph = new Graph();
        GraphBuilder graphBuilder = new GraphBuilder(graph);
        graphBuilder.getBusStops();

        Place startPlace = new Place(50.8466191301886,  5.70587752641509);
        Place endPlace = new Place(50.8531907678571, 5.68879784285714);

        /*AStar aStar = new AStar(graph);
        aStar.connectPlaceToGraph(startPlace);
        aStar.connectPlaceToGraph(endPlace);

        System.out.println("Place: " + startPlace);
        for (Edge edge : graph.getEdges(startPlace)) {
            System.out.println("  Edge to: " + edge.getTo() + " with weight: " + edge.getWeight() + edge);
        }

        System.out.println("Place: " + endPlace);
        for (Edge edge : graph.getEdges(endPlace)) {
            System.out.println("  Edge to: " + edge.getTo() + " with weight: " + edge.getWeight());
        }

        BusStop busStop1 = new BusStop(2578133, "Maastricht, Forum MECC", 50.837099, 5.713363);
        for (Edge edge : graph.getEdges(busStop1)) {
            System.out.println(edge);
        }*/

        AStar aStar = new AStar(graph);
        List<Place> path = aStar.findShortestPath(startPlace, endPlace);

        if (path.isEmpty()) {
            System.out.println("No path found.");
        } else {
            System.out.println("Path found:");
            for (Place place : path) {
                System.out.println(place);
            }

            for (String s : aStar.directions) {
                System.out.println(s);
            }
        }
    }
}
