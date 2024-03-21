package src.java.API;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import org.json.JSONObject;

import src.java.Singletons.FileManager;

public class RetrievePostalWithAPI {

    public RetrievePostalWithAPI() {

    }
    static FileManager fileManager = FileManager.getInstance();

    public ArrayList<Double> getPCode(String pCode) throws IOException{
        ArrayList<Double> LatLong = null;
        UserObject userObject = retrieveUserObject("userObject.ser");
        if (userObject.getIP().equals(getIP()) == false) {
            System.out.println("New UserObject created");
            userObject = new UserObject(getIP());
        }
        
        if (userAllowedToInteract(userObject)) {
            userObject.addInteraction(getCurrentTime());
            fileManager.serializeObject(userObject, "userObject.ser");
            LatLong = sentPostRequest(pCode);
        }

        else {
            System.out.println("Too many requests, try again later");
        }
        
        return LatLong;
    }

    public ArrayList<Double> sentPostRequest(String pCode) {
        ArrayList<Double> LatLong = new ArrayList<Double>();
        try {
        @SuppressWarnings("deprecation")
        
        String requestBody = "{\"postcode\": \"" + pCode + "\"}";

        HttpRequest request = HttpRequest.newBuilder()
        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
        .uri(URI.create("https://computerscience.dacs.unimaas.nl/get_coordinates?"))
        .header("Content-Type", "application/json")
        .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
        .send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.statusCode());
        System.out.println(response.body());

        JSONObject jsonObject = new JSONObject(response.body());
        
        // Extract latitude and longitude
        String latitude = jsonObject.getString("latitude");
        String longitude = jsonObject.getString("longitude");
        LatLong.add(Double.parseDouble((latitude)));
        LatLong.add(Double.parseDouble((longitude)));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return LatLong;
    }

    public boolean userAllowedToInteract(UserObject userObject)  {
        //5 Seconds 1 request per IP
        //1 Minute 5 requests per IP
        //1 Hour 40 requests per IP
        //1 Day 100 requests per IP
        ArrayList<String> callsList = userObject.getCallsList();

        if (callsList.size() == 0) {
            return true;
        }

        if (determineIfAllowanceExceeded(callsList)) {
            return true;
        }

        return false;
    }


    public boolean determineIfAllowanceExceeded(ArrayList<String> callsList) {//Has to be refactored
        int startingIndex = 0;       
        for (String call: callsList) {
            long difference = getTimeDifference(call, getCurrentTime());

            if (difference <= 86400) { //86400 is the amount of seconds in a day
                int indexDay = callsList.indexOf(call);
                startingIndex = indexDay;
                if (getAmountOfCalls(startingIndex, callsList) >= 100) {
                    return false;
                }
            }

            if (difference <= 3600) {
                int indexHour = callsList.indexOf(call);
                startingIndex =  indexHour;
                if (getAmountOfCalls(startingIndex, callsList) >= 40) {
                    return false;
                }
            }

            if (difference <= 60) {
                int indexMinute = callsList.indexOf(call);
                startingIndex = indexMinute;
                if (getAmountOfCalls(startingIndex, callsList) >= 5) {
                    return false;
                }
            }

            if (difference <= 5) {
                int indexSecond = callsList.indexOf(call);
                startingIndex = indexSecond;
                if (getAmountOfCalls(startingIndex, callsList) >= 1) {
                    return false;
                }
            }

        }

        return true;
    }

    public int getAmountOfCalls(int startingIndex, ArrayList<String> callsList) {
        int amountOfCallsToday = 0;

        for (;startingIndex < callsList.size(); startingIndex++) {
            amountOfCallsToday++;
        }

        return amountOfCallsToday;
    }

    public long getTimeDifference (String time1, String time2) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");

        LocalDateTime formattedTime1 = LocalDateTime.parse(time1, formatter);   
        LocalDateTime formattedTime2 = LocalDateTime.parse(time2, formatter);   
        
        return ChronoUnit.SECONDS.between(formattedTime1, formattedTime2);
    }

    public UserObject retrieveUserObject(String objectFileName) throws IOException {
        UserObject userObject = null;
        File file = new File(objectFileName);
        if (file.exists()) {
            try {
                userObject = (UserObject) fileManager.getObject("userObject.ser");
            }
            
            catch (ClassNotFoundException e){
                e.printStackTrace();
            }
        }
        
        else {
            userObject = new UserObject(getIP());
        }

        return userObject;
    }

    public String getCurrentTime() {
        LocalDateTime timeCurrent = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");
        String formattedDateTime = timeCurrent.format(formatter);
        return formattedDateTime;
    }

    //Helps in enforcing a rate limit on API calls (See Project 1-2 manual)
    public String getIP() throws IOException{
        String urlString = "http://checkip.amazonaws.com/";
        String string = "";
        URL url = new URL(urlString);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
            string = br.readLine();
        }
        
        return string;
    }
}   