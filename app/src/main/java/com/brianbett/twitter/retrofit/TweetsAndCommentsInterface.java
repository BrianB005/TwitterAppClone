package com.brianbett.twitter.retrofit;

import java.util.List;

public interface TweetsAndCommentsInterface {
    void success(List<ProfileTweetOrComment> timelineTweets);
    void failure(Throwable throwable);
    void errorExists(String errorMessage);
}
