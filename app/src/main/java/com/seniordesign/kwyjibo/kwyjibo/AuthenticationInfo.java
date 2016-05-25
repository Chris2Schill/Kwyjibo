package com.seniordesign.kwyjibo.kwyjibo;

public class AuthenticationInfo {

    private static AuthenticationInfo singleton = null;

    private String username;
    private String email;
    private String authToken;
    private boolean authenticated;

    private AuthenticationInfo(){}

    public static AuthenticationInfo getSession(){
        if (singleton == null){
            singleton = new AuthenticationInfo();
        }
        return singleton;
    }

    public void destroySession(){
        username = null;
        email = null;
        authToken = null;
        authenticated = false;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getAuthToken() {
        return authToken;
    }
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }
    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }
}
