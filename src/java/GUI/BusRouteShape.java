package src.java.GUI;

public class BusRouteShape {
    protected int shapeId;
    protected int shapePtSequence;
    protected double shapePtLat;
    protected double shapePtLon;
    protected double shapeDistTraveled;

    public BusRouteShape(int shapeId, int shapePtSequence, double shapePtLat, double shapePtLon, double shapeDistTraveled) {
        this.shapeId = shapeId;
        this.shapePtSequence = shapePtSequence;
        this.shapePtLat = shapePtLat;
        this.shapePtLon = shapePtLon;
        this.shapeDistTraveled = shapeDistTraveled;
    }
}
