package src.java.API;

import java.io.Serializable;
import java.util.ArrayList;

public class UserObject implements Serializable {
    private String ipAddress;
    private ArrayList<String> callsList;

    UserObject(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void addInteraction(String callTime) {
        this.callsList.add(callTime);
    }

    public String getIP() {
        return this.ipAddress;
    }


}
