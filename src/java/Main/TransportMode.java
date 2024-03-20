package src.java.Main;

// Abstract class for transportation modes
abstract class TransportMode {
    protected double velocity;
    // In kilometers per hour

    public TransportMode(double velocity) {
        this.velocity = velocity;
    }

    // Getter for velocity
    public double getVelocity() {
        return this.velocity;
    }
}

// Concrete class for walking
class Walk extends TransportMode {
    public Walk() {
        super(5);
    }
}

// Concrete class for car
class Car extends TransportMode {
    public Car() {
        super(50);
    }
}

// Concrete class for bike
class Bike extends TransportMode {
    public Bike() {
        super(15);
    }
}
