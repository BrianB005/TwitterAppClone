package com.brianbett.twitter;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.brianbett.twitter.databinding.ActivityTweetBinding;
import com.brianbett.twitter.retrofit.CommentInterface;
import com.brianbett.twitter.retrofit.ProfileTweetOrComment;
import com.brianbett.twitter.retrofit.RetrofitHandler;
import com.brianbett.twitter.retrofit.Tweet;
import com.brianbett.twitter.retrofit.TweetInterface;
import com.brianbett.twitter.retrofit.TweetsAndCommentsInterface;
import com.bumptech.glide.Glide;
import com.github.drjacky.imagepicker.ImagePicker;
import com.github.drjacky.imagepicker.constant.ImageProvider;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.security.acl.AclNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

public class TweetActivity extends AppCompatActivity {

    ActivityTweetBinding binding;
    TextView timePosted,datePosted,username,name,tweetTitle,commentsCount,likesCount,retweetsCount,retweetTweet,likeTweet,commentTweet,noComments;
    ImageView profilePic ,tweetImage;
    CardView cardView;
    RecyclerView commentsRecyclerView;
    ImageButton commentImage;
    View replyLayout;
    TextView replyUsername;
    EditText commentInput;
    MaterialButton replyBtn;
    ProgressBar progressBar;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityTweetBinding.inflate(getLayoutInflater());
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        setContentView(binding.getRoot());

        Intent intent=getIntent();

        timePosted= binding.tweetTime;
        datePosted= binding.tweetDate;
        username= binding.username;
        name= binding.name;
        tweetTitle= binding.tweetTitle;
        commentTweet= binding.openComments;
        likeTweet=binding.likeTweet;
        retweetTweet= binding.retweetTweet;
        profilePic= binding.profilePic;
        tweetImage= binding.tweetImage;
        cardView=binding.cardView;
        retweetsCount=binding.numberOfRetweets;
        likesCount=binding.numberOfLikes;
        commentsCount=binding.numberOfComments;
        commentsRecyclerView=binding.commentsRecyclerView;
        noComments=binding.noComments;
        progressBar=binding.progressView;
        commentImage=binding.commentImage;
        commentInput=binding.replyInput;
        replyLayout=binding.replyLayout;
        replyBtn=binding.replyBtn;
        replyUsername=binding.replyUsername;




        Tweet tweet=(Tweet) intent.getSerializableExtra("tweet");


        //        opening gallery when one wants to reply with an image

        ActivityResultLauncher<Intent> launcher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),(ActivityResult result)-> {
            if(result.getResultCode()==RESULT_OK){
                assert result.getData() != null;
                Uri uri=result.getData().getData();
                Intent intent1=new Intent(TweetActivity.this,CreateImageReplyActivity.class);
                intent1.putExtra("image",uri.toString());
                intent1.putExtra("replyUsername",tweet.getUserDetails().getUsername());
                intent1.putExtra("tweetId",tweet.getTweetId());
                startActivity(intent1);
            }else if(result.getResultCode()== ImagePicker.RESULT_ERROR){
                Log.d("Error","Something went wrong!");
            }
        });

        commentImage.setOnClickListener(view -> {
            ImagePicker.Companion.with(TweetActivity.this)
                    .crop()
                    .provider(ImageProvider.BOTH)
                    .createIntentFromDialog(new Function1() {
                        public Object invoke(Object var1) {
                            this.invoke((Intent) var1);
                            return Unit.INSTANCE;
                        }

                        public void invoke(@NotNull Intent it) {
                            Intrinsics.checkNotNullParameter(it, "it");
                            launcher.launch(it);
                        }
                    });
        });

        progressBar.setVisibility(View.VISIBLE);
        noComments.setVisibility(View.GONE);
        ArrayList<ProfileTweetOrComment> comments=new ArrayList<>();

        CommentsRecyclerViewAdapter recyclerViewAdapter=new CommentsRecyclerViewAdapter(comments,getApplicationContext());
        commentsRecyclerView.setAdapter(recyclerViewAdapter);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        RetrofitHandler.getATweetsComments(getApplicationContext(), tweet.getTweetId(), new TweetsAndCommentsInterface() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void success(List<ProfileTweetOrComment> tweetComments) {
                progressBar.setVisibility(View.GONE);
                if(tweetComments.isEmpty()){
                    noComments.setVisibility(View.VISIBLE);
                }else{
                    commentsRecyclerView.setVisibility(View.VISIBLE);
                    comments.addAll(tweetComments);
                    recyclerViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void failure(Throwable throwable) {
                progressBar.setVisibility(View.GONE);
                Log.d("Exception",throwable.getMessage());

            }

            @Override
            public void errorExists(String errorMessage) {

                progressBar.setVisibility(View.GONE);
                Log.d("Exception",errorMessage);
            }
        });

        if(tweet.getImages().isEmpty()){
            cardView.setVisibility(View.GONE);
        }else{
            cardView.setVisibility(View.VISIBLE);
            StorageReference storageReference= FirebaseStorage.getInstance().getReference("images/"+tweet.getImages().get(0));

            Task<Uri> uriTask=storageReference.getDownloadUrl();

            uriTask.addOnSuccessListener(uri1 -> {
                Glide.with(getApplicationContext()).load(uri1).into(tweetImage);
            });
        }
        StorageReference storageReference= FirebaseStorage.getInstance().getReference("images/"+tweet.getUserDetails().getProfilePic());

        Task<Uri> uriTask=storageReference.getDownloadUrl();

        uriTask.addOnSuccessListener(uri1 -> {
            Glide.with(getApplicationContext()).load(uri1).into(profilePic);

        });



        datePosted.setText(ConvertToDate.getDate(tweet.getTimePosted()));
        timePosted.setText(ConvertToDate.getTime(tweet.getTimePosted()));
        name.setText(tweet.getUserDetails().getName());
        username.setText("@"+tweet.getUserDetails().getUsername());


        tweetTitle.setText(tweet.getTitle());
        retweetsCount.setText(String.valueOf(tweet.getRetweets().size()));
        likesCount.setText(String.valueOf(tweet.getLikes().size()));
        commentsCount.setText(String.valueOf(tweet.getComments().size()));

        String currentUserId=Preferences.getItemFromSP(getApplicationContext(),"userId");
        if(tweet.getLikes().contains(currentUserId)){
            likeTweet.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart, 0, 0, 0);
        }else{
            likeTweet.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_white, 0, 0, 0);
        }


        //        liking/unliking tweet

        likeTweet.setOnClickListener(view -> {
            if(tweet.getLikes().contains(currentUserId)){
                likeTweet.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_white, 0, 0, 0);
                likeTweet.setText(String.valueOf(tweet.getLikes().size()-1));

            }else {
                likeTweet.setCompoundDrawablesWithIntrinsicBounds(R.drawable.heart, 0, 0, 0);
                likeTweet.setText(String.valueOf(tweet.getLikes().size()+1));
            }
            RetrofitHandler.likeTweet(getApplicationContext(), tweet.getTweetId(), new TweetInterface() {
                @Override
                public void success(Tweet tweet) {
                    Log.d("Success","Liked ");
                    tweet.setLikes(tweet.getLikes());
                }

                @Override
                public void failure(Throwable throwable) {

                    Log.d("Exception",throwable.getMessage());
                }

                @Override
                public void errorExists() {

                    Toast.makeText(getApplicationContext(),"Something went wrong!",Toast.LENGTH_SHORT).show();
                }
            });
        });



