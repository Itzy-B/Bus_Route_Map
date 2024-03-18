package src.java.API;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.ArrayList;

public class retrievePostalWithAPI{
    public static ArrayList<Double> getPCode(String pCode) throws IOException{
        ArrayList<Double> LatLong = new ArrayList<Double>();
        try {
        @SuppressWarnings("deprecation")
        URL obj = new URL("https://www.computerscience.dacs.unimaas.nl");

        HttpURLConnection httpURLConnection = (HttpURLConnection) obj.openConnection();
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
        String POST_PARAMS = "/get_coordinates?postcode={6217HG}";
        httpURLConnection.setDoOutput(true);
        OutputStream os = httpURLConnection.getOutputStream();
        os.write(POST_PARAMS.getBytes());
        os.flush();
        os.close();
        int responseCode = httpURLConnection.getResponseCode();
        System.out.println(responseCode);
        System.out.println(httpURLConnection.getInputStream());
        
        //Do something with Lat
        }
        catch (Exception e) {

        }
        return LatLong;
        
    }

    //Enforce a rate limit on API calls (See Project 1-2 manual)
    public static void getIP() throws IOException{
        String urlString = "http://checkip.amazonaws.com/";
        URL url = new URL(urlString);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
            System.out.println(br.readLine());
        }
    }




}   