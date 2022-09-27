package com.brianbett.twitter.retrofit;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;


import com.brianbett.twitter.Preferences;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHandler {

    private static final Retrofit retrofit=new Retrofit.Builder()
            .baseUrl("https://my-twitterr-clone.herokuapp.com/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private static final MyApi myApi=retrofit.create(MyApi.class);



    public static void checkUserDetails(HashMap<String,String> userDetails,UserDetailsInterface detailsInterface){
        Call<ResponseBody> detailsCall= myApi.checkDetails(userDetails);

        detailsCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,@NonNull Response<ResponseBody> response) {
                if(!response.isSuccessful()){
                    try {
                        JSONObject jsonObject=new JSONObject(Objects.requireNonNull(Objects.requireNonNull(response).errorBody()).string());
                        String errorMessage=jsonObject.getString("msg");
                        detailsInterface.errorExists(errorMessage);

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    detailsInterface.success();
                }

            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call,@NonNull Throwable t) {

                detailsInterface.failure(t);
            }
        });
    }
    public static void registerUser(HashMap<String,String> userDetails, UserInterface userInterface){
        Call<User> userCall= myApi.registerUser(userDetails);
        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {

                if(!response.isSuccessful()){
                    try {
                        JSONObject jsonObject=new JSONObject(Objects.requireNonNull(Objects.requireNonNull(response).errorBody()).string());
                        String errorMessage=jsonObject.getString("msg");
                        userInterface.errorExists(errorMessage);

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    userInterface.success(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {

                userInterface.failure(t);
            }
        });
    }
    public static void loginUser(HashMap<String,String> userDetails, UserInterface userInterface){
        Call<User> userCall= myApi.loginUser(userDetails);
        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {

                if(!response.isSuccessful()){
                    try {
                        assert response.errorBody()!=null;
                        JSONObject jsonObject=new JSONObject(response.errorBody().string());
                        String errorMessage=jsonObject.getString("msg");
                        userInterface.errorExists(errorMessage);

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    userInterface.success(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                userInterface.failure(t);
            }
        });


    }
    public static void getTimelineTweets(Context context, TweetsInterface tweetsInterface){
        String token= Preferences.getItemFromSP(context,"token");
        Call<List<Tweet>> tweetsCall= myApi.timelineTweets("Bearer "+token);

        tweetsCall.enqueue(new Callback<List<Tweet>>() {
            @Override
            public void onResponse(@NonNull Call<List<Tweet>> call,@NonNull Response<List<Tweet>> response) {
                if(!response.isSuccessful()){
                    try {
                        assert response.errorBody()!=null;
                        JSONObject errorObject = new JSONObject(response.errorBody().string());
                        String errorMessage= errorObject.getString("msg");
                        tweetsInterface.errorExists(errorMessage);
                    }catch (IOException| JSONException e){
                        e.printStackTrace();
                    }
                }else{
                    tweetsInterface.success(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Tweet>> call,@NonNull Throwable t) {

                tweetsInterface.failure(t);
            }
        });

    }

    public static void likeTweet(Context context,String tweetId,TweetInterface tweetInterface) {
        String token=Preferences.getItemFromSP(context,"token");
        Call<Tweet> tweetCall= myApi.likeTweet("Bearer "+token,tweetId);
        tweetCall.enqueue(new Callback<Tweet>() {
            @Override
            public void onResponse(@NonNull Call<Tweet> call,@NonNull Response<Tweet> response) {
                if(!response.isSuccessful()){
                    Log.d("Error","Something went wrong!");
                }else {
                    tweetInterface.success(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Tweet> call,@NonNull Throwable t) {

                tweetInterface.failure(t);
            }
        });
    }
    public static void retweetTweet(Context context,String tweetId,TweetInterface tweetInterface) {
        String token=Preferences.getItemFromSP(context,"token");
        Call<Tweet> tweetCall= myApi.retweetTweet("Bearer "+token,tweetId);
        tweetCall.enqueue(new Callback<Tweet>() {
            @Override
            public void onResponse(@NonNull Call<Tweet> call,@NonNull Response<Tweet> response) {
                if(!response.isSuccessful()){
                    Log.d("Error","Something went wrong!");
                }else {
                    tweetInterface.success(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Tweet> call,@NonNull Throwable t) {

                tweetInterface.failure(t);
            }
        });
    }

    public static void createTweet(Context context,HashMap<String,String> tweetDetails,TweetInterface tweetInterface){
        String token=Preferences.getItemFromSP(context,"token");
        Call<Tweet> tweetCall= myApi.createTweet("Bearer "+token,tweetDetails);

        tweetCall.enqueue(new Callback<Tweet>() {
            @Override
            public void onResponse(@NonNull Call<Tweet> call,@NonNull Response<Tweet> response) {
                tweetInterface.success(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Tweet> call,@NonNull Throwable t) {

                tweetInterface.failure(t);
            }
        });
    }

    public static void getMyProfileTweets(Context context,TweetsInterface tweetsInterface){
        String token=Preferences.getItemFromSP(context,"token");

        Call<List<Tweet>> tweetsCall= myApi.myProfileTweets("Bearer "+token);

        tweetsCall.enqueue(new Callback<List<Tweet>>() {
            @Override
            public void onResponse(@NonNull Call<List<Tweet>> call,@NonNull Response<List<Tweet>> response) {

                if(!response.isSuccessful()){
                    try {
                        assert response.errorBody()!=null;
                        JSONObject errorObject=new JSONObject(response.errorBody().string());
                        String errorMessage= errorObject.getString("msg");
                        tweetsInterface.errorExists(errorMessage);
                    }catch (JSONException|IOException e){
                        e.printStackTrace();
                    }
                }else {
                    tweetsInterface.success(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Tweet>> call,@NonNull Throwable t) {

                tweetsInterface.failure(t);
            }
        });
    }
    public static void getMyProfileTweetsAndComments(Context context,TweetsAndCommentsInterface tweetsInterface){
        String token=Preferences.getItemFromSP(context,"token");

        Call<List<ProfileTweetOrComment>> tweetsCall= myApi.myProfileTweetsAndComments("Bearer "+token);

        tweetsCall.enqueue(new Callback<List<ProfileTweetOrComment>>() {
            @Override
            public void onResponse(@NonNull Call<List<ProfileTweetOrComment>> call,@NonNull Response<List<ProfileTweetOrComment>> response) {

                if(!response.isSuccessful()){
                    try {
                        assert response.errorBody()!=null;
                        JSONObject errorObject=new JSONObject(response.errorBody().string());
                        String errorMessage= errorObject.getString("msg");
                        tweetsInterface.errorExists(errorMessage);
                    }catch (JSONException|IOException e){
                        e.printStackTrace();
                    }
                }else {
                    tweetsInterface.success(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ProfileTweetOrComment>> call,@NonNull Throwable t) {

                tweetsInterface.failure(t);
            }
        });
    }
    public static void getMyProfileMediaTweets(Context context,TweetsAndCommentsInterface tweetsInterface){
        String token=Preferences.getItemFromSP(context,"token");

        Call<List<ProfileTweetOrComment>> tweetsCall= myApi.myProfileMedia("Bearer "+token);

        tweetsCall.enqueue(new Callback<List<ProfileTweetOrComment>>() {
            @Override
            public void onResponse(@NonNull Call<List<ProfileTweetOrComment>> call,@NonNull Response<List<ProfileTweetOrComment>> response) {

                if(!response.isSuccessful()){
                    try {
                        assert response.errorBody()!=null;
                        JSONObject errorObject=new JSONObject(response.errorBody().string());
                        String errorMessage= errorObject.getString("msg");
                        tweetsInterface.errorExists(errorMessage);
                    }catch (JSONException|IOException e){
                        e.printStackTrace();
                    }
                }else {
                    tweetsInterface.success(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ProfileTweetOrComment>> call,@NonNull Throwable t) {

                tweetsInterface.failure(t);
            }
        });
    }
    public static void getMyProfileLikedTweets(Context context,TweetsAndCommentsInterface tweetsInterface){
        String token=Preferences.getItemFromSP(context,"token");

        Call<List<ProfileTweetOrComment>> tweetsCall= myApi.myProfileLikedTweets("Bearer "+token);

        tweetsCall.enqueue(new Callback<List<ProfileTweetOrComment>>() {
            @Override
            public void onResponse(@NonNull Call<List<ProfileTweetOrComment>> call,@NonNull Response<List<ProfileTweetOrComment>> response) {

                if(!response.isSuccessful()){
                    try {
                        assert response.errorBody()!=null;
                        JSONObject errorObject=new JSONObject(response.errorBody().string());
                        String errorMessage= errorObject.getString("msg");
                        tweetsInterface.errorExists(errorMessage);
                    }catch (JSONException|IOException e){
                        e.printStackTrace();
                    }
                }else {
                    tweetsInterface.success(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ProfileTweetOrComment>> call,@NonNull Throwable t) {

                tweetsInterface.failure(t);
            }
        });
    }

    public static void getUserProfileTweets(Context context,String userId,TweetsInterface tweetsInterface){

        String token=Preferences.getItemFromSP(context,"token");

        Call<List<Tweet>> tweetsCall= myApi.getUserTweets("Bearer "+token,userId);
        tweetsCall.enqueue(new Callback<List<Tweet>>() {
            @Override
            public void onResponse(@NonNull Call<List<Tweet>> call,@NonNull Response<List<Tweet>> response) {

                if(!response.isSuccessful()){
                    try {
                        assert response.errorBody()!=null;
                        JSONObject errorObject=new JSONObject(response.errorBody().string());
                        String errorMessage= errorObject.getString("msg");
                        tweetsInterface.errorExists(errorMessage);
                    }catch (JSONException|IOException e){
                        e.printStackTrace();
                    }
                }else {
                    tweetsInterface.success(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Tweet>> call,@NonNull Throwable t) {

                tweetsInterface.failure(t);
            }
        });
    }
    public static void getUserProfileLikedTweets(Context context,String userId,TweetsAndCommentsInterface tweetsInterface){

        String token=Preferences.getItemFromSP(context,"token");

        Call<List<ProfileTweetOrComment>> tweetsCall= myApi.getUserLikedTweets("Bearer "+token,userId);

        tweetsCall.enqueue(new Callback<List<ProfileTweetOrComment>>() {
            @Override
            public void onResponse(@NonNull Call<List<ProfileTweetOrComment>> call,@NonNull Response<List<ProfileTweetOrComment>> response) {

                if(!response.isSuccessful()){
                    try {
                        assert response.errorBody()!=null;
                        JSONObject errorObject=new JSONObject(response.errorBody().string());
                        String errorMessage= errorObject.getString("msg");
                        tweetsInterface.errorExists(errorMessage);
                    }catch (JSONException|IOException e){
                        e.printStackTrace();
                    }
                }else {
                    tweetsInterface.success(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ProfileTweetOrComment>> call,@NonNull Throwable t) {

                tweetsInterface.failure(t);
            }
        });
    }
    public static void getUserProfileTweetsAndComments(Context context,String userId,TweetsAndCommentsInterface tweetsInterface){
        String token=Preferences.getItemFromSP(context,"token");
        Call<List<ProfileTweetOrComment>> tweetsCall= myApi.getUserTweetsAndComments("Bearer "+token,userId);

        tweetsCall.enqueue(new Callback<List<ProfileTweetOrComment>>() {
            @Override
            public void onResponse(@NonNull Call<List<ProfileTweetOrComment>> call,@NonNull Response<List<ProfileTweetOrComment>> response) {

                if(!response.isSuccessful()){
                    try {
                        assert response.errorBody()!=null;
                        JSONObject errorObject=new JSONObject(response.errorBody().string());
                        String errorMessage= errorObject.getString("msg");
                        tweetsInterface.errorExists(errorMessage);
                    }catch (JSONException|IOException e){
                        e.printStackTrace();
                    }
                }else {
                    tweetsInterface.success(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ProfileTweetOrComment>> call,@NonNull Throwable t) {

                tweetsInterface.failure(t);
            }
        });
    }
    public static void getUserProfileMediaTweets(Context context,String userId,TweetsAndCommentsInterface tweetsInterface){
        String token=Preferences.getItemFromSP(context,"token");
        Call<List<ProfileTweetOrComment>> tweetsCall= myApi.getUserMediaTweets("Bearer "+token,userId);

        tweetsCall.enqueue(new Callback<List<ProfileTweetOrComment>>() {
            @Override
            public void onResponse(@NonNull Call<List<ProfileTweetOrComment>> call,@NonNull Response<List<ProfileTweetOrComment>> response) {

                if(!response.isSuccessful()){
                    try {
                        assert response.errorBody()!=null;
                        JSONObject errorObject=new JSONObject(response.errorBody().string());
                        String errorMessage= errorObject.getString("msg");
                        tweetsInterface.errorExists(errorMessage);
                    }catch (JSONException|IOException e){
                        e.printStackTrace();
                    }
                }else {
                    tweetsInterface.success(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ProfileTweetOrComment>> call,@NonNull Throwable t) {

                tweetsInterface.failure(t);
            }
        });
    }

    public static void getMyFollowers(Context context, FollowersInterface userInterface){
        String token=Preferences.getItemFromSP(context,"token");

        Call<List<UserDetails>> followersCall= myApi.getMyFollowers("Bearer "+token);

        followersCall.enqueue(new Callback<List<UserDetails>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserDetails>> call,@NonNull Response<List<UserDetails>> response) {
                if(!response.isSuccessful()){
                    try {
                        assert response.errorBody()!=null;
                        JSONObject errorObject=new JSONObject(response.errorBody().string());
                        String errorMessage= errorObject.getString("msg");
                        userInterface.errorExists(errorMessage);
                    }catch (JSONException|IOException e){
                        e.printStackTrace();
                    }
                }else{
                    userInterface.success(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserDetails>> call,@NonNull Throwable t) {

                userInterface.failure(t);
            }
        });
    }
    public static void getMyFollowing(Context context, FollowersInterface userInterface){
        String token=Preferences.getItemFromSP(context,"token");
        Call<List<UserDetails>> followingCall= myApi.getMyFollowing("Bearer "+token);

        followingCall.enqueue(new Callback<List<UserDetails>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserDetails>> call,@NonNull Response<List<UserDetails>> response) {
                if(!response.isSuccessful()){
                    try {
                        assert response.errorBody()!=null;
                        JSONObject errorObject=new JSONObject(response.errorBody().string());
                        String errorMessage= errorObject.getString("msg");
                        userInterface.errorExists(errorMessage);
                    }catch (JSONException|IOException e){
                        e.printStackTrace();
                    }
                }else{
                    userInterface.success(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserDetails>> call,@NonNull Throwable t) {

                userInterface.failure(t);
            }
        });
    }
    public static void getUserFollowing(Context context,String userId, FollowersInterface userInterface){
        String token=Preferences.getItemFromSP(context,"token");
        Call<List<UserDetails>> followingCall= myApi.getUserFollowing("Bearer "+token,userId);

        followingCall.enqueue(new Callback<List<UserDetails>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserDetails>> call,@NonNull Response<List<UserDetails>> response) {
                if(!response.isSuccessful()){
                    try {
                        assert response.errorBody()!=null;
                        JSONObject errorObject=new JSONObject(response.errorBody().string());
                        String errorMessage= errorObject.getString("msg");
                        userInterface.errorExists(errorMessage);
                    }catch (JSONException|IOException e){
                        e.printStackTrace();
                    }
                }else{
                    userInterface.success(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserDetails>> call,@NonNull Throwable t) {
                userInterface.failure(t);
            }
        });
    }
    public static void getUserFollowers(Context context,String userId, FollowersInterface userInterface){
        String token=Preferences.getItemFromSP(context,"token");
        Call<List<UserDetails>> followersCall= myApi.getUserFollowers("Bearer "+token,userId);

        followersCall.enqueue(new Callback<List<UserDetails>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserDetails>> call,@NonNull Response<List<UserDetails>> response) {
                if(!response.isSuccessful()){
                    try {
                        assert response.errorBody()!=null;
                        JSONObject errorObject=new JSONObject(response.errorBody().string());
                        String errorMessage= errorObject.getString("msg");
                        userInterface.errorExists(errorMessage);
                    }catch (JSONException|IOException e){
                        e.printStackTrace();
                    }
                }else{
                    userInterface.success(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserDetails>> call,@NonNull Throwable t) {
                userInterface.failure(t);
            }
        });
    }
    public static void getAllUsers(Context context, FollowersInterface userInterface){
        String token=Preferences.getItemFromSP(context,"token");
        Call<List<UserDetails>> allUsersCall= myApi.getAllUsers("Bearer "+token);

        allUsersCall.enqueue(new Callback<List<UserDetails>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserDetails>> call,@NonNull Response<List<UserDetails>> response) {
                if(!response.isSuccessful()){
                    try {
                        assert response.errorBody()!=null;
                        JSONObject errorObject=new JSONObject(response.errorBody().string());
                        String errorMessage= errorObject.getString("msg");
                        userInterface.errorExists(errorMessage);
                    }catch (JSONException|IOException e){
                        e.printStackTrace();
                    }
                }else{
                    userInterface.success(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<UserDetails>> call,@NonNull Throwable t) {
                userInterface.failure(t);
            }
        });
    }

    public static void followUser(Context context,String userId,FollowUserSuccess followUserSuccess){
        String token=Preferences.getItemFromSP(context,"token");

        Call<ResponseBody> userCall= myApi.followUser("Bearer "+token,userId);

        userCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,@NonNull Response<ResponseBody> response) {
                if(!response.isSuccessful()){
                    followUserSuccess.errorExists();
                }else{
                    assert response.body()!=null;
                    followUserSuccess.success(response.body().toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call,@NonNull Throwable t) {

                followUserSuccess.failure(t);
            }
        });
    }
    public static void unFollowUser(Context context,String userId,FollowUserSuccess followUserSuccess){
        String token=Preferences.getItemFromSP(context,"token");

        Call<ResponseBody> userCall= myApi.unfollowUser("Bearer "+token,userId);

        userCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call,@NonNull Response<ResponseBody> response) {
                if(!response.isSuccessful()){
                    followUserSuccess.errorExists();
                }else{
                    assert response.body()!=null;
                    followUserSuccess.success(response.body().toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call,@NonNull Throwable t) {

                followUserSuccess.failure(t);
            }
        });
    }

    public static void updateUser(Context context,HashMap<String,String> updatedDetails,UserInterface userInterface){
        String token=Preferences.getItemFromSP(context,"token");
        Call<User> userCall= myApi.updateUser("Bearer "+token,updatedDetails);

        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call,@NonNull Response<User> response) {
                if(!response.isSuccessful()){
                    try {
                        assert response.errorBody()!=null;
                        JSONObject errorObject=new JSONObject(response.errorBody().string());
                        String errorMessage= errorObject.getString("msg");
                        userInterface.errorExists(errorMessage);
                    }catch (JSONException|IOException e){
                        e.printStackTrace();
                    }
                }else {
                    userInterface.success(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call,@NonNull Throwable t) {
                userInterface.failure(t);
            }
        });

    }

//    comments
public static void likeComment(Context context,String commentId,TweetInterface tweetInterface) {
    String token=Preferences.getItemFromSP(context,"token");
    Call<Tweet> tweetCall= myApi.likeComment("Bearer "+token,commentId);
    tweetCall.enqueue(new Callback<Tweet>() {
        @Override
        public void onResponse(@NonNull Call<Tweet> call,@NonNull Response<Tweet> response) {
            if(!response.isSuccessful()){
                Log.d("Error","Something went wrong!");
            }else {
                tweetInterface.success(response.body());
            }
        }

        @Override
        public void onFailure(@NonNull Call<Tweet> call,@NonNull Throwable t) {

            tweetInterface.failure(t);
        }
    });
}
    public static void retweetComment(Context context,String commentId,TweetInterface tweetInterface) {
        String token=Preferences.getItemFromSP(context,"token");
        Call<Tweet> tweetCall= myApi.retweetComment("Bearer "+token,commentId);
        tweetCall.enqueue(new Callback<Tweet>() {
            @Override
            public void onResponse(@NonNull Call<Tweet> call,@NonNull Response<Tweet> response) {
                if(!response.isSuccessful()){
                    Log.d("Error","Something went wrong!");
                }else {
                    tweetInterface.success(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Tweet> call,@NonNull Throwable t) {

                tweetInterface.failure(t);
            }
        });
    }

    public static void createComment(Context context,String tweetId,HashMap<String,String> commentDetails,CommentInterface commentInterface){
        String token=Preferences.getItemFromSP(context,"token");
        Call<ProfileTweetOrComment> tweetCall= myApi.createComment("Bearer "+token,tweetId,commentDetails);
        tweetCall.enqueue(new Callback<ProfileTweetOrComment>() {
            @Override
            public void onResponse(@NonNull Call<ProfileTweetOrComment> call,@NonNull Response<ProfileTweetOrComment> response) {
                if(!response.isSuccessful()){
                    try {
                        assert response.errorBody()!=null;
                        JSONObject errorObject=new JSONObject(response.errorBody().string());
                        String errorMessage= errorObject.getString("msg");
                        commentInterface.errorExists(errorMessage);
                    }catch (JSONException|IOException e){
                        e.printStackTrace();
                    }
                }else{
                    commentInterface.success(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileTweetOrComment> call,@NonNull Throwable t) {
                commentInterface.failure(t);
            }
        });


    }

    public static void getATweetsComments(Context context,String tweetId,TweetsAndCommentsInterface tweetsInterface){
        String token=Preferences.getItemFromSP(context,"token");
        Call<List<ProfileTweetOrComment>> tweetsCall= myApi.getATweetsComments("Bearer "+token,tweetId);

        tweetsCall.enqueue(new Callback<List<ProfileTweetOrComment>>() {
            @Override
            public void onResponse(@NonNull Call<List<ProfileTweetOrComment>> call,@NonNull Response<List<ProfileTweetOrComment>> response) {

                if(!response.isSuccessful()){
                    try {
                        assert response.errorBody()!=null;
                        JSONObject errorObject=new JSONObject(response.errorBody().string());
                        String errorMessage= errorObject.getString("msg");
                        tweetsInterface.errorExists(errorMessage);
                    }catch (JSONException|IOException e){
                        e.printStackTrace();
                    }
                }else {
                    tweetsInterface.success(response.body());

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ProfileTweetOrComment>> call,@NonNull Throwable t) {

                tweetsInterface.failure(t);
            }
        });
    }

    public static void getCurrentUser(Context context,UserDetailsInterface userDetailsInterface){
        String token=Preferences.getItemFromSP(context,"token");
        Call<UserDetails> userCall= myApi.getCurrentUser("Bearer "+token);
        userCall.enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(@NonNull Call<UserDetails> call,@NonNull Response<UserDetails> response) {
                if(!response.isSuccessful()){
                    try {
                        assert response.errorBody()!=null;
                        JSONObject errorObject=new JSONObject(response.errorBody().string());
                        String errorMessage= errorObject.getString("msg");
                        userDetailsInterface.errorExists(errorMessage);
                    }catch (JSONException|IOException e){
                        e.printStackTrace();
                    }
                }else {
                    userDetailsInterface.success();
                    Gson gson = new Gson();
                    String userString = gson.toJson(response.body());
                    Preferences.saveItemToSP(context,"currentUser",userString);
                }

            }

            @Override
            public void onFailure(@NonNull Call<UserDetails> call,@NonNull Throwable t) {
                userDetailsInterface.failure(t);
            }
        });
    }


}
