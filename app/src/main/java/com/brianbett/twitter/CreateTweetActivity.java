package com.brianbett.twitter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.brianbett.twitter.databinding.ActivityCreateTweetBinding;
import com.brianbett.twitter.retrofit.RetrofitHandler;
import com.brianbett.twitter.retrofit.Tweet;
import com.brianbett.twitter.retrofit.TweetInterface;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;

public class CreateTweetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCreateTweetBinding binding=ActivityCreateTweetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.closeActivity.setOnClickListener(view->finish());

        EditText tweetInput=binding.tweetInput;
        MaterialButton createTweetBtn=binding.createTweet;
        ProgressBar progressBar=binding.progressView;

        tweetInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                createTweetBtn.setEnabled(editable.length() > 0);
            }
        });

        createTweetBtn.setOnClickListener(view->{
            String tweetTitle=tweetInput.getText().toString();
            if(tweetTitle.isEmpty()){
                tweetInput.setError("Tweet can't be empty!");
            }else{
                progressBar.setVisibility(View.VISIBLE);
                HashMap<String,String> tweetDetails=new HashMap<>();
                tweetDetails.put("title",tweetTitle);
                RetrofitHandler.createTweet(getApplicationContext(), tweetDetails, new TweetInterface() {
                    @Override
                    public void success(Tweet tweet) {
                        progressBar.setVisibility(View.GONE);
                        tweetInput.setText("");
                    }

                    @Override
                    public void failure(Throwable throwable) {

                        Toast.makeText(getApplicationContext(),throwable.getMessage(),Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void errorExists() {

//                        Toast.makeText(getApplicationContext(),"Something went wrong! ",Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });

        binding.shareImage.setOnClickListener(view -> startActivity(new Intent(CreateTweetActivity.this,CreateImageTweetActivity.class)));

    }
}