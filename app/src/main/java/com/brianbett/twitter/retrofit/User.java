package com.brianbett.twitter.retrofit;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("user")
    private UserDetails userDetails;
    private String token;

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }
}
