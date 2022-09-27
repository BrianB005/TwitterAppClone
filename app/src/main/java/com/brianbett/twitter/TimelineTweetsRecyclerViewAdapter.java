package com.brianbett.twitter;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.brianbett.twitter.databinding.SingleTweetBinding;
import com.brianbett.twitter.retrofit.RetrofitHandler;
import com.brianbett.twitter.retrofit.Tweet;
import com.brianbett.twitter.retrofit.TweetInterface;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.ArrayList;

import retrofit2.http.GET;

public class TimelineTweetsRecyclerViewAdapter extends RecyclerView.Adapter<TimelineTweetsRecyclerViewAdapter.MyViewHolder> {
    private final ArrayList<Tweet> tweets;
    private final Context context;
    LayoutInflater inflater;

    public TimelineTweetsRecyclerViewAdapter(Context context,ArrayList<Tweet> tweets) {
        this.tweets = tweets;
        this.context=context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        inflater=LayoutInflater.from(parent.getContext());
        SingleTweetBinding singleTweetBinding=SingleTweetBinding.inflate(inflater,parent,false);



        return new MyViewHolder(singleTweetBinding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if(tweets.get(position).getImages().isEmpty()){
            holder.cardView.setVisibility(View.GONE);
        }else{
            holder.cardView.setVisibility(View.VISIBLE);

            StorageReference storageReference= FirebaseStorage.getInstance().getReference("images/"+tweets.get(position).getImages().get(0));

            Task<Uri> uriTask=storageReference.getDownloadUrl();

            uriTask.addOnSuccessListener(uri1 -> {
                Glide.with(context).load(uri1).into(holder.tweetImage);
                holder.tweetImage.setOnClickListener(view -> {
                    @SuppressLint("InflateParams") View popUpView=inflater.inflate(R.layout.view_image_popup,null);
                    PopupWindow popupWindow=new PopupWindow(popUpView,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,true);
                    popUpView.startAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_in));
                    popupWindow.showAtLocation(view, Gravity.BOTTOM,0,0);
                    ImageView tweetImage = popUpView.findViewById(R.id.tweet_image);
                    TextView retweets, comments, likes;
                    retweets = popUpView.findViewById(R.id.retweet_tweet);
                    comments = popUpView.findViewById(R.id.open_comments);
                    likes = popUpView.findViewById(R.id.like_tweet);

                    MaterialToolbar toolbar=popUpView.findViewById(R.id.tool_bar);

                    toolbar.setOnMenuItemClickListener(item -> {
                        if(item.getItemId()==R.id.save_image){

                            ContentValues values=new ContentValues();
                            values.put(MediaStore.Images.Media.TITLE,"New photo");
                            values.put(MediaStore.Images.Media.DISPLAY_NAME,"Twitter");

                            values.put(MediaStore.Images.Media.DESCRIPTION,"A great picture");

                            context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
                            Toast.makeText(context,"Image saved",Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    });

                    toolbar.getChildAt(0).setOnClickListener(view1 -> {
                        popUpView.startAnimation(AnimationUtils.loadAnimation(context,R.anim.fade_out));
                        popupWindow.dismiss();
                    });



                    Tweet tweet=tweets.get(holder.getAdapterPosition());
                    Glide.with(context).load(uri1).into(tweetImage);


                    String currentUserId = Preferences.getItemFromSP(context, "userId");
                    retweets.setText(String.valueOf(tweet.getRetweets().size()));
                    likes.setText(String.valueOf(tweet.getLikes().size()));
                    comments.setText(String.valueOf(tweet.getComments().size()));

                    if (tweet.getLikes().contains(currentUserId)) {
                        likes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart, 0, 0, 0);
                    } else {
                        likes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_white, 0, 0, 0);
                    }

//        liking/unliking tweet

                    likes.setOnClickListener(view3-> {
                        if (tweet.getLikes().contains(currentUserId)) {
                            likes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_white, 0, 0, 0);
                            likes.setText(String.valueOf(tweet.getLikes().size() - 1));

                        } else {
                            likes.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart, 0, 0, 0);
                            likes.setText(String.valueOf(tweet.getLikes().size() + 1));
                        }
                        RetrofitHandler.likeTweet(context, tweet.getTweetId(), new TweetInterface() {
                            @Override
                            public void success(Tweet tweet) {
                                Log.d("Success", "Liked ");
                                tweet.setLikes(tweet.getLikes());
                            }

                            @Override
                            public void failure(Throwable throwable) {

                                Log.d("Exception", throwable.getMessage());
                            }

                            @Override
                            public void errorExists() {

                                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                            }
                        });

                    });


//        retweeting/undoing retweet
                    if (tweet.getRetweets().contains(currentUserId)) {
                        retweets.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweted, 0, 0, 0);
                    } else {
                        retweets.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweet_white, 0, 0, 0);
                    }


                    retweets.setOnClickListener(view1 -> {
                        if (tweet.getRetweets().contains(currentUserId)) {
                            retweets.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweet_white, 0, 0, 0);
                            retweets.setText(String.valueOf(tweet.getRetweets().size() - 1));

                        } else {
                            retweets.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweted, 0, 0, 0);
                            retweets.setText(String.valueOf(tweet.getRetweets().size() + 1));
                        }
                        RetrofitHandler.retweetTweet(context, tweet.getTweetId(), new TweetInterface() {
                            @Override
                            public void success(Tweet tweet) {
                                Log.d("Success", "retweeted ");
                                tweet.setRetweets(tweet.getRetweets());
                            }

                            @Override
                            public void failure(Throwable throwable) {

                                Log.d("Exception", throwable.getMessage());
                            }

                            @Override
                            public void errorExists() {

                                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    });


                });
            });

        }
        holder.retweetsCount.setText(String.valueOf(tweets.get(position).getRetweets().size()));
        holder.commentsCount.setText(String.valueOf(tweets.get(position).getComments().size()));
        holder.likesCount.setText(String.valueOf(tweets.get(position).getLikes().size()));
        holder.tweetTitle.setText(tweets.get(position).getTitle());
        holder.timePosted.setText(ConvertToDate.getTimeAgo(tweets.get(position).getTimePosted()));



        holder.name.setText(tweets.get(position).getUserDetails().getName());
        holder.username.setText("@"+tweets.get(position).getUserDetails().getUsername());
        String currentUserId=Preferences.getItemFromSP(context,"userId");

        StorageReference storageReference= FirebaseStorage.getInstance().getReference("images/"+tweets.get(position).getUserDetails().getProfilePic());

        Task<Uri> uriTask=storageReference.getDownloadUrl();

        uriTask.addOnSuccessListener(uri1 -> {
            Glide.with(context).load(uri1).into(holder.profilePic);

        });
        if(tweets.get(position).getLikes().contains(currentUserId)){
            holder.likesCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart, 0, 0, 0);
        }else{
            holder.likesCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_white, 0, 0, 0);
        }


