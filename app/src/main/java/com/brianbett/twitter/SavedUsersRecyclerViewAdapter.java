package com.brianbett.twitter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.brianbett.twitter.databinding.SingleUserAccountBinding;
import com.brianbett.twitter.retrofit.UserDetails;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SavedUsersRecyclerViewAdapter extends RecyclerView.Adapter<SavedUsersRecyclerViewAdapter.MyViewHolder> {
    private final ArrayList<SharedPrefUser> users;
    private final Context context;

    public SavedUsersRecyclerViewAdapter(ArrayList<SharedPrefUser> users, Context context) {
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        SingleUserAccountBinding binding=SingleUserAccountBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.username.setText(users.get(position).getUserDetails().getUsername());
        holder.name.setText(users.get(position).getUserDetails().getName());
        holder.selectAccount.setChecked(users.get(position).isActive());




//        switching current active account when a user clicks it
        holder.itemView.setOnClickListener(view -> {
            holder.selectAccount.setChecked(users.get(position).isActive());
            Gson gson=new Gson();

            String usersList=Preferences.getItemFromSP(context,"usersList");
            Type type = new TypeToken<ArrayList<SharedPrefUser>>() {
            }.getType();
            ArrayList<SharedPrefUser> usersArrayList=gson.fromJson(usersList,type);


            for(SharedPrefUser sharedPrefUser:usersArrayList){
                sharedPrefUser.setActive(false);
                if(sharedPrefUser.getUserDetails().getUserId().equals(users.get(position).getUserDetails().getUserId())){
                    Preferences.saveItemToSP(context,"token", sharedPrefUser.getToken());
                    Preferences.saveItemToSP(context,"userId", sharedPrefUser.getUserDetails().getUserId());
                    sharedPrefUser.setActive(true);
                }else{
                    sharedPrefUser.setActive(false);
                }
            }
            String newUsersListString=gson.toJson(usersArrayList);
            Preferences.saveItemToSP(context,"usersList",newUsersListString);
            context.startActivity(new Intent(context,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            ((Activity)context).finish();
        });
        holder.selectAccount.setOnClickListener(view->{
            Gson gson=new Gson();
            String usersList=Preferences.getItemFromSP(context,"usersList");
            Type type = new TypeToken<ArrayList<SharedPrefUser>>() {
            }.getType();
            ArrayList<SharedPrefUser> usersArrayList=gson.fromJson(usersList,type);


            for(SharedPrefUser sharedPrefUser:usersArrayList){
                if(sharedPrefUser.getUserDetails().getUserId().equals(users.get(position).getUserDetails().getUserId())){
                    Preferences.saveItemToSP(context,"token", sharedPrefUser.getToken());
                    Log.d("userId",sharedPrefUser.getUserDetails().getUserId());
                    Preferences.saveItemToSP(context,"userId", sharedPrefUser.getUserDetails().getUserId());
                    sharedPrefUser.setActive(true);
                }
                else{
                    sharedPrefUser.setActive(false);
                }
            }
            String newUsersListString=gson.toJson(usersArrayList);
            Preferences.saveItemToSP(context,"usersList",newUsersListString);

            context.startActivity(new Intent(context,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        });





        StorageReference storageReference= FirebaseStorage.getInstance().getReference("images/"+users.get(position).getUserDetails().getProfilePic());

        Task<Uri> uriTask=storageReference.getDownloadUrl();
        uriTask.addOnSuccessListener(uri1 -> {
            Glide.with(context).load(uri1).into(holder.profilePic);

        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView profilePic;
        TextView name,username;
        RadioButton selectAccount;
        public MyViewHolder(SingleUserAccountBinding binding) {
            super(binding.getRoot());
            profilePic= binding.profilePic;
            name= binding.name;
            username=binding.username;
            selectAccount= binding.selectAccountButton;

        }
    }
}
