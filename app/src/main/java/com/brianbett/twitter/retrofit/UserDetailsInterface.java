package com.brianbett.twitter.retrofit;

public interface UserDetailsInterface {
    void success();
    void errorExists(String errorMessage);
    void failure(Throwable throwable);
}
