package com.seniordesign.kwyjibo.beans;

public class SessionInfo {
    public String USER_ID;
    public String USER_NAME;
    public String USER_EMAIL;
    public String AUTH_TOKEN;
    public boolean IS_AUTHENTICATED;
    public String CURRENT_STATION;
    public String ERROR;

    public String toString(){
        return "{\"USER_ID\":\"" + USER_ID + "\","
                + " \"USER_NAME\":\"" + USER_NAME + "\","
                + " \"USER_EMAIL\":\"" + USER_EMAIL + "\","
                + " \"AUTH_TOKEN\":\"" + AUTH_TOKEN + "\","
                + " \"IS_AUTHENTICATED\":\"" + IS_AUTHENTICATED + "\","
                + " \"RADIO_STATION\":\"" + CURRENT_STATION + "\"}"
                + " \"ERROR\":\"" + ERROR + "\"}";
    }
}
