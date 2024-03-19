package src.java.Main;
//Main Will Be Instancing of GUI

import static src.java.Main.CalculateDistance.*;
import src.java.API.RetrievePostalWithAPI;

import java.io.IOException;
public class Main {
    public static void main(String[] args) throws IOException {
        // System.out.println(printDistance("ABCD", "6213HD")); 
        RetrievePostalWithAPI.getPCode("43"); 
    }
}