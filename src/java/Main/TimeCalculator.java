package src.java.Main;

public class TimeCalculator {
    
    private static int MinutesInHours = 60;


    public double calculateAverageTimeTaken(double distance, TransportMode mode) {
        double averageVelocity = mode.getVelocity();
        
        // Ensure that the average velocity is not zero to avoid division by zero, useful if we implement user input velocity
        // Also helps if you have any mistakes in the abstract class

        if (averageVelocity == 0) {
            throw new IllegalArgumentException("Average velocity cannot be zero.");
        }

        return (distance / averageVelocity) * MinutesInHours;

    }
    
    public static void main(String[] args) {
        TimeCalculator calculator = new TimeCalculator();
        

        double distance = 100;
        // In kilometers
        // Change this for a call to a getter method

        TransportMode mode = new Car();
        // Change this to new Walk() or new Bike() to calculate for different modes
        
        
        double averageTimeTaken = calculator.calculateAverageTimeTaken(distance, mode);
        

        System.out.println("Average Time Taken: " + averageTimeTaken + " minutes");
    }
}

