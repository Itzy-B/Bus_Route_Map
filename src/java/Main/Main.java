package src.java.Main;
//Main Will Be Instancing of GUI

import static src.java.Main.CalculateDistance.*;

import java.io.IOException;

import src.java.API.RetrievePostalWithAPI;

import static src.java.API.RetrievePostalWithAPI.*;
public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println(printDistance("6222CN", "6213HD"));  
    }
}