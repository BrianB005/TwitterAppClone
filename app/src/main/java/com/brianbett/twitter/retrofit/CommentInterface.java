package com.brianbett.twitter.retrofit;

public interface CommentInterface {
    void success(ProfileTweetOrComment comment);
    void failure(Throwable throwable);
    void errorExists(String errorMessage);
}
