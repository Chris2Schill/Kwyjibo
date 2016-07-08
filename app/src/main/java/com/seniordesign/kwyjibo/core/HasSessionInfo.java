package com.seniordesign.kwyjibo.core;

// I use this as a cheap way to easily share these string constants across the codebase
public interface HasSessionInfo {
    String USER_ID = "Id";
    String USER_NAME = "Username";
    String USER_EMAIL = "Email";
    String AUTH_TOKEN = "AuthToken";
    String IS_AUTHENTICATED = "Authenticated";
    String CURRENT_STATION = "CurrentStation";
}
