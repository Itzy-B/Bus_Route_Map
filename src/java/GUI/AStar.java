package src.java.GUI;

import src.java.Main.CalculateDistance;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalTime;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class AStar {
    protected final int AVGWALKINGTIME = 70; // meters/min
    protected final Graph graph;
    protected List<String> directions;

    public AStar(Graph graph) {
        this.graph = graph;
        directions = new ArrayList<>();
    }

    public List<String> getDirections() {
        return this.directions;
    }

    public List<Place> findShortestPath(Place startPlace, Place endPlace) throws Exception {
        connectBusStops();

        connectPlaceToGraph(startPlace, 10);
        connectPlaceToGraph(endPlace, 10);

        return aStarSearch(startPlace, endPlace, LocalTime.of(12, 0, 0));
    }

    private void connectPlaceToGraph(Place place, int stops) throws Exception {
        List<BusStop> nearestBusStops = graph.findNearestBusStops(place.getLatitude(), place.getLongitude(), stops);
        graph.addVertex(place);
        for (BusStop busStop : nearestBusStops) {
            int walkingDist = getRealDistance(place, busStop);
            long walkingTime = Math.ceilDiv(walkingDist, AVGWALKINGTIME);

            Edge walkEdge = new Edge(place, busStop, walkingTime, walkingDist, "walk");
            graph.addEdge(walkEdge);

            walkEdge = new Edge(busStop, place, walkingTime, walkingDist, "walk");
            graph.addEdge(walkEdge);
        }
    }

    private void connectBusStops() {
        for (Place p1 : graph.getVertices()) {
             for (Place p2 : graph.getVertices()) {
                if (p2.equals(p1)) {
                    continue;
                }
                int walkingDist = (int) Math.round(CalculateDistance.distanceBetween(p1.lat, p1.lon, p2.lat, p2.lon));
                if (walkingDist <= 100) {
                    long walkingTime = Math.ceilDiv(walkingDist, AVGWALKINGTIME);

                    Edge walkEdge = new Edge(p1, p2, walkingTime, walkingDist, "walk");
                    graph.addEdge(walkEdge);

                    walkEdge = new Edge(p2, p1, walkingTime, walkingDist, "walk");
                    graph.addEdge(walkEdge);
                }
            }
        }
    }

    private List<Place> aStarSearch(Place start, Place goal, LocalTime startTime) {
        PriorityQueue<SearchNode> openSet = new PriorityQueue<>(Comparator.comparingDouble(searchNode -> searchNode.f));
        Map<Place, Long> priorityMap = new HashMap<>();

        for (Place p : graph.getVertices()) {
            priorityMap.putIfAbsent(p, Long.MAX_VALUE);
        }

        SearchNode startSearchNode = new SearchNode(start, 0, heuristic(start, goal), startTime, 0, null, null);
        priorityMap.put(start, startSearchNode.f);
        openSet.add(startSearchNode);

        while (!openSet.isEmpty()) {
            SearchNode currentSearchNode = openSet.poll();
            if (priorityMap.get(currentSearchNode.place) != currentSearchNode.f) {
                continue;
            }

            if (currentSearchNode.place.equals(goal)) {
                return reconstructPath(currentSearchNode);
            }

            System.out.println("Current Node: " + currentSearchNode.place + ", g = " + currentSearchNode.g + ", h = " + currentSearchNode.h + ", f = " + currentSearchNode.f + ", time = " + currentSearchNode.time);

            for (Edge edge : graph.getEdges(currentSearchNode.place)) {
                if (currentSearchNode.cameFrom != null && edge.getTo().equals(currentSearchNode.cameFrom.place)) {
                    continue;
                }

                long timeCost = 0;
                int distCost = 0;
                String edgeHeadSign = edge.getTripHeadSign();

                if (edgeHeadSign.equals("walk")) {
                    System.out.println("Considering walking to: " + edge.getTo() + ", time cost: " + edge.getWalkingTime() + ", dist cost: " + edge.getWalkingDist());

                    timeCost = currentSearchNode.g + edge.getWalkingTime();
                    distCost = currentSearchNode.dist + edge.getWalkingDist();
                    LocalTime time = currentSearchNode.time.plusMinutes(edge.getWalkingTime());
                    SearchNode searchNode = new SearchNode(edge.getTo(), timeCost, heuristic(edge.getTo(), goal), time, distCost, currentSearchNode, null);
                    if (searchNode.f < priorityMap.get(searchNode.place)) {
                        priorityMap.put(searchNode.place, searchNode.f);
                    }
                    openSet.add(searchNode);


                    continue;
                }

                System.out.println("Considering edge to: " + edge.getTo() + ", tripHeadsign: " + edge.getTripHeadSign());

                List<Trip> trips = edge.getTrips();
                Collections.sort(trips);

                for (Trip trip : trips) {
                    //System.out.println("Considering trip: " + trip);

                    if (trip.getDepartureTime().compareTo(currentSearchNode.time) >= 0) {
                        System.out.println("choose trip: " + trip);
                        timeCost = currentSearchNode.g + GraphBuilder.calculateTimeDifference(currentSearchNode.time, trip.getArriveTime());
                        distCost = currentSearchNode.dist + trip.getShapeDistTraveled();
                        LocalTime time = currentSearchNode.time.plusMinutes(GraphBuilder.calculateTimeDifference(currentSearchNode.time, trip.getArriveTime()));
                        SearchNode searchNode = new SearchNode(edge.getTo(), timeCost, heuristic(edge.getTo(), goal), time, distCost, currentSearchNode, trip);
                        if (searchNode.f < priorityMap.get(searchNode.place)) {
                            priorityMap.put(searchNode.place, searchNode.f);
                        }
                        openSet.add(searchNode);
                        break;
                    }
                }
            }
        }

        return Collections.emptyList();
    }

    private long heuristic(Place place, Place goal) {
        double dist = CalculateDistance.distanceBetween(place.getLatitude(), place.getLongitude(), goal.getLatitude(), goal.getLongitude());
        return Math.round(dist / AVGWALKINGTIME);
    }

    private List<Place> reconstructPath(SearchNode currentSearchNode) {
        List<SearchNode> pathNodes = new ArrayList<>();
        List<Place> stops = new ArrayList<>();
        while (currentSearchNode != null) {
            pathNodes.add(currentSearchNode);
            currentSearchNode = currentSearchNode.cameFrom;
        }
        Collections.reverse(pathNodes);

        for (int i = 0; i < pathNodes.size(); i += 1) {
            if (i == 0) {
                System.out.println("walk from " + pathNodes.get(0).place + " to " + pathNodes.get(1).place + ", time: " + pathNodes.get(1).time);
            } else if (i == pathNodes.size() - 1) {
                System.out.println("walk from " + pathNodes.get(pathNodes.size() - 2).place + " to " + pathNodes.get(pathNodes.size() - 1).place + ", time: " + pathNodes.get(pathNodes.size() - 1).time);
            } else {
                System.out.println("take bus from " + pathNodes.get(i).place + " to " + pathNodes.get(i + 1).place + ", time: " + pathNodes.get(i + 1).time);
                System.out.println(" ---- in trip: " + pathNodes.get(i + 1).trip);
            }
            stops.add(pathNodes.get(i).place);
        }

        return stops;
    }

    /*private void constructDirections(Node currentNode) {
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
    }*/

    public static int getRealDistance(Place origin, Place destination) throws Exception {
        String apiKey = "AIzaSyDnJH0pu5NzqH0b6GjiPyTDfdkBDugYw6w";
        String originStr = origin.lat + "," + origin.lon;
        String destinationStr = destination.lat + "," + destination.lon;
        String urlString = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + originStr +
                "&destinations=" + destinationStr + "&mode=walking&key=" + apiKey;

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();
        conn.disconnect();

        // Parse the JSON response
        JSONObject jsonResponse = new JSONObject(content.toString());
        JSONArray rows = jsonResponse.getJSONArray("rows");
        JSONObject elements = rows.getJSONObject(0);
        JSONArray element = elements.getJSONArray("elements");
        JSONObject distance = element.getJSONObject(0).getJSONObject("distance");

        return distance.getInt("value");
    }

    public static void main(String[] args) throws Exception {
        Graph graph = new Graph();
        GraphBuilder graphBuilder = new GraphBuilder(graph);
        graphBuilder.getBusStops();
        Place startPlace  = new Place(50.8385716, 5.66547324285714);
        Place endPlace = new Place(50.8380708633987, 5.71570995359477);

        AStar aStar = new AStar(graph);
        List<Place> path = aStar.findShortestPath(startPlace, endPlace);

        if (path.isEmpty()) {
            System.out.println("No path found.");
        }

        /*if (path.isEmpty()) {
            System.out.println("No path found.");
        } else {
            System.out.println("Path found:");
            for (Place place : path) {
                System.out.println(place);
            }
        }*/
    }
}
