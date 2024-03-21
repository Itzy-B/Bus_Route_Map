package src.java.Main;

import java.io.IOException;

public class TimeCalculator {
    
    private static int MinutesInHours = 60;

    public static long calculateAverageTimeTaken(String zipCode1, String zipCode2, TransportMode mode) throws IOException {
        double distance = CalculateDistance.getDistance(zipCode1, zipCode2, false);
        double averageVelocity = mode.getVelocity();
        
        if (averageVelocity == 0) {
            throw new IllegalArgumentException("Average velocity cannot be zero.");
        }

        double averageTime = (distance / averageVelocity) * MinutesInHours;

        return Math.round(averageTime);
    }
    
    /*  Main test code
    public static void main(String[] args) {
         TimeCalculator calculator = new TimeCalculator();
        
         TransportMode mode = new Car();
         // Change this to new Walk() or new Bike() to calculate for different modes
         String zipCode1 = "6211AL";
         String zipCode2 = "6211PK";
         try {
             double averageTimeTaken = calculator.calculateAverageTimeTaken(zipCode1, zipCode2, mode);
            
             System.out.println("Average Time Taken: " + averageTimeTaken + " minutes");
         } catch (IOException e) {
             System.err.println("An error occurred while calculating the distance: " + e.getMessage());
             e.printStackTrace();
         }

     */
    }


