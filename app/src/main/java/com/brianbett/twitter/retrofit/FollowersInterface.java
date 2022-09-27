package com.brianbett.twitter.retrofit;

import java.util.List;

public interface FollowersInterface {

    void success(List<UserDetails> usersList);
    void failure(Throwable throwable);
    void errorExists(String errorMessage);
}
