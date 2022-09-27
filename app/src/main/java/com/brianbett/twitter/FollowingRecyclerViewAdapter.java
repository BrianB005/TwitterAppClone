package com.brianbett.twitter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brianbett.twitter.databinding.SingleFollowerBinding;
import com.brianbett.twitter.databinding.SingleFollowingBinding;
import com.brianbett.twitter.retrofit.FollowUserSuccess;
import com.brianbett.twitter.retrofit.RetrofitHandler;
import com.brianbett.twitter.retrofit.User;
import com.brianbett.twitter.retrofit.UserDetails;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class FollowingRecyclerViewAdapter extends RecyclerView.Adapter<FollowingRecyclerViewAdapter.MyViewHolder> {

    private final ArrayList<UserDetails> following;
    private final Context context;

    public FollowingRecyclerViewAdapter(ArrayList<UserDetails> following, Context context) {
        this.following = following;
        this.context = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        SingleFollowingBinding binding=SingleFollowingBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new FollowingRecyclerViewAdapter.MyViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull FollowingRecyclerViewAdapter.MyViewHolder holder, int position) {


        StorageReference storageReference= FirebaseStorage.getInstance().getReference("images/"+following.get(position).getProfilePic());

        Task<Uri> uriTask=storageReference.getDownloadUrl();

        uriTask.addOnSuccessListener(uri1 -> {
            Glide.with(context).load(uri1).into(holder.profilePic);

        });


        holder.username.setText("@"+following.get(position).getUsername());
        holder.name.setText(following.get(position).getName());


        holder.profilePic.setOnClickListener(view->{

            Intent intent = new Intent(context, ProfileActivity.class);
            intent.putExtra("user", following.get(holder.getAdapterPosition()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

        });

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ProfileActivity.class);
            intent.putExtra("user", following.get(holder.getAdapterPosition()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        String currentUserId=Preferences.getItemFromSP(context,"userId");
        if(following.get(holder.getAdapterPosition()).getFollowing().contains(currentUserId)) {
            holder.followsYou.setVisibility(View.VISIBLE);
        }else{
            holder.followsYou.setVisibility(View.GONE);
        }

        holder.followingBtn.setOnClickListener(view -> {
            holder.followingBtn.setVisibility(View.GONE);
            String userId=following.get(holder.getAdapterPosition()).getUserId();
            RetrofitHandler.unFollowUser(context, userId, new FollowUserSuccess() {
                @Override
                public void success(String message) {

                    holder.followBackBtn.setEnabled(true);
                }

                @Override
                public void errorExists() {

                    holder.followBackBtn.setEnabled(true);
                }

                @Override
                public void failure(Throwable throwable) {

                    holder.followBackBtn.setEnabled(true);
                }
            });
            if(following.get(holder.getAdapterPosition()).getFollowing().contains(currentUserId)){
                holder.followBackBtn.setVisibility(View.VISIBLE);
                holder.followBackBtn.setOnClickListener(view1 -> {
                    holder.followBackBtn.setVisibility(View.GONE);
                    holder.followingBtn.setVisibility(View.VISIBLE);
                    holder.followingBtn.setEnabled(false);
                    RetrofitHandler.followUser(context, userId, new FollowUserSuccess() {
                        @Override
                        public void success(String message) {
                            holder.followingBtn.setEnabled(true);
                        }

                        @Override
                        public void errorExists() {

                            holder.followingBtn.setEnabled(true);
                        }

                        @Override
                        public void failure(Throwable throwable) {

                            holder.followingBtn.setEnabled(true);
                        }
                    });
                });
            }else{
                holder.followBtn.setVisibility(View.VISIBLE);
                holder.followBtn.setOnClickListener(view1 -> {
                    holder.followingBtn.setVisibility(View.VISIBLE);
                    holder.followBtn.setVisibility(View.GONE);
                    holder.followingBtn.setEnabled(false);
                    RetrofitHandler.followUser(context, userId, new FollowUserSuccess() {
                        @Override
                        public void success(String message) {
                            holder.followingBtn.setEnabled(true);
                        }

                        @Override
                        public void errorExists() {

                            holder.followingBtn.setEnabled(true);
                        }

                        @Override
                        public void failure(Throwable throwable) {

                            holder.followingBtn.setEnabled(true);
                        }
                    });
                });
            }
            if (following.get(position).getUserId().equals(currentUserId)){
                holder.followingBtn.setVisibility(View.GONE);
                holder.followBackBtn.setVisibility(View.GONE);
                holder.followBtn.setVisibility(View.GONE);
            }

        });

    }

    @Override
    public int getItemCount() {
        return following.size();
    }

    protected static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView profilePic;
        MaterialButton followingBtn,followBackBtn,followBtn;
        TextView username,name,profileDescription,followsYou;
        public MyViewHolder(@NonNull SingleFollowingBinding binding) {
            super(binding.getRoot());
            profilePic=binding.profilePic;
            followingBtn= binding.followingBtn;
            followBtn= binding.followBtn;
            followBackBtn= binding.followBackBtn;

            username= binding.username;
            name=binding.name;
            profileDescription=binding.profileDescription;
            followsYou=binding.followsYou;


        }
    }

}