//        liking/unliking tweet

        holder.likesCount.setOnClickListener(view -> {
            if(tweets.get(position).getLikes().contains(currentUserId)){
                holder.likesCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_white, 0, 0, 0);
                holder.likesCount.setText(String.valueOf(tweets.get(position).getLikes().size()-1));

            }else {
                holder.likesCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart, 0, 0, 0);
                holder.likesCount.setText(String.valueOf(tweets.get(position).getLikes().size()+1));
            }
            RetrofitHandler.likeTweet(context, tweets.get(holder.getAdapterPosition()).getTweetId(), new TweetInterface() {
                @Override
                public void success(Tweet tweet) {
                    Log.d("Success","Liked ");
                    tweets.get(holder.getAdapterPosition()).setLikes(tweet.getLikes());
                }

                @Override
                public void failure(Throwable throwable) {

                    Log.d("Exception",throwable.getMessage());
                }

                @Override
                public void errorExists() {

                    Toast.makeText(context,"Something went wrong!",Toast.LENGTH_SHORT).show();
                }
            });

        });


//        retweeting/undoing retweet
        if(tweets.get(position).getRetweets().contains(currentUserId)){
            holder.retweetsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweted, 0, 0, 0);
        }else{
            holder.retweetsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweet_white, 0, 0, 0);
        }



        holder.retweetsCount.setOnClickListener(view -> {
            holder.retweetsCount.setEnabled(false);
            if(tweets.get(holder.getAdapterPosition()).getRetweets().contains(currentUserId)){
                holder.retweetsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweet_white, 0, 0, 0);
                holder.retweetsCount.setText(String.valueOf(tweets.get(position).getRetweets().size()-1));

            }else{
                holder.retweetsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweted, 0, 0, 0);
                holder.retweetsCount.setText(String.valueOf(tweets.get(position).getRetweets().size()+1));
            }
            RetrofitHandler.retweetTweet(context, tweets.get(holder.getAdapterPosition()).getTweetId(), new TweetInterface() {
                @Override
                public void success(Tweet tweet) {
                    Log.d("Success","retweeted ");
                    tweets.get(holder.getAdapterPosition()).setRetweets(tweet.getRetweets());
                    holder.retweetsCount.setEnabled(true);
                }

                @Override
                public void failure(Throwable throwable) {
                    holder.retweetsCount.setEnabled(true);

                    Log.d("Exception",throwable.getMessage());
                }

                @Override
                public void errorExists() {

                    holder.retweetsCount.setEnabled(true);
                    Toast.makeText(context,"Something went wrong!",Toast.LENGTH_SHORT).show();
                }
            });
        });


        holder.profilePic.setOnClickListener(view->{
            Intent intent;

            if(tweets.get(position).getUserDetails().getUserId().equals(currentUserId)){
                intent = new Intent(context, MyProfileActivity.class);

                intent.putExtra("user",tweets.get(position).getUserDetails());

            }else {
                intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("user",tweets.get(holder.getAdapterPosition()).getUserDetails());
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        holder.itemView.setOnClickListener(view -> {
            Intent intent=new Intent(context,TweetActivity.class);
            intent.putExtra("tweet",tweets.get(holder.getAdapterPosition()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView timePosted,username,name,tweetTitle,commentsCount,likesCount,retweetsCount;
        ImageView profilePic ,tweetImage;
        CardView cardView;


        public MyViewHolder(@NonNull SingleTweetBinding binding) {
            super(binding.getRoot());
            timePosted= binding.tweetTime;
            username= binding.username;
            name= binding.name;
            tweetTitle= binding.tweetTitle;
            commentsCount= binding.openComments;
            likesCount=binding.likeTweet;
            retweetsCount= binding.retweetTweet;
            profilePic= binding.profilePic;
            tweetImage= binding.tweetImage;
            cardView=binding.cardView;

        }
    }
}
