package com.brianbett.twitter.retrofit;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class UserDetails implements Serializable {

    @SerializedName("_id")
    private String userId;
    private String username;
    private String name;
    private String dateOfBirth;
    private List<String> followers;
    private List<String> following;
    private String profilePic;
    @SerializedName("createdAt")
    private String joinedOn;
    @SerializedName("location")
    private String location;
    private String description;
    private String headerPic;


    public String getDescription() {
        return description;
    }

    public String getHeaderPic() {
        return headerPic;
    }

    public String getLocation() {
        return location;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public List<String> getFollowing() {
        return following;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public String getJoinedOn() {
        return joinedOn;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }

    public void setFollowing(List<String> following) {
        this.following = following;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setJoinedOn(String joinedOn) {
        this.joinedOn = joinedOn;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setHeaderPic(String headerPic) {
        this.headerPic = headerPic;
    }
}
