package com.brianbett.twitter.retrofit;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Tweet implements Serializable {

    @SerializedName("_id")
    private String tweetId;
    private String title;
    private List<String> comments;
    private List<String> retweets;
    private List<String> likes;
    private List<String >images;
    @SerializedName("user")
    private UserDetails userDetails;
    @SerializedName("createdAt")
    private String timePosted;

    public String getTweetId() {
        return tweetId;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getComments() {
        return comments;
    }

    public List<String> getRetweets() {
        return retweets;
    }

    public List<String> getLikes() {
        return likes;
    }

    public List<String> getImages() {
        return images;
    }

    @SerializedName("user")
    public UserDetails getUserDetails() {
        return userDetails;
    }

    public String getTimePosted() {
        return timePosted;
    }

    public void setRetweets(List<String> retweets) {
        this.retweets = retweets;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }
}
