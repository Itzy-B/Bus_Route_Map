package src.java.API;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import src.java.Singletons.FileManager;


public class RetrievePostalWithAPI{
    static FileManager fileManager = FileManager.getInstance();

    public static ArrayList<Double> getPCode(String pCode) throws IOException{



        UserObject userObject = retrieveUserObject("userObject.ser");
        if (userObject.getIP().equals(getIP()) == false) {
            userObject = new UserObject(getIP());
        }
        
        if (userAllowedToInteract(userObject)) {
            userObject.addInteraction(getCurrentTime());
        }
        
        
        
        
        
        fileManager.serializeObject(userObject, "userObject.ser");

        ArrayList<Double> LatLong = sentPostRequest();

        return LatLong;
    }

    public static ArrayList<Double> sentPostRequest() {
                // try {
        // @SuppressWarnings("deprecation")
        // URL obj = new URL("https://www.computerscience.dacs.unimaas.nl");

        // HttpURLConnection httpURLConnection = (HttpURLConnection) obj.openConnection();
        // httpURLConnection.setRequestMethod("POST");
        // httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
        // String POST_PARAMS = "/get_coordinates?postcode={6217HG}";
        // httpURLConnection.setDoOutput(true);
        // OutputStream os = httpURLConnection.getOutputStream();
        // os.write(POST_PARAMS.getBytes());
        // os.flush();
        // os.close();
        // int responseCode = httpURLConnection.getResponseCode();
        // System.out.println(responseCode);
        // System.out.println(httpURLConnection.getInputStream());
        
        //Do something with Lat
        // }
        // catch (Exception e) {
        ArrayList<Double> LatLong = new ArrayList<Double>();
        return LatLong;
    }

    public static boolean userAllowedToInteract(UserObject userObject)  {
        //     5 Seconds 1 request per IP
// 1 Minute 5 requests per IP
// 1 Hour 40 requests per IP
// 1 Day 100 requests per IP
        return false;
    }

    public static UserObject retrieveUserObject(String objectFileName) throws IOException {
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

    public static String getCurrentTime() {
        LocalDateTime timeCurrent = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");
        String formattedDateTime = timeCurrent.format(formatter);
        return formattedDateTime;
    }

    //Enforce a rate limit on API calls (See Project 1-2 manual)
    public static String getIP() throws IOException{
        String urlString = "http://checkip.amazonaws.com/";
        String string = "";
        URL url = new URL(urlString);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
            string = br.readLine();
        }
        
        return string;
    }
}   