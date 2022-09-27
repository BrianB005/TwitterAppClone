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

public class FollowersRecyclerViewAdapter  extends RecyclerView.Adapter<FollowersRecyclerViewAdapter.MyViewHolder> {

    private final ArrayList<UserDetails> followers;
    private final Context context;

    public FollowersRecyclerViewAdapter(ArrayList<UserDetails> followers, Context context) {
        this.followers = followers;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        SingleFollowerBinding binding=SingleFollowerBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new MyViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        StorageReference storageReference= FirebaseStorage.getInstance().getReference("images/"+followers.get(position).getProfilePic());

        Task<Uri> uriTask=storageReference.getDownloadUrl();

        uriTask.addOnSuccessListener(uri1 -> {
            Glide.with(context).load(uri1).into(holder.profilePic);

        });


        holder.username.setText("@"+followers.get(position).getUsername());
        holder.name.setText(followers.get(position).getName());


        holder.profilePic.setOnClickListener(view->{

                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("user", followers.get(holder.getAdapterPosition()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

        });

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ProfileActivity.class);
            intent.putExtra("user", followers.get(holder.getAdapterPosition()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        String currentUserId=Preferences.getItemFromSP(context,"userId");
        if(followers.get(position).getFollowers().contains(currentUserId)){
            holder.followingBtn.setVisibility(View.VISIBLE);
            holder.followBackBtn.setVisibility(View.GONE);
            holder.followingBtn.setOnClickListener(view -> {
                holder.followingBtn.setEnabled(false);
                holder.followBackBtn.setVisibility(View.VISIBLE);
                holder.followingBtn.setVisibility(View.GONE);
                String userId = followers.get(holder.getAdapterPosition()).getUserId();
                RetrofitHandler.unFollowUser(context, userId, new FollowUserSuccess() {
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
            holder.followingBtn.setVisibility(View.GONE);
            holder.followBackBtn.setVisibility(View.VISIBLE);

            holder.followBackBtn.setOnClickListener(view -> {
                holder.followingBtn.setEnabled(false);
                holder.followingBtn.setVisibility(View.VISIBLE);
                holder.followBackBtn.setVisibility(View.GONE);
                String userId=followers.get(holder.getAdapterPosition()).getUserId();
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
        if(followers.get(position).getUserId().equals(currentUserId)){
            holder.followingBtn.setVisibility(View.GONE);
            holder.followBackBtn.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return followers.size();
    }

    protected static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView profilePic;
        MaterialButton followBackBtn,followingBtn;
        TextView username,name,profileDescription;
        public MyViewHolder(@NonNull SingleFollowerBinding binding) {
            super(binding.getRoot());

            profilePic=binding.profilePic;
            followBackBtn=binding.followBtn;
            followingBtn= binding.followingBtn;
            username= binding.username;
            name=binding.name;
            profileDescription=binding.profileDescription;
        }
    }
}
