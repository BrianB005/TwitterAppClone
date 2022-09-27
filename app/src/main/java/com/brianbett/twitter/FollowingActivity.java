package com.brianbett.twitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.brianbett.twitter.databinding.ActivityFollowingBinding;
import com.brianbett.twitter.retrofit.FollowersInterface;
import com.brianbett.twitter.retrofit.RetrofitHandler;
import com.brianbett.twitter.retrofit.User;
import com.brianbett.twitter.retrofit.UserDetails;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class FollowingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityFollowingBinding binding= ActivityFollowingBinding.inflate(getLayoutInflater());
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        setContentView(binding.getRoot());

        MaterialToolbar toolbar=binding.toolBar;

        toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId()==R.id.add_followers){
                startActivity(new Intent(FollowingActivity.this,AddFollowersActivity.class));
            }
            return false;
        });

        toolbar.getChildAt(0).setOnClickListener(view -> super.onBackPressed());

        Intent intent=getIntent();
        String userId=intent.getStringExtra("userId");




        RecyclerView recyclerView=binding.followingRecyclerView;
        ProgressBar progressBar=binding.progressView;
        TextView noFollowing=binding.noFollowing;


        ArrayList<UserDetails> following=new ArrayList<>();

        FollowingRecyclerViewAdapter adapter=new FollowingRecyclerViewAdapter(following,getApplicationContext());

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        progressBar.setVisibility(View.VISIBLE);

        recyclerView.setVisibility(View.GONE);

        if(userId==null) {
            RetrofitHandler.getMyFollowing(getApplicationContext(), new FollowersInterface() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void success(List<UserDetails> usersList) {
                    if (usersList.size() == 0) {
                        noFollowing.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);

                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        following.addAll(usersList);
                        adapter.notifyDataSetChanged();

                    }
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void failure(Throwable throwable) {
                    progressBar.setVisibility(View.GONE);
                    Log.d("Exception ", throwable.getMessage());
                }

                @Override
                public void errorExists(String errorMessage) {
                    progressBar.setVisibility(View.GONE);
                    Log.d("Exception ", errorMessage);
                }
            });
        }else{
            RetrofitHandler.getUserFollowing(getApplicationContext(),userId, new FollowersInterface() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void success(List<UserDetails> usersList) {
                    if (usersList.size() == 0) {
                        noFollowing.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);

                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        following.addAll(usersList);
                        adapter.notifyDataSetChanged();


                    }
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void failure(Throwable throwable) {
                    progressBar.setVisibility(View.GONE);
                    Log.d("Exception ", throwable.getMessage());
                }
                @Override
                public void errorExists(String errorMessage) {
                    progressBar.setVisibility(View.GONE);
                    Log.d("Exception ", errorMessage);
                }
            });
        }

    }



}