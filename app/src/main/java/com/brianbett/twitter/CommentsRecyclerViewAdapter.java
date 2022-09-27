package com.brianbett.twitter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.brianbett.twitter.databinding.SingleMyProfileTweetOrCommentBinding;
import com.brianbett.twitter.retrofit.ProfileTweetOrComment;
import com.brianbett.twitter.retrofit.RetrofitHandler;
import com.brianbett.twitter.retrofit.Tweet;
import com.brianbett.twitter.retrofit.TweetInterface;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class CommentsRecyclerViewAdapter extends RecyclerView.Adapter<CommentsRecyclerViewAdapter.MyViewHolder> {

    private final ArrayList<ProfileTweetOrComment> tweets;
    private final Context context;
    LayoutInflater inflater;

    public CommentsRecyclerViewAdapter(ArrayList<ProfileTweetOrComment> tweets, Context context) {
        this.tweets = tweets;
        this.context=context;
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        inflater=LayoutInflater.from(parent.getContext());
        SingleMyProfileTweetOrCommentBinding binding=SingleMyProfileTweetOrCommentBinding.inflate(inflater,parent,false);
        return new MyViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {




        holder.replyingTo.setVisibility(View.VISIBLE);
        holder.replyUsername.setText("@"+tweets.get(position).getReplyingTo().get(0));

        if(tweets.get(position).getImages().isEmpty()){
            holder.cardView.setVisibility(View.GONE);
        }else{
            holder.cardView.setVisibility(View.VISIBLE);
            StorageReference storageReference= FirebaseStorage.getInstance().getReference("images/"+tweets.get(position).getImages().get(0));

            Task<Uri> uriTask=storageReference.getDownloadUrl();

            uriTask.addOnSuccessListener(uri1 -> {
                Glide.with(context).load(uri1).into(holder.tweetImage);
                holder.tweetImage.setOnClickListener(view -> {
                    @SuppressLint("InflateParams") View popUpView = inflater.inflate(R.layout.view_image_popup, null);
                    PopupWindow popupWindow = new PopupWindow(popUpView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
                    popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
                    ImageView tweetImage = popUpView.findViewById(R.id.tweet_image);
                    TextView retweets, comments, likes;
                    retweets = popUpView.findViewById(R.id.retweet_tweet);
                    comments = popUpView.findViewById(R.id.open_comments);
                    likes = popUpView.findViewById(R.id.like_tweet);


                    Tweet tweet = tweets.get(holder.getAdapterPosition());
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

                    likes.setOnClickListener(view3 -> {
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
        StorageReference storageReference= FirebaseStorage.getInstance().getReference("images/"+tweets.get(position).getUserDetails().getProfilePic());

        Task<Uri> uriTask=storageReference.getDownloadUrl();

        uriTask.addOnSuccessListener(uri1 -> Glide.with(context).load(uri1).into(holder.profilePic));
        holder.retweetsCount.setText(String.valueOf(tweets.get(position).getRetweets().size()));
        holder.commentsCount.setText(String.valueOf(tweets.get(position).getComments().size()));
        holder.likesCount.setText(String.valueOf(tweets.get(position).getLikes().size()));
        holder.tweetTitle.setText(tweets.get(position).getTitle());
        holder.timePosted.setText(ConvertToDate.getTimeAgo(tweets.get(position).getTimePosted()));
        holder.name.setText(tweets.get(position).getUserDetails().getName());
        holder.username.setText("@"+tweets.get(position).getUserDetails().getUsername());
        String currentUserId=Preferences.getItemFromSP(context,"userId");
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
            RetrofitHandler.likeComment(context, tweets.get(holder.getAdapterPosition()).getTweetId(), new TweetInterface() {
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
            if(tweets.get(holder.getAdapterPosition()).getRetweets().contains(currentUserId)){
                holder.retweetsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweet_white, 0, 0, 0);
                holder.retweetsCount.setText(String.valueOf(tweets.get(position).getRetweets().size()-1));

            }else{
                holder.retweetsCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweted, 0, 0, 0);
                holder.retweetsCount.setText(String.valueOf(tweets.get(position).getRetweets().size()+1));
            }
            RetrofitHandler.retweetComment(context, tweets.get(holder.getAdapterPosition()).getTweetId(), new TweetInterface() {
                @Override
                public void success(Tweet tweet) {
                    Log.d("Success","retweeted ");
                    tweets.get(holder.getAdapterPosition()).setRetweets(tweet.getRetweets());
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
//
//        holder.itemView.setOnClickListener(view -> {
//            Intent intent=new Intent(context,TweetActivity.class);
//            intent.putExtra("tweet",tweets.get(holder.getAdapterPosition()));
//            context.startActivity(intent);
//        });


        holder.profilePic.setOnClickListener(view->{
            if(!tweets.get(position).getUserDetails().getUserId().equals(currentUserId)){
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("user", tweets.get(holder.getAdapterPosition()).getUserDetails());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    protected static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView timePosted,username,name,tweetTitle,commentsCount,likesCount,retweetsCount,retweeted;
        ImageView profilePic ,tweetImage;
        CardView cardView;
        View replyingTo;
        TextView replyUsername;


        public MyViewHolder(@NonNull SingleMyProfileTweetOrCommentBinding binding) {
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
            retweeted=binding.retweetedBy;
            cardView=binding.cardView;
            replyingTo=binding.replyingTo;
            replyUsername= binding.replyUsername;

        }
    }
}
