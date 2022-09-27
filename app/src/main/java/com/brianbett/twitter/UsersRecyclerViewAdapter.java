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
import com.brianbett.twitter.databinding.SingleUserToFollowBinding;
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

public class UsersRecyclerViewAdapter extends RecyclerView.Adapter<UsersRecyclerViewAdapter.MyViewHolder> {

    private final ArrayList<UserDetails> users;
    private final Context context;

    public UsersRecyclerViewAdapter(ArrayList<UserDetails> users, Context context) {
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        SingleUserToFollowBinding binding=SingleUserToFollowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new MyViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        StorageReference storageReference= FirebaseStorage.getInstance().getReference("images/"+users.get(position).getProfilePic());

        Task<Uri> uriTask=storageReference.getDownloadUrl();

        uriTask.addOnSuccessListener(uri1 -> {
            Glide.with(context).load(uri1).into(holder.profilePic);

        });

        holder.username.setText("@"+users.get(position).getUsername());
        holder.name.setText(users.get(position).getName());


        holder.profilePic.setOnClickListener(view->{

                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("user", users.get(holder.getAdapterPosition()));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

        });

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ProfileActivity.class);
            intent.putExtra("user", users.get(holder.getAdapterPosition()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });


        String currentUserId=Preferences.getItemFromSP(context,"userId");


        if(users.get(position).getFollowing().contains(currentUserId)){


            if(users.get(position).getFollowers().contains(currentUserId)){
                holder.followBtn.setVisibility(View.GONE);
                holder.followBackBtn.setVisibility(View.GONE);
                holder.followingBtn.setVisibility(View.VISIBLE);
            }else{
                holder.followBtn.setVisibility(View.GONE);
                holder.followingBtn.setVisibility(View.GONE);
                holder.followBackBtn.setVisibility(View.VISIBLE);
            }



        }else{
            if(users.get(position).getFollowers().contains(currentUserId)){
                holder.followBtn.setVisibility(View.GONE);
                holder.followingBtn.setVisibility(View.GONE);
                holder.followBackBtn.setVisibility(View.VISIBLE);
            }else{
                holder.followBtn.setVisibility(View.VISIBLE);
                holder.followingBtn.setVisibility(View.GONE);
                holder.followBackBtn.setVisibility(View.GONE);
            }
        }

        holder.followBtn.setOnClickListener(view1 -> {
            holder.followBtn.setVisibility(View.GONE);
            holder.followingBtn.setVisibility(View.VISIBLE);
            holder.followingBtn.setEnabled(false);
            RetrofitHandler.followUser(context, users.get(position).getUserId(), new FollowUserSuccess() {
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
        holder.followBackBtn.setOnClickListener(view1 -> {
            holder.followingBtn.setEnabled(false);
            holder.followBackBtn.setVisibility(View.GONE);
            holder.followingBtn.setVisibility(View.VISIBLE);
            RetrofitHandler.followUser(context, users.get(position).getUserId(), new FollowUserSuccess() {
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


        holder.followingBtn.setOnClickListener(view -> {
            holder.followingBtn.setVisibility(View.GONE);
            String userId=users.get(position).getUserId();
            RetrofitHandler.unFollowUser(context, userId, new FollowUserSuccess() {
                @Override
                public void success(String message) {


                }

                @Override
                public void errorExists() {


                }

                @Override
                public void failure(Throwable throwable) {


                }
            });

        });



    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    protected static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView profilePic;
        MaterialButton followBackBtn,followBtn,followingBtn;
        TextView username,name,profileDescription,followsYou;
        public MyViewHolder(@NonNull SingleUserToFollowBinding binding) {
            super(binding.getRoot());
            profilePic=binding.profilePic;
            followBtn=binding.followBtn;
            followBackBtn=binding.followBackBtn;
            username= binding.username;
            name=binding.name;
            profileDescription=binding.profileDescription;
            followsYou=binding.followsYou;
            followingBtn=binding.followingBtn;
        }
    }
}
