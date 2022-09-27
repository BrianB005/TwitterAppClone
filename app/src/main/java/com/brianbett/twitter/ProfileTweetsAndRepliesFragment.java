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

import com.brianbett.twitter.databinding.FragmentProfileTweetsAndRepliesBinding;
import com.brianbett.twitter.retrofit.ProfileTweetOrComment;
import com.brianbett.twitter.retrofit.RetrofitHandler;
import com.brianbett.twitter.retrofit.TweetsAndCommentsInterface;

import java.util.ArrayList;
import java.util.List;


public class ProfileTweetsAndRepliesFragment extends Fragment {


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FragmentProfileTweetsAndRepliesBinding binding=FragmentProfileTweetsAndRepliesBinding.inflate(inflater, container, false);

        RecyclerView recyclerView= binding.tweetsRecyclerView;
        ProgressBar progressBar= binding.progressView;

        TextView noTweets= binding.noTweets;
        ArrayList<ProfileTweetOrComment> tweetsArrayList=new ArrayList<>();


        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        assert  getActivity()!=null;
        ProfileTweetsAndCommentsRecyclerViewAdapter recyclerViewAdapter=new ProfileTweetsAndCommentsRecyclerViewAdapter(tweetsArrayList,getActivity().getApplicationContext());
        if(getArguments()!=null) {
            String userId = getArguments().getString("userId");
            RetrofitHandler.getUserProfileTweetsAndComments(getContext(),userId, new TweetsAndCommentsInterface() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void success(List<ProfileTweetOrComment> tweets) {
                    if (tweets.size() == 0) {
                        noTweets.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        noTweets.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
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