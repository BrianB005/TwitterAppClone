package com.brianbett.twitter.retrofit;

import java.util.List;

public interface TweetsInterface {
    void success(List<Tweet> timelineTweets);
    void failure(Throwable throwable);
    void errorExists(String errorMessage);
}