//        retweeting/undoing retweet
        if(tweet.getRetweets().contains(currentUserId)){
            retweetTweet.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweted, 0, 0, 0);
        }else{
            retweetTweet.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweet_white, 0, 0, 0);
        }


        retweetTweet.setOnClickListener(view -> {
            if(tweet.getRetweets().contains(currentUserId)){
                retweetTweet.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweet_white, 0, 0, 0);
                retweetTweet.setText(String.valueOf(tweet.getRetweets().size()-1));

            }else{
                retweetTweet.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweted, 0, 0, 0);
                retweetTweet.setText(String.valueOf(tweet.getRetweets().size()+1));
            }
            RetrofitHandler.retweetTweet(getApplicationContext(), tweet.getTweetId(), new TweetInterface() {
                @Override
                public void success(Tweet tweet) {
                    Log.d("Success","retweeted ");
                    tweet.setRetweets(tweet.getRetweets());
                }
                @Override
                public void failure(Throwable throwable) {

                    Log.d("Exception",throwable.getMessage());
                }

                @Override
                public void errorExists() {

                    Toast.makeText(getApplicationContext(),"Something went wrong!",Toast.LENGTH_SHORT).show();
                }
            });
        });

        replyBtn.setOnClickListener(view -> {
            hideKeyboard(TweetActivity.this);
            replyBtn.setEnabled(false);
            String commentTitle=commentInput.getText().toString();
            if(commentTitle.isEmpty()){
                commentInput.setError("Comment cannot be empty!");
            }else{
                commentInput.setError(null);
                HashMap<String,String> commentDetails=new HashMap<>();
                commentDetails.put("title",commentTitle);
                RetrofitHandler.createComment(getApplicationContext(), tweet.getTweetId(), commentDetails, new CommentInterface() {
                    @Override
                    public void success(ProfileTweetOrComment comment) {
                        Toast toast=new Toast(getApplicationContext());
                        View customToast=getLayoutInflater().inflate(R.layout.custom_toast,(ViewGroup)findViewById(R.id.customToast),false );
                        toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL,0,40);

                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(customToast);
                        toast.show();

                        replyBtn.setEnabled(true);
                        commentInput.setText("");



                    }

                    @Override
                    public void failure(Throwable throwable) {

                        replyBtn.setEnabled(true);
                        Toast.makeText(getApplicationContext(),"Something went wrong!",Toast.LENGTH_SHORT).show();
                        Log.d("Exception",throwable.getMessage());
                    }

                    @Override
                    public void errorExists(String errorMessage) {
                        replyBtn.setEnabled(false);

                        Log.d("Error",errorMessage);
                    }
                });
            }
        });

        replyUsername.setText("@"+tweet.getUserDetails().getUsername());

        commentInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(charSequence.length()>0){
                    replyLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in));
                    replyLayout.setVisibility(View.VISIBLE);
                }else{

                    replyLayout.setVisibility(View.GONE);
                    replyLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out));
                }
            }



            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}