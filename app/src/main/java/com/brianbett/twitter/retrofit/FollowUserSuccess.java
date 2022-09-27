package com.brianbett.twitter.retrofit;

public interface FollowUserSuccess {
    void success(String message);
    void errorExists();
    void failure(Throwable throwable);
}
