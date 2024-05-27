package src.java.GUI;

import src.java.Main.CalculateDistance;

import java.sql.SQLException;
import java.util.*;

public class AStar {
    private final Graph graph;

    public AStar(Graph graph) {
        this.graph = graph;
    }

    public List<Place> findShortestPath(Place startPlace, Place endPlace) {
        connectPlaceToGraph(startPlace);
        connectPlaceToGraph(endPlace);

        return aStarSearch(startPlace, endPlace);
    }

    private void connectPlaceToGraph(Place place) {
        List<BusStop> nearestBusStops = graph.findNearestBusStops(place.getLatitude(), place.getLongitude());
        graph.addVertex(place);
        for (BusStop busStop : nearestBusStops) {
            double distance = CalculateDistance.distanceBetween(place.getLatitude(), place.getLongitude(), busStop.getLatitude(), busStop.getLongitude());
            graph.addEdge(place, busStop, distance, "walk");
            graph.addEdge(busStop, place, distance, "walk");
        }
    }

    private List<Place> aStarSearch(Place start, Place goal) {
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(node -> node.f));
        //Map<Place, Node> allNodes = new HashMap<>();

        Node startNode = new Node(start, 0, heuristic(start, goal), null, "walk");
        openSet.add(startNode);
        //allNodes.put(start, startNode);

        while (!openSet.isEmpty()) {
            Node currentNode = openSet.poll();

            if (currentNode.place.equals(goal)) {
                return reconstructPath(currentNode);
            }

            //System.out.println("Current Node: " + currentNode.place);

            for (Edge edge : graph.getEdges(currentNode.place)) {
                if (currentNode.cameFrom != null && edge.getTo().equals(currentNode.cameFrom.place)) {
                    continue; // Skip already visited nodes
                }

                // Handle start node and nodes with headsign
                String edgeHeadSign = edge.getTripHeadsign();
                String currHeadSign = currentNode.tripHeadsign;
                if (!edgeHeadSign.equals("walk") && !currHeadSign.equals("walk") && !edgeHeadSign.equals(currHeadSign)) {
                    continue;
                }

                //System.out.println("Considering edge to: " + edge.getTo() + " with weight: " + edge.getWeight() + " and tripHeadsign: " + edge.getTripHeadsign());
                double cost = currentNode.g + edge.getWeight();
                Node node = new Node(edge.getTo(), cost, heuristic(edge.getTo(), goal), currentNode, currHeadSign);
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

    public static void main(String[] args) throws SQLException {
        Graph graph = new Graph();
        GraphBuilder graphBuilder = new GraphBuilder(graph);
        graphBuilder.getBusStops();

        Place startPlace = new Place(50.85253504,  5.68969885999999);
        Place endPlace = new Place(50.8380708633987, 5.71570995359477);

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
        }
    }
}
