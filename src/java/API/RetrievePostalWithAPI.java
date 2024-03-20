package src.java.API;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import src.java.Singletons.FileManager;

import src.java.API.UserObject;


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
            System.out.println("Allowed to call! Succes");
            fileManager.serializeObject(userObject, "userObject.ser");
            LatLong = sentPostRequest("6218HW");
        }

        else {
            System.out.println("Request failed");
        }
        
        return LatLong;
    }

    public ArrayList<Double> sentPostRequest(String pCode) {
        ArrayList<Double> LatLong = null;
        //TODO: Uncomment this when connected to WiFi from UM
        try {
        @SuppressWarnings("deprecation")
        
        URL obj = new URL("https://www.computerscience.dacs.unimaas.nl/get_coordinates");

        HttpURLConnection httpURLConnection = (HttpURLConnection) obj.openConnection();
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
        String POST_PARAMS = "{\"postcode\": \"6229EN\"}";
        httpURLConnection.setDoOutput(true);
        OutputStream os = httpURLConnection.getOutputStream();
        int responseCode = httpURLConnection.getResponseCode();
        System.out.println(responseCode);
        os.write(POST_PARAMS.getBytes());
        os.flush();
        os.close();
        System.out.println(httpURLConnection.getInputStream());
        LatLong = new ArrayList<Double>();

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