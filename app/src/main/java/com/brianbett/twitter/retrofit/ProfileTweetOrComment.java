package com.brianbett.twitter.retrofit;

import java.util.List;

public class ProfileTweetOrComment extends Tweet{
    private String tweet;
    private List<String> replyingTo;

    public String getTweet() {
        return tweet;
    }

    public List<String> getReplyingTo() {
        return replyingTo;
    }
}
