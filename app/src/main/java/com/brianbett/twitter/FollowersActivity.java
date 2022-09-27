package com.brianbett.twitter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.usage.NetworkStatsManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.brianbett.twitter.databinding.ActivityFollowersBinding;
import com.brianbett.twitter.retrofit.FollowersInterface;
import com.brianbett.twitter.retrofit.RetrofitHandler;
import com.brianbett.twitter.retrofit.User;
import com.brianbett.twitter.retrofit.UserDetails;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class FollowersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityFollowersBinding binding=ActivityFollowersBinding.inflate(getLayoutInflater());
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        setContentView(binding.getRoot());



        MaterialToolbar toolbar=binding.toolBar;

        toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId()==R.id.add_followers){
                startActivity(new Intent(FollowersActivity.this,AddFollowersActivity.class));
            }
            return false;
        });

        toolbar.getChildAt(0).setOnClickListener(view -> super.onBackPressed());


        RecyclerView recyclerView=binding.usersRecyclerView;
        ProgressBar progressBar=binding.progressView;
        TextView noFollowers=binding.noFollowers;

        Intent intent=getIntent();


        ArrayList<UserDetails> followers=new ArrayList<>();

        FollowersRecyclerViewAdapter adapter=new FollowersRecyclerViewAdapter(followers,getApplicationContext());

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);


        String userId=intent.getStringExtra("userId");
        if(userId==null){

            RetrofitHandler.getMyFollowers(getApplicationContext(), new FollowersInterface() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void success(List<UserDetails> usersList) {
                    if(usersList.size()==0){
                        noFollowers.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);

                    }else {
                        followers.addAll(usersList);
                        adapter.notifyDataSetChanged();
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void failure(Throwable throwable) {
                    progressBar.setVisibility(View.GONE);
                    Log.d("Exception",throwable.getMessage());

                }

                @Override
                public void errorExists(String errorMessage) {
                    progressBar.setVisibility(View.GONE);
                    Log.d("Exception E",errorMessage);
                }
            });
        }else {
            RetrofitHandler.getUserFollowers(getApplicationContext(),userId, new FollowersInterface() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void success(List<UserDetails> usersList) {
                    if(usersList.size()==0){
                        noFollowers.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);

                    }else {
                        followers.addAll(usersList);
                        adapter.notifyDataSetChanged();
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void failure(Throwable throwable) {
                    progressBar.setVisibility(View.GONE);
                    Log.d("Exception",throwable.getMessage());

                }

                @Override
                public void errorExists(String errorMessage) {
                    progressBar.setVisibility(View.GONE);
                    Log.d("Exception E",errorMessage);
                }
            });
        }






    }
}