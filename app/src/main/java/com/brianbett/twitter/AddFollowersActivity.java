package com.brianbett.twitter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.brianbett.twitter.databinding.ActivityAddFollowersBinding;
import com.brianbett.twitter.retrofit.FollowersInterface;
import com.brianbett.twitter.retrofit.RetrofitHandler;
import com.brianbett.twitter.retrofit.User;
import com.brianbett.twitter.retrofit.UserDetails;

import java.util.ArrayList;
import java.util.List;

public class AddFollowersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityAddFollowersBinding binding=ActivityAddFollowersBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        RecyclerView recyclerView=binding.usersRecyclerView;
        ProgressBar progressBar=binding.progressView;
        TextView noUsers=binding.noUsers;


        ArrayList<UserDetails> users=new ArrayList<>();

        UsersRecyclerViewAdapter adapter=new UsersRecyclerViewAdapter(users,getApplicationContext());

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        progressBar.setVisibility(View.VISIBLE);

        RetrofitHandler.getAllUsers(getApplicationContext(), new FollowersInterface() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void success(List<UserDetails> usersList) {
                if(usersList.size()==0){
                    noUsers.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);

                }else {
                    users.addAll(usersList);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
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
                Log.d("Exception",errorMessage);
            }
        });
    }
}