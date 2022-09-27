package com.brianbett.twitter.retrofit;

import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface MyApi {

    @POST("auth/register")
    Call<User> registerUser(@Body HashMap<String,String> userDetails);

    @POST("auth/login")
    Call<User> loginUser(@Body HashMap<String,String> userDetails);

    @POST("auth/checkDetails")
    Call<ResponseBody> checkDetails(@Body HashMap<String,String> userDetails);

    @GET("tweets")
    Call<List<Tweet>>  timelineTweets(@Header("Authorization") String token);

    @POST("tweets/like/{tweetId}")
    Call<Tweet> likeTweet(@Header("Authorization") String token, @Path(value = "tweetId")String tweetId);
    @POST("tweets/retweet/{tweetId}")
    Call<Tweet> retweetTweet(@Header("Authorization") String token, @Path(value = "tweetId")String tweetId);
    @POST("tweets/create")
    Call<Tweet> createTweet(@Header("Authorization") String token, @Body HashMap<String,String> tweetDetails);

    @GET("tweets/getCurrentUserTweets")
    Call<List<Tweet>>  myProfileTweets(@Header("Authorization") String token);
    @GET("tweets/getCurrentUserTweetsAndComments")
    Call<List<ProfileTweetOrComment>>  myProfileTweetsAndComments(@Header("Authorization") String token);
    @GET("tweets/getCurrentUserMediaTweets")
    Call<List<ProfileTweetOrComment>>  myProfileMedia(@Header("Authorization") String token);
    @GET("tweets/getCurrentUserLikedTweets")
    Call<List<ProfileTweetOrComment>>  myProfileLikedTweets(@Header("Authorization") String token);

    @GET("tweets/getUserTweets/{userId}")
    Call<List<Tweet>> getUserTweets(@Header("Authorization") String token,@Path(value = "userId") String userId);
    @GET("tweets/getUserLikedTweets/{userId}")
    Call<List<ProfileTweetOrComment>> getUserLikedTweets(@Header("Authorization") String token,@Path(value = "userId") String userId);
    @GET("tweets/getUserTweetsAndComments/{userId}")
    Call<List<ProfileTweetOrComment>> getUserTweetsAndComments(@Header("Authorization") String token,@Path(value = "userId") String userId);
    @GET("tweets/getUserMediaTweets/{userId}")
    Call<List<ProfileTweetOrComment>> getUserMediaTweets(@Header("Authorization") String token,@Path(value = "userId") String userId);


//    user actions
    @GET("users/getFollowers/{userId}")
    Call<List<UserDetails>> getUserFollowers(@Header("Authorization")String token,@Path("userId")String userId);
    @GET("users/getFollowing/{userId}")
    Call<List<UserDetails>> getUserFollowing(@Header("Authorization")String token,@Path("userId")String userId);

    @GET("users/getFollowers")
    Call<List<UserDetails>> getMyFollowers(@Header("Authorization") String token);

    @GET("users/getFollowing")
    Call<List<UserDetails>> getMyFollowing(@Header("Authorization") String token);

    @GET("users")
    Call<List<UserDetails>> getAllUsers(@Header("Authorization") String token);

    @POST("users/follow/{userId}")
    Call<ResponseBody> followUser(@Header("Authorization")String token,@Path("userId")String userId);
    @POST("users/unfollow/{userId}")
    Call<ResponseBody> unfollowUser(@Header("Authorization")String token,@Path("userId")String userId);

    @PUT("users/updateUser")
    Call<User> updateUser(@Header("Authorization")String token,@Body HashMap<String,String> updatedDetails);

    @GET("users/currentUser")
    Call<UserDetails> getCurrentUser(@Header("Authorization")String token);


//    comments actions
    @GET("comments/{tweetId}")
    Call<List<ProfileTweetOrComment>> getATweetsComments(@Header("Authorization")String token,@Path("tweetId") String tweetId);

    @POST("comments/like/{commentId}")
    Call<Tweet> likeComment(@Header("Authorization") String token, @Path(value = "commentId")String commentId);
    @POST("comments/retweet/{commentId}")
    Call<Tweet> retweetComment(@Header("Authorization") String token, @Path(value = "commentId")String tweetId);
    @POST("comments/create/{tweetId}")
    Call<ProfileTweetOrComment> createComment(@Header("Authorization") String token, @Path(value = "tweetId")String tweetId, @Body HashMap<String,String> commentDetails);










}
