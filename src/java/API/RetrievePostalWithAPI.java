package src.java.API;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutput;
import java.io.OutputStream;
import java.net.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import src.java.Singletons.FileManager;


public class RetrievePostalWithAPI{
    static FileManager fileManager = FileManager.getInstance();

    public static ArrayList<Double> getPCode(String pCode) throws IOException{
        ArrayList<Double> LatLong = new ArrayList<Double>();
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

        // }

        List<String> stream = fileManager.getFile("src/java/Resources/currentIP.txt");



        UserObject userObject = retrieveUserObject("userObject.ser");
        if (userObject.getIP().equals(getIP())) {
            System.out.println("retart");
        }
        
        return LatLong;
        // ObjectOutput serializedUserObject = fileManager.serializeObject(userObject, "src/java/Resources/");

        // String[] IPInstance = {"value"};
        // String currentIp = "";
        // if (stream.size() != 0) {
        //     IPInstance = stream.get(0).split(",");
        //     currentIp = getIP();
        //     if (currentIp.equals(IPInstance[0])) {
        //         int amountOfCalls = Integer.parseInt(IPInstance[1]);
        //         System.out.println("found in file");
        //         String IpInstance = createIpInstance(currentIp, amountOfCalls + 1);
        //         FileManager.getInstance().writeToFile("src/java/Resources/","currentIP.txt", IpInstance);

        //     }

        //     else {
        //         FileManager.getInstance().writeToFile("src/java/Resources/","currentIP.txt", createIpInstance(IPInstance, 1, true));
        //     }
        // }

        // System.out.println(stream);
        
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

//     5 Seconds 1 request per IP
// 1 Minute 5 requests per IP
// 1 Hour 40 requests per IP
// 1 Day 100 requests per IP




}   