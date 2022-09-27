package com.brianbett.twitter;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.brianbett.twitter.databinding.FragmentProfileTweetsBinding;
import com.brianbett.twitter.retrofit.RetrofitHandler;
import com.brianbett.twitter.retrofit.Tweet;
import com.brianbett.twitter.retrofit.TweetsInterface;

import java.util.ArrayList;
import java.util.List;


public class ProfileTweetsFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentProfileTweetsBinding binding=FragmentProfileTweetsBinding.inflate(inflater, container, false);

        RecyclerView recyclerView= binding.tweetsRecyclerView;
        ProgressBar progressBar= binding.progressView;

        TextView noTweets= binding.noTweets;
        ArrayList<Tweet> tweetsArrayList=new ArrayList<>();


        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);




        if(getArguments()!=null){
            String userId=getArguments().getString("userId");
            RetrofitHandler.getUserProfileTweets(getContext(),userId, new TweetsInterface() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void success(List<Tweet> tweets) {
                    if(tweets.size()==0){
                        noTweets.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }else{
                        noTweets.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        assert  getActivity()!=null;
                        ProfileTweetsRecyclerViewAdapter recyclerViewAdapter=new ProfileTweetsRecyclerViewAdapter(getActivity().getApplicationContext(),tweetsArrayList,userId);
                        recyclerView.setAdapter(recyclerViewAdapter);
                        tweetsArrayList.addAll(tweets);

                        recyclerViewAdapter.notifyDataSetChanged();
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                    }
                    progressBar.setVisibility(View.GONE);

                }

                @Override
                public void failure(Throwable throwable) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void errorExists(String errorMessage) {
                    progressBar.setVisibility(View.GONE);
                }
            });
        }


        return binding.getRoot();
    }
}