package com.brianbett.twitter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.brianbett.twitter.databinding.FragmentHomeBinding;
import com.brianbett.twitter.retrofit.RetrofitHandler;
import com.brianbett.twitter.retrofit.TweetsInterface;
import com.brianbett.twitter.retrofit.Tweet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class HomeFragment extends Fragment {
    FragmentHomeBinding binding;
    ArrayList<Tweet> timelineTweets;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    TimelineTweetsRecyclerViewAdapter recyclerViewAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentHomeBinding.inflate(inflater,container,false);

        recyclerView= binding.tweetRecyclerView;
        progressBar= binding.progressView;

        DividerItemDecoration itemDecorator = new DividerItemDecoration(requireActivity(), DividerItemDecoration.HORIZONTAL);
        itemDecorator.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(requireActivity(), R.drawable.recycler_view_divider)));
        recyclerView.addItemDecoration(itemDecorator);

        timelineTweets=new ArrayList<>();
        assert getActivity()!=null;
        recyclerViewAdapter=new TimelineTweetsRecyclerViewAdapter(getActivity().getApplicationContext(),timelineTweets);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        RetrofitHandler.getTimelineTweets(getActivity(), new TweetsInterface() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void success(List<Tweet> allTimelineTweets) {
                if(allTimelineTweets.size()==0){

                    binding.noTweets.setVisibility(View.VISIBLE);
                    binding.addFollowers.setOnClickListener(view -> startActivity(new Intent(getContext(),AddFollowersActivity.class)));
                    recyclerView.setVisibility(View.GONE);
                }else {
                    timelineTweets.addAll(allTimelineTweets);
                    recyclerViewAdapter.notifyDataSetChanged();
                    binding.noTweets.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void failure(Throwable throwable) {

                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
//                Toast.makeText(getContext(),throwable.getMessage(),Toast.LENGTH_SHORT).show();
            }
            @Override
            public void errorExists(String errorMessage) {

                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(),errorMessage,Toast.LENGTH_SHORT).show();
            }
        });



//        opening create tweet activity
        binding.floatingActionButton.setOnClickListener(view->{
            startActivity(new Intent(getActivity(),CreateTweetActivity.class));
        });

        return binding.getRoot();
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        RetrofitHandler.getTimelineTweets(getActivity(), new TweetsInterface() {
//            @SuppressLint("NotifyDataSetChanged")
//            @Override
//            public void success(List<Tweet> allTimelineTweets) {
//                if(allTimelineTweets.size()==0){
//                    binding.noTweets.setVisibility(View.VISIBLE);
//                    recyclerView.setVisibility(View.GONE);
//                }else {
//                    timelineTweets.addAll(allTimelineTweets);
//                    recyclerViewAdapter.notifyDataSetChanged();
//                    binding.noTweets.setVisibility(View.GONE);
//                    recyclerView.setVisibility(View.VISIBLE);
//                }
//                progressBar.setVisibility(View.GONE);
//
//            }
//
//            @Override
//            public void failure(Throwable throwable) {
//
//                progressBar.setVisibility(View.GONE);
//                recyclerView.setVisibility(View.VISIBLE);
//                Toast.makeText(getContext(),throwable.getMessage(),Toast.LENGTH_SHORT).show();
//            }
//            @Override
//            public void errorExists(String errorMessage) {
//
//                progressBar.setVisibility(View.GONE);
//                recyclerView.setVisibility(View.VISIBLE);
//                Toast.makeText(getContext(),errorMessage,Toast.LENGTH_SHORT).show();
//            }
//        });

//    }
}