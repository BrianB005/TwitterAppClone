package com.brianbett.twitter.retrofit;

public interface UserInterface {
    void success(User user);
    void failure(Throwable throwable);
    void errorExists(String errorMessage);
}
