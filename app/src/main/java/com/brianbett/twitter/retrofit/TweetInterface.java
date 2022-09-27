package com.brianbett.twitter.retrofit;

public interface TweetInterface {
    void success(Tweet tweet);
    void failure(Throwable throwable);
    void errorExists();
}
